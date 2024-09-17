package com.imrohansoni.chess

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.imrohansoni.chess.models.Castle
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Draw
import com.imrohansoni.chess.models.DrawType
import com.imrohansoni.chess.models.EnPassant
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.PawnPromotion
import com.imrohansoni.chess.models.PieceView
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.SpecialMove
import com.imrohansoni.chess.models.Type
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
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.boardMatrix
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.darkKingPosition
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.files
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.lightKingPosition
import com.imrohansoni.chess.utils.ChessBoardManager.Companion.ranks
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider
import com.imrohansoni.chess.utils.Constant.boardRange
import com.imrohansoni.chess.utils.SquareManager
import com.imrohansoni.chess.utils.animatePieceMovement
import com.imrohansoni.chess.utils.findAvailableSquares
import kotlin.math.abs


data class CastlingRight(var kingSide: Boolean, var queenSide: Boolean)

data class CapturedPiece(
    val capturedPiece: Piece,
    val capturedPieceView: PieceView
)

class ChessBoardController(
    private val context: Context,
    private val invalidator: CanvasInvalidator,
    private val chessBoardHandler: ChessBoardHandler
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
                val position = Position(row, col)
                boardManager.getPiece(position)?.let {
                    pieceBitmaps[it.fen]?.let { bitmap ->
                        val (x, y) = getCoordinates(position)
                        val pieceView = PieceView(bitmap, position, x, y)
                        pieceViews.add(pieceView)
                    }
                }
            }
        }
    }

    fun selectSquare(position: Position) {
        if (GameManager.gameState == GameState.FINISHED) return
        if (!GameManager.redoStack.empty()) return
        squareManager.resetSquareHighlights()

        GameManager.selectedSquarePosition = position

        val selectedPiece = boardManager.getPiece(position) ?: return
        if (selectedPiece.color != GameManager.currentPlayerColor) return

        var possibleSquares = findAvailableSquares(selectedPiece, position, boardMatrix)

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

        GameManager.safeSquares = possibleSquares

        GameManager.safeSquares.forEach {
            val safeSquare = squareManager.addSquare(it, isSafeSquare = true)
            if (boardManager.getPiece(it) != null) {
                safeSquare.canBeCaptured = true
            }
        }

        invalidator.invalidateCanvas()
    }

    fun canSelectedPieceMove(): Boolean {
        GameManager.selectedSquarePosition?.let {
            val piece = boardManager.getPiece(it)
            return piece?.color == GameManager.currentPlayerColor
        }
        return false
    }

    fun movePiece(currentPosition: Position, finalPosition: Position) {
        val currentPiece = boardManager.getPiece(currentPosition) ?: return
        val pieceView = pieceViews.find { piece -> piece.currentPosition == currentPosition }

        if (pieceView == null) return

        var specialMove: SpecialMove? = null

        GameManager.fiftyRuleCounter++

        if (currentPiece is Pawn) {
            GameManager.fiftyRuleCounter = 0
            if (!currentPiece.moved) currentPiece.moved = true

            if (currentPosition.col != finalPosition.col && boardManager.getPiece(finalPosition) == null) {
                val captureEnPassantPosition = Position(currentPosition.row, finalPosition.col)
                val capturePieceView =
                    pieceViews.find { pieceViews -> pieceViews.currentPosition == captureEnPassantPosition }
                capturePieceView?.let {
                    pieceViews.remove(it)
                    boardManager.setPiece(captureEnPassantPosition, null)
                    specialMove = EnPassant(it)
                }
            }

            if (finalPosition.row == 7 || finalPosition.row == 0) {
                chessBoardHandler.pawnPromotionHandler(currentPiece.color) { pieceType ->
                    // this callback will be called when user selects the promotion piece
                    getPromotedPiece(pieceType, currentPiece.color)?.let { (piece, bitmap) ->
                        val (x, y) = getCoordinates(finalPosition)
                        val promotedPieceView = PieceView(bitmap, finalPosition, x, y)
                        specialMove = PawnPromotion(piece, promotedPieceView)
                    }

                    handlePieceMove(
                        currentPosition, finalPosition, currentPiece, pieceView, specialMove
                    )
                }
                return
            }
        }

        if (currentPiece is King) {
            if (!currentPiece.moved) currentPiece.moved = true
            if (abs(finalPosition.col - currentPosition.col) == 2) {
                performCastle(
                    currentPiece,
                    finalPosition
                )?.let { (rookCurrentPosition, rookFinalPosition, rookPieceView) ->
                    specialMove = Castle(rookPieceView, rookCurrentPosition, rookFinalPosition)
                }
            }

            when (GameManager.currentPlayerColor) {
                Color.LIGHT -> lightKingPosition = positionToNotation(finalPosition)
                Color.DARK -> darkKingPosition = positionToNotation(finalPosition)
            }
        }

        if (currentPiece is Rook && !currentPiece.moved) currentPiece.moved = true


        if (GameManager.fiftyRuleCounter >= 50) {
            specialMove = Draw(DrawType.FIFTY_MOVE_RULE)
            GameManager.gameState = GameState.FINISHED
        }

        handlePieceMove(currentPosition, finalPosition, currentPiece, pieceView, specialMove)
    }

    private fun getCoordinates(position: Position): Pair<Float, Float> {
        val x = (squareSize * position.row).toFloat()
        val y = (squareSize * position.col).toFloat()
        return Pair(x, y)
    }

    private fun handlePieceMove(
        currentPosition: Position,
        finalPosition: Position,
        piece: Piece,
        selectedPieceView: PieceView,
        specialMove: SpecialMove?
    ){

        val capturedPieceView = pieceViews.find { piece -> piece.currentPosition == finalPosition }

        capturedPieceView?.let {
            pieceViews.remove(it)
            GameManager.fiftyRuleCounter = 0
        }

        updatePieceLocation(piece, selectedPieceView, currentPosition, finalPosition)

        if (specialMove is PawnPromotion) {
            val promotedPieceView = specialMove.promotedPieceView
            val promotedPiece = specialMove.piece

            boardManager.setPiece(finalPosition, promotedPiece)

            pieceViews.removeIf { pieceView -> pieceView.currentPosition == finalPosition }
            pieceViews.add(promotedPieceView)
        }

        squareManager.resetSquares()

        squareManager.addSquare(currentPosition, lastMove = true)
        squareManager.addSquare(finalPosition, lastMove = true)

        val move = Move(
            algebraicNotation = positionToNotation(finalPosition),
            piece = piece,
            pieceView = selectedPieceView,
            capturedPieceView = capturedPieceView,
            startingPosition = currentPosition,
            finalPosition = finalPosition,
            specialMove = specialMove
        )

        GameManager.undoStack.push(move)
        chessBoardHandler.pieceMoveHandler(move)

        val currentPlayerColor = GameManager.currentPlayerColor

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

        GameManager.currentPlayerColor = currentPlayerColor.opposite()
        GameManager.safeSquares = listOf()
        GameManager.selectedSquarePosition = null

        invalidator.invalidateCanvas()
    }

    private fun getPromotedPiece(
        promotedPieceType: Type,
        pieceColor: Color
    ): Pair<Piece, Bitmap>? {
        val (promotedPiece, promotedPieceBitmap) = when (promotedPieceType) {
            Type.KNIGHT -> {
                listOf(
                    Knight(pieceColor),
                    if (pieceColor == Color.LIGHT) pieceBitmaps["N"] else pieceBitmaps["N"]
                )
            }

            Type.BISHOP -> {
                listOf(
                    Bishop(pieceColor),
                    if (pieceColor == Color.LIGHT) pieceBitmaps["B"] else pieceBitmaps["b"]
                )
            }

            Type.ROOK -> {
                listOf(
                    Rook(pieceColor),
                    if (pieceColor == Color.LIGHT) pieceBitmaps["R"] else pieceBitmaps["r"]
                )
            }

            Type.QUEEN -> {
                listOf(
                    Queen(pieceColor),
                    if (pieceColor == Color.LIGHT) pieceBitmaps["Q"] else pieceBitmaps["q"]
                )
            }

            else -> {
                return null
            }
        }
        return if (promotedPiece is Piece && promotedPieceBitmap is Bitmap) {
            Pair(promotedPiece, promotedPieceBitmap)
        } else {
            null
        }
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

    private fun performCastle(
        king: King,
        kingFinalPosition: Position
    ): Triple<Position, Position, PieceView>? {
        val (rookCurrent, rookFinal) = when (positionToNotation(kingFinalPosition)) {
            "g1" -> Pair("h1", "f1")
            "c1" -> Pair("a1", "d1")
            "c8" -> Pair("a8", "d8")
            "g8" -> Pair("h8", "f8")
            else -> return null
        }

        val rookCurrentPosition = notationToPosition(rookCurrent)
        val rookFinalPosition = notationToPosition(rookFinal)

        val rook = boardManager.getPiece(rookCurrentPosition)

        if (rook is Rook && !rook.moved && rook.color == king.color) {
            rook.moved = true
            val rookPieceView = pieceViews.find { it.currentPosition == rookCurrentPosition }

            rookPieceView?.let {
                updatePieceLocation(rook, it, rookCurrentPosition, rookFinalPosition)
                return Triple(rookCurrentPosition, rookFinalPosition, rookPieceView)
            }
        }
        return null
    }

    private fun isKingInCheck(kingColor: Color): Boolean {
        val kingPosition = findKingPosition(kingColor)

        for (row in boardRange) {
            for (col in boardRange) {
                val piece = boardManager.getPiece(Position(row, col))

                if (piece != null && piece.color == kingColor.opposite()) {
                    val possibleSquares =
                        findAvailableSquares(piece, Position(row, col), boardMatrix)
                    if (possibleSquares.contains(kingPosition)) {
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

                val possibleSquares = findAvailableSquares(piece, currentPosition, boardMatrix)

                possibleSquares.forEach { finalPosition ->
                    if (isSafeSquare(currentPosition, finalPosition)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun moveToPreviousMove() {
        if (GameManager.undoStack.empty()) return

        val lastMove = GameManager.undoStack.pop()
        GameManager.redoStack.push(lastMove)

        lastMove.capturedPieceView?.let { capturePieceView ->
            pieceViews.add(capturePieceView)
        }

        lastMove.specialMove?.let { specialMove ->
            when (specialMove) {
                is Castle -> {
                    animatePieceMovement(
                        specialMove.rookPieceView,
                        specialMove.rookInitialPosition,
                        squareSize,
                        invalidator,
                        100L
                    )
                }

                is EnPassant -> {
                    pieceViews.add(specialMove.enPassantPiece)
                }

                is PawnPromotion -> {
                    pieceViews.remove(specialMove.promotedPieceView)
                    pieceViews.add(lastMove.pieceView)
                }

                else -> null
            }
        }

        squareManager.resetSquares()

        if (!GameManager.undoStack.empty()) {
            val move = GameManager.undoStack.last()
            squareManager.addSquare(move.startingPosition, lastMove = true)
            squareManager.addSquare(move.finalPosition, lastMove = true)
        }

        invalidator.invalidateCanvas()

        animatePieceMovement(
            lastMove.pieceView, lastMove.startingPosition, squareSize, invalidator, 100L
        )
    }

    fun moveToNextMove() {
        if (GameManager.redoStack.empty()) return

        val move = GameManager.redoStack.pop()

        GameManager.undoStack.push(move)

        move.specialMove?.let { specialMove ->
            when (specialMove) {
                is Castle -> {
                    animatePieceMovement(
                        specialMove.rookPieceView,
                        specialMove.rookFinalPosition,
                        squareSize,
                        invalidator,
                        100L
                    )
                }

                is EnPassant -> {
                    pieceViews.remove(specialMove.enPassantPiece)
                }

                is PawnPromotion -> {
                    val promotedPieceView = specialMove.promotedPieceView
                    pieceViews.add(promotedPieceView)
                    pieceViews.remove(move.pieceView)
                }

                else -> null
            }
        }

        squareManager.resetSquares()

        squareManager.addSquare(move.startingPosition, lastMove = true)
        squareManager.addSquare(move.finalPosition, lastMove = true)

        move.capturedPieceView?.let {
            pieceViews.remove(it)
        }

        invalidator.invalidateCanvas()

        animatePieceMovement(
            move.pieceView, move.finalPosition, squareSize, invalidator, 100L
        )
    }

    private fun canCaptureEnPassant(
        pawn: Piece, pawnPosition: Position
    ): Position? {
        if (GameManager.undoStack.isEmpty()) return null
        val lastMove = GameManager.undoStack.last()

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
        squareManager.resetSquareHighlights()
        GameManager.selectedSquarePosition = null
        GameManager.safeSquares = listOf()
        invalidator.invalidateCanvas()
    }
}