package com.imrohansoni.chess

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.PieceType
import com.imrohansoni.chess.models.PieceView
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.opposite
import com.imrohansoni.chess.pieces.Bishop
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Knight
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.pieces.Piece
import com.imrohansoni.chess.pieces.Queen
import com.imrohansoni.chess.pieces.Rook
import com.imrohansoni.chess.utils.CanvasInvalidator
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.boardState
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.darkKingPosition
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.files
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.lightKingPosition
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.ranks
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider
import com.imrohansoni.chess.utils.Constants.boardRange
import com.imrohansoni.chess.utils.SquareManager
import com.imrohansoni.chess.utils.animatePieceMovement
import kotlin.math.abs


data class CastlingRight(var kingSide: Boolean, var queenSide: Boolean)

class ChessBoardController(
    private val context: Context,
    private val invalidator: CanvasInvalidator,
    private val onPawnPromotion: (color: Color, onPieceSelected: (PieceType) -> Unit) -> Unit
) {
    private val squareManager = SquareManager(context)
    private var boardManager = ChessBoardManager()

    private val squareSize = squareManager.squareSize
    private val pieceBitmaps = ChessPieceBitmapProvider.getPieceBitmaps(context, squareSize)

    val pieceViews = mutableListOf<PieceView>()

    init {
        initializePieceViews()
    }

    private fun initializePieceViews() {
        for (row in boardRange) {
            for (col in boardRange) {
                boardManager.getPiece(Position(row, col))?.let {
                    pieceBitmaps[it.fen]?.let { bitmap ->
                        val x = (squareSize * row).toFloat()
                        val y = (squareSize * col).toFloat()
                        val pieceView = PieceView(bitmap, Position(row, col), x, y)
                        pieceViews.add(pieceView)
                    }
                }
            }
        }
    }

    fun selectSquare(position: Position) {
        if (!GameState.redoStack.empty()) return
        squareManager.resetSelectedSquare()

        GameState.selectedSquarePosition = position

        val selectedPiece = boardManager.getPiece(position) ?: return
        if (selectedPiece.color != GameState.currentPlayerColor) return

        var possibleSquares =
            selectedPiece.calculatePossibleMoves(position, boardState).toMutableList()

        squareManager.addSquare(position, isSelected = true)

        possibleSquares = possibleSquares.filter {
            isSafeSquare(position, it)
        }.toMutableList()

        if (selectedPiece is King) {
            if (canKingPerformCastle(selectedPiece).kingSide) {
                possibleSquares.add(notationToPosition(if (selectedPiece.color == Color.LIGHT) "g1" else "g8"))
            }

            if (canKingPerformCastle(selectedPiece).queenSide) {
                possibleSquares.add(notationToPosition(if (selectedPiece.color == Color.LIGHT) "c1" else "c8"))
            }
        }

        if (selectedPiece is Pawn) {
            val enPassantPosition = canCaptureEnPassant(selectedPiece, position)

            enPassantPosition?.let {
                possibleSquares.add(it)
                squareManager.addSquare(it, canBeCaptured = true)
            }
        }

        GameState.safeSquares = possibleSquares

        GameState.safeSquares.forEach {
            val safeSquare = squareManager.addSquare(it, isSafeSquare = true)
            if (boardManager.getPiece(it) != null) {
                safeSquare.canBeCaptured = true
            }
        }

        invalidator.canvasInvalidator()
    }

    fun canSelectedPieceMove(): Boolean {
        GameState.selectedSquarePosition?.let {
            val piece = boardManager.getPiece(it)
            return piece?.color == GameState.currentPlayerColor
        }
        return false
    }

    fun movePiece(currentPosition: Position, finalPosition: Position): Move? {
        val currentPiece = boardManager.getPiece(currentPosition) ?: return null
        val pieceView = pieceViews.find { piece -> piece.currentPosition == currentPosition }

        if (pieceView == null) return null

        if (currentPiece is Pawn) {
            if (!currentPiece.moved) currentPiece.moved = true

            if (currentPosition.col != finalPosition.col && boardManager.getPiece(finalPosition) == null) {
                val captureEnPassantPosition = Position(currentPosition.row, finalPosition.col)

                pieceViews.removeIf { pieceViews -> pieceViews.currentPosition == captureEnPassantPosition }
                boardManager.setPiece(captureEnPassantPosition, null)
            }

            if (finalPosition.row == 7 || finalPosition.row == 0) {
                onPawnPromotion.invoke(currentPiece.color) { pieceType ->
                    Toast.makeText(context, "piece type $pieceType", Toast.LENGTH_SHORT).show()
                    handlePieceMove(
                        currentPosition, finalPosition, currentPiece, pieceView, pieceType
                    )
                }
                return@movePiece null
            }
        }

        if (currentPiece is King) {
            if (!currentPiece.moved) currentPiece.moved = true
            if (abs(finalPosition.col - currentPosition.col) == 2) {
                performCastle(currentPiece, finalPosition)
            }
            when (GameState.currentPlayerColor) {
                Color.LIGHT -> lightKingPosition = positionToNotation(finalPosition)
                Color.DARK -> darkKingPosition = positionToNotation(finalPosition)
            }
        }

        if (currentPiece is Rook && !currentPiece.moved) currentPiece.moved = true

        val move = handlePieceMove(
            currentPosition, finalPosition, currentPiece, pieceView, null
        )

        return move
    }

    private fun handlePieceMove(
        currentPosition: Position,
        finalPosition: Position,
        piece: Piece,
        selectedPieceView: PieceView,
        promotedPieceType: PieceType?
    ): Move {

        val capturedPieceView = pieceViews.find { piece -> piece.currentPosition == finalPosition }

        capturedPieceView?.let {
            pieceViews.remove(it)
        }

        updatePieceLocation(piece, selectedPieceView, currentPosition, finalPosition)

        squareManager.resetSquareFlags()
        squareManager.resetCheckSquareFlag()

        squareManager.addSquare(currentPosition, lastMove = true)
        squareManager.addSquare(finalPosition, lastMove = true)

        promotedPieceType?.let {
            val (promotedPiece, promotedPieceBitmap) = when (it) {
                PieceType.KNIGHT -> {
                    listOf(
                        Knight(piece.color),
                        if (piece.color == Color.LIGHT) pieceBitmaps["N"] else pieceBitmaps["N"]
                    )
                }

                PieceType.BISHOP -> {
                    listOf(
                        Bishop(piece.color),
                        if (piece.color == Color.LIGHT) pieceBitmaps["B"] else pieceBitmaps["b"]
                    )
                }

                PieceType.ROOK -> {
                    listOf(
                        Rook(piece.color),
                        if (piece.color == Color.LIGHT) pieceBitmaps["R"] else pieceBitmaps["r"]
                    )
                }

                PieceType.QUEEN -> {
                    listOf(
                        Queen(piece.color),
                        if (piece.color == Color.LIGHT) pieceBitmaps["Q"] else pieceBitmaps["q"]
                    )
                }

                else -> {
                    listOf(null, null)
                }
            }

            if (promotedPieceBitmap is Bitmap && promotedPiece is Piece) {
                val x = (squareSize * finalPosition.row).toFloat()
                val y = (squareSize * finalPosition.col).toFloat()
                pieceViews.removeIf { it.currentPosition == finalPosition }
                pieceViews.add(PieceView(promotedPieceBitmap, finalPosition, x, y))
                boardManager.setPiece(finalPosition, promotedPiece)

            }
        }

        val move = Move(
            algebraicNotation = positionToNotation(finalPosition),
            piece = piece,
            pieceView = selectedPieceView,
            capturedPieceView = capturedPieceView,
            startingPosition = currentPosition,
            finalPosition = finalPosition,
        )

        GameState.undoStack.push(move)

        val currentPlayerColor = GameState.currentPlayerColor

        val kingPosition = findKingPosition(currentPlayerColor.opposite())

        if (isKingInCheck(currentPlayerColor.opposite())) {
            squareManager.addSquare(kingPosition, isChecked = true)
            if (!isSafeSquareAvailable(currentPlayerColor.opposite())) {
                Log.d("CHESS_BOARD_GAME", "**************** ITS CHECKMATE ******************")
            }
        } else {
            if (!isSafeSquareAvailable(currentPlayerColor.opposite())) {
                Log.d("CHESS_BOARD_GAME", "**********  ITS STALEMATE ***********")
            }
        }

        GameState.currentPlayerColor = currentPlayerColor.opposite()
        GameState.safeSquares = listOf()
        GameState.selectedSquarePosition = null

        invalidator.canvasInvalidator()
        return move
    }

    private fun updatePieceLocation(
        piece: Piece, pieceView: PieceView, currentPosition: Position, finalPosition: Position
    ) {
        animatePieceMovement(pieceView, finalPosition, squareSize, invalidator)

        pieceView.currentPosition = finalPosition
        boardManager.setPiece(currentPosition, null)
        boardManager.setPiece(finalPosition, piece)
    }


    private fun canKingPerformCastle(king: King): CastlingRight {
        if (king.moved || isKingInCheck(king.color)) {
            return CastlingRight(kingSide = false, queenSide = false)
        }

        val castlingRight = CastlingRight(kingSide = true, queenSide = true)

        val (rookKingSidePosition, rookQueenSidePosition) = if (king.color == Color.LIGHT) {
            Pair(notationToPosition("h1"), notationToPosition("a1"))
        } else {
            Pair(notationToPosition("h8"), notationToPosition("a8"))
        }

        val rookKingSide = boardManager.getPiece(rookKingSidePosition)
        val rookQueenSide = boardManager.getPiece(rookQueenSidePosition)

        if (rookKingSide !is Rook || rookKingSide.color != king.color || rookKingSide.moved) {
            castlingRight.kingSide = false
        }

        if (rookQueenSide !is Rook || rookQueenSide.color != king.color || rookQueenSide.moved) {
            castlingRight.queenSide = false
        }

        if (!castlingRight.kingSide && !castlingRight.queenSide) return castlingRight

        val kingSidePieces =
            if (king.color == Color.LIGHT) arrayOf("f1", "g1") else arrayOf("f8", "g8")
        val queenSidePieces =
            if (king.color == Color.LIGHT) arrayOf("b1", "c1", "d1") else arrayOf("b8", "c8", "d8")

        for (piece in kingSidePieces) {
            if (boardManager.getPiece(notationToPosition(piece)) != null) {
                castlingRight.kingSide = false
                break
            }
        }

        for (piece in queenSidePieces) {
            if (boardManager.getPiece(notationToPosition(piece)) != null) {
                castlingRight.queenSide = false
                break
            }
        }

        if (!castlingRight.kingSide && !castlingRight.queenSide) return castlingRight

        val kingPosition = findKingPosition(king.color)

        for (position in kingSidePieces) {
            if (!isSafeSquare(kingPosition, notationToPosition(position))) {
                castlingRight.kingSide = false
                break
            }
        }

        for (position in queenSidePieces) {
            if (!isSafeSquare(kingPosition, notationToPosition(position))) {
                castlingRight.queenSide = false
                break
            }
        }

        return castlingRight
    }

    private fun performCastle(king: King, kingFinalPosition: Position) {
        val (rookCurrent, rookFinal) = when (positionToNotation(kingFinalPosition)) {
            "g1" -> Pair("h1", "f1")
            "c1" -> Pair("a1", "d1")
            "c8" -> Pair("a8", "d8")
            "g8" -> Pair("h8", "f8")
            else -> return
        }

        val rookCurrentPosition = notationToPosition(rookCurrent)
        val rookFinalPosition = notationToPosition(rookFinal)

        val rook = boardManager.getPiece(rookCurrentPosition)

        if (rook is Rook && !rook.moved && rook.color == king.color) {
            rook.moved = true
            val rookPieceView = pieceViews.find { it.currentPosition == rookCurrentPosition }

            rookPieceView?.let {
                updatePieceLocation(rook, it, rookCurrentPosition, rookFinalPosition)
            } ?: return
        }
    }

    private fun isKingInCheck(kingColor: Color): Boolean {
        val kingPosition = findKingPosition(kingColor)

        for (row in boardRange) {
            for (col in boardRange) {
                val piece = boardManager.getPiece(Position(row, col))

                if (piece != null && piece.color == kingColor.opposite() && piece.pieceType != PieceType.KING) {
                    val possibleMoves = piece.calculatePossibleMoves(Position(row, col), boardState)
                    if (possibleMoves.contains(kingPosition)) {
                        Log.d("CHESS_BOARD_GAME", "***** KING IS IN CHECK ********")
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun findKingPosition(color: Color): Position {
        return notationToPosition(if (color == Color.LIGHT) lightKingPosition else darkKingPosition)
    }

    private fun isSafeSquare(currentPosition: Position, finalPosition: Position): Boolean {
        var isValidMove = false

        val currentPiece = boardManager.getPiece(currentPosition) ?: return false
        val capturedPiece = boardManager.getPiece(finalPosition)

        // changing the position temporarily
        boardManager.setPiece(finalPosition, currentPiece)
        boardManager.setPiece(currentPosition, null)

        if (currentPiece is King) {
            if (currentPiece.color == Color.LIGHT) {
                lightKingPosition = positionToNotation(finalPosition)
            } else {
                darkKingPosition = positionToNotation(finalPosition)
            }
        }

        if (!isKingInCheck(currentPiece.color)) {
            isValidMove = true
        }

        // restoring the position
        boardManager.setPiece(finalPosition, capturedPiece)
        boardManager.setPiece(currentPosition, currentPiece)

        if (currentPiece is King) {
            if (currentPiece.color == Color.LIGHT) {
                lightKingPosition = positionToNotation(currentPosition)
            } else {
                darkKingPosition = positionToNotation(currentPosition)
            }
        }

        return isValidMove
    }

    private fun notationToPosition(notation: String): Position {
        val col = files.indexOf(notation[0])
        val row = ranks.indexOf(notation[1])
        return Position(row, col)
    }

    private fun positionToNotation(position: Position): String {
        return "${files[position.col]}${ranks[position.row]}"
    }

    private fun isSafeSquareAvailable(kingColor: Color): Boolean {
        for (row in boardRange) {
            for (col in boardRange) {
                val currentPosition = Position(row, col)
                val piece = boardManager.getPiece(currentPosition)

                if (piece == null || piece.color != kingColor) continue

                val possibleMoves = piece.calculatePossibleMoves(currentPosition, boardState)

                possibleMoves.forEach { finalPosition ->
                    if (isSafeSquare(currentPosition, finalPosition)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun moveToPreviousMove() {
        if (GameState.undoStack.empty()) return

        val lastMove = GameState.undoStack.pop()
        GameState.redoStack.push(lastMove)

        lastMove.capturedPieceView?.let {
            pieceViews.add(it)
        }

        squareManager.resetSelectedSquare()

        if (!GameState.undoStack.empty()) {
            val move = GameState.undoStack.last()

            squareManager.addSquare(move.startingPosition, lastMove = true)
            squareManager.addSquare(move.finalPosition, lastMove = true)
        }

        invalidator.canvasInvalidator()

        animatePieceMovement(
            lastMove.pieceView, lastMove.startingPosition, squareSize, invalidator, 100L
        )
    }

    fun moveToNextMove() {
        if (GameState.redoStack.empty()) return

        val move = GameState.redoStack.pop()

        GameState.undoStack.push(move)

        squareManager.resetSelectedSquare()

        squareManager.addSquare(move.startingPosition, lastMove = true)
        squareManager.addSquare(move.finalPosition, lastMove = true)

        move.capturedPieceView?.let {
            pieceViews.remove(it)
        }

        invalidator.canvasInvalidator()

        animatePieceMovement(
            move.pieceView, move.finalPosition, squareSize, invalidator, 100L
        )
    }

    private fun canCaptureEnPassant(
        pawn: Piece, pawnPosition: Position
    ): Position? {
        if (GameState.undoStack.isEmpty()) return null
        val lastMove = GameState.undoStack.last()

        if (pawn is Pawn && lastMove.piece is Pawn && lastMove.piece.color != pawn.color) {
            if (abs(lastMove.startingPosition.row - lastMove.finalPosition.row) == 2 && abs(
                    pawnPosition.col - lastMove.finalPosition.col
                ) == 1 && pawnPosition.row == lastMove.finalPosition.row
            ) {
                val row = if (ChessBoardManager.primaryPlayerColor == Color.LIGHT) {
                    if (pawn.color == Color.LIGHT) -1 else 1
                } else {
                    if (pawn.color == Color.LIGHT) 1 else -1
                }
                val capturePosition = Position(pawnPosition.row + row, lastMove.finalPosition.col)
                if (boardManager.getPiece(capturePosition) != null) return null
                if (isSafeSquare(pawnPosition, capturePosition)) {
                    return capturePosition
                }
            }
        }
        return null
    }

    fun resetSelection() {
        squareManager.resetSelectedSquare()
        GameState.selectedSquarePosition = null
        GameState.safeSquares = listOf()
        invalidator.canvasInvalidator()
    }
}