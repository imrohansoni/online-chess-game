package com.imrohansoni.chess

import android.content.Context
import android.util.Log
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.PieceView
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.opposite
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.pieces.Piece
import com.imrohansoni.chess.pieces.Rook
import com.imrohansoni.chess.utils.CanvasInvalidator
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider
import com.imrohansoni.chess.utils.Constants.boardRange
import com.imrohansoni.chess.utils.SquareManager
import com.imrohansoni.chess.utils.animatePieceMovement
import kotlin.math.abs


data class CastlingRight(var kingSide: Boolean, var queenSide: Boolean)

class ChessBoardController(
    context: Context, private val invalidator: CanvasInvalidator
) {
    private val squareManager = SquareManager(context)
    private var chessBoardManager = ChessBoardManager()

    private val squareSize = squareManager.squareSize
    private val pieceBitmaps = ChessPieceBitmapProvider.getPieceBitmaps(context, squareSize)

    val pieceViews = mutableListOf<PieceView>()

    init {
        initializePieceViews()
    }

    private fun initializePieceViews() {
        for (row in boardRange) {
            for (col in boardRange) {
                chessBoardManager.getPiece(Position(row, col))?.let {
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

    fun canMovePiece(): Boolean {
        GameState.selectedSquarePosition?.let {
            val piece = chessBoardManager.getPiece(it)
            return piece?.color == GameState.currentPlayerColor
        }
        return false
    }

    private fun updatePieceLocation(
        piece: Piece,
        pieceView: PieceView,
        currentPosition: Position,
        finalPosition: Position
    ) {

        animatePieceMovement(
            pieceView, finalPosition, squareSize, invalidator
        )

        pieceView.currentPosition = finalPosition

        chessBoardManager.setPiece(currentPosition, null)
        chessBoardManager.setPiece(finalPosition, piece)
    }

    private fun performCastle(king: King, kingFinalPosition: Position) {
        val kingPosition = chessBoardManager.positionToNotation(kingFinalPosition)
        val (rookStart, rookEnd) = when (kingPosition) {
            "g1" -> "h1" to "f1"
            "c1" -> "a1" to "d1"
            "c8" -> "a8" to "d8"
            "g8" -> "h8" to "f8"
            else -> return
        }

        val rookStartPosition = chessBoardManager.notationToPosition(rookStart)
        val rookFinalPosition = chessBoardManager.notationToPosition(rookEnd)
        val rook = chessBoardManager.getPiece(rookStartPosition)

        if (rook is Rook && !rook.moved && rook.color == king.color) {
            rook.moved = true
            val rookPieceView = pieceViews.find { it.currentPosition == rookStartPosition }
            if (rookPieceView != null) {
                updatePieceLocation(rook, rookPieceView, rookStartPosition, rookFinalPosition)
            }
        }
    }


    fun movePiece(currentPosition: Position, finalPosition: Position): Move? {
        val currentPiece = chessBoardManager.getPiece(currentPosition) ?: return null
        val pieceView = pieceViews.find { piece -> piece.currentPosition == currentPosition }

        if (pieceView == null) return null

        val capturedPieceView = pieceViews.find { piece -> piece.currentPosition == finalPosition }

        capturedPieceView?.let {
            pieceViews.remove(it)
        }

        updatePieceLocation(currentPiece, pieceView, currentPosition, finalPosition)

        squareManager.resetSquareFlags()
        squareManager.resetCheckSquareFlag()

        squareManager.addSquare(currentPosition, lastMove = true)
        squareManager.addSquare(finalPosition, lastMove = true)

        if (currentPiece is Pawn) currentPiece.moved = true
        if (currentPiece is King) {
            currentPiece.moved = true
            if (abs(finalPosition.col - currentPosition.col) == 2) {
                performCastle(currentPiece, finalPosition)
            }
            when (GameState.currentPlayerColor) {
                Color.LIGHT -> ChessBoardManager.lightKing =
                    chessBoardManager.positionToNotation(finalPosition)

                Color.DARK -> ChessBoardManager.darkKing =
                    chessBoardManager.positionToNotation(finalPosition)
            }
        }
        if (currentPiece is Rook) currentPiece.moved = true

        val move = Move(
            startingPosition = currentPosition,
            finalPosition = finalPosition,
            capturedPieceColor = if (GameState.currentPlayerColor == Color.LIGHT) Color.DARK else Color.LIGHT,
            algebraicNotation = chessBoardManager.positionToNotation(finalPosition),
            pieceView = pieceView,
            fen = currentPiece.fen,
            capturePiece = capturedPieceView
        )

        GameState.undoStack.push(move)

        val currentPlayerColor = GameState.currentPlayerColor
        GameState.currentPlayerColor = currentPlayerColor.opposite()
        val kingPosition = chessBoardManager.findKingPosition(currentPlayerColor)

        if (chessBoardManager.isKingInCheck(currentPlayerColor)) {
            squareManager.addSquare(kingPosition, isChecked = true)
            isCheckmate()
        }

        GameState.safeSquares = listOf()
        GameState.selectedSquarePosition = null

        invalidator.canvasInvalidator()
        return move
    }

    private fun isCheckmate() {
        for (row in boardRange) {
            for (col in boardRange) {
                val piece = ChessBoardManager.boardState[row][col]
                if (piece != null && piece.color == GameState.currentPlayerColor) {
                    val possibleMoves = piece.calculatePossibleMoves(
                        Position(row, col), ChessBoardManager.boardState
                    )
                    possibleMoves.forEach {
                        if (chessBoardManager.isSafeSquare(Position(row, col), it)) {
                            return
                        }
                    }
                }
            }
        }
        Log.d("CHESS_BOARD_VIEW", "its checkmate")
    }

    fun moveToPreviousMove() {
        if (GameState.undoStack.empty()) return

        val lastMove = GameState.undoStack.pop()
        GameState.redoStack.push(lastMove)

        lastMove.capturePiece?.let {
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

        move.capturePiece?.let {
            pieceViews.remove(it)
        }

        invalidator.canvasInvalidator()

        animatePieceMovement(
            move.pieceView, move.finalPosition, squareSize, invalidator, 100L
        )
    }

    fun selectSquare(position: Position) {
        if (!GameState.redoStack.empty()) return
        squareManager.resetSelectedSquare()

        GameState.selectedSquarePosition = position

        val selectedPiece = chessBoardManager.getPiece(position) ?: return
        if (selectedPiece.color != GameState.currentPlayerColor) return

        var possibleSquares = selectedPiece
            .calculatePossibleMoves(position, ChessBoardManager.boardState)
            .toMutableList()


        squareManager.addSquare(position, isSelected = true)

        possibleSquares = possibleSquares.filter {
            chessBoardManager.isSafeSquare(position, it)
        }.toMutableList()

        if (selectedPiece is King) {
            if (chessBoardManager.canCastle(selectedPiece).kingSide) {
                possibleSquares.add(chessBoardManager.notationToPosition(if (selectedPiece.color == Color.LIGHT) "g1" else "g8"))
            }

            if (chessBoardManager.canCastle(selectedPiece).queenSide) {
                possibleSquares.add(chessBoardManager.notationToPosition(if (selectedPiece.color == Color.LIGHT) "c1" else "c8"))
            }
        }

        GameState.safeSquares = possibleSquares

        GameState.safeSquares.forEach {
            val safeSquare = squareManager.addSquare(it, isSafeSquare = true)
            if (chessBoardManager.getPiece(it) != null) {
                safeSquare.canBeCaptured = true
            }
        }

        invalidator.canvasInvalidator()
    }

    fun resetSelection() {
        squareManager.resetSelectedSquare()
        GameState.selectedSquarePosition = null
        GameState.safeSquares = listOf()
        invalidator.canvasInvalidator()
    }
}