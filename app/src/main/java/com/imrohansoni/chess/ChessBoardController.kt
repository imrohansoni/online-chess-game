package com.imrohansoni.chess

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.PieceView
import com.imrohansoni.chess.models.Square
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.utils.CanvasInvalidator
import com.imrohansoni.chess.utils.ChessBoardState
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider
import com.imrohansoni.chess.utils.animatePieceMovement

class ChessBoardController(
    private val context: Context,
    private val invalidator: CanvasInvalidator
) {
    private val moves = mutableListOf<Move>()

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = DisplayMetrics()

    private val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowsMatrix = windowManager.currentWindowMetrics
        val bounds = windowsMatrix.bounds
        val width = bounds.width()
        width
    } else {
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
    private var currentPlayerType = Type.LIGHT

    private val squareSize = width / 8

    private val pieceBitmaps = ChessPieceBitmapProvider.getPieceBitmaps(context, squareSize)
    private val boardState = ChessBoardState.initializeBoard()
    var currentlySelectedSquare: Square? = null
    private var availableMoves: Array<Pair<Int, Int>>? = null
    val pieceViews = mutableListOf<PieceView>()

    init {
        initializePieceViews()
    }

    val squares = Array(8) { row ->
        Array(8) { col ->
            Square(context, row, col, squareSize)
        }
    }


    private fun initializePieceViews() {
        for (row in boardState.indices) {
            for (col in boardState[row].indices) {
                boardState[row][col]?.let { piece ->
                    pieceBitmaps[piece.fen]?.let { bitmap ->
                        val x = (squareSize * row).toFloat()
                        val y = (squareSize * col).toFloat()
                        val pieceView = PieceView(bitmap, row, col, x, y)
                        pieceViews.add(pieceView)
                    }
                }
            }
        }
    }


    fun moveSelectedPiece(x: Float, y: Float) {

        currentlySelectedSquare?.let {
            val (endRow, endCol) = getPiecePosition(x, y)

            val startRow = currentlySelectedSquare!!.row
            val startCol = currentlySelectedSquare!!.col

            if (availableMoves?.contains(Pair(endRow, endCol)) == true) {
                val canMove =
                    boardState[startRow][startCol].let { piece -> piece?.type == currentPlayerType }
                if (!canMove) {
                    return selectSquare(x, y)
                }

                val pieceView = pieceViews.find { p ->
                    p.row == startRow && p.col == startCol
                }

                val capturedPieceView = pieceViews.find { p ->
                    p.row == endRow && p.col == endCol
                }

                pieceView?.let {
                    animatePieceMovement(pieceView, endRow, endCol, squareSize, invalidator)
                    pieceView.row = endRow
                    pieceView.col = endCol
                }

                if (capturedPieceView != null) {
                    pieceViews.remove(capturedPieceView)
                }

                val piece = boardState[startRow][startCol]
                boardState[startRow][startCol] = null
                boardState[endRow][endCol] = piece


                it.isSelected = false

                resetSquareFlags()
                resetMovedFlags()
                squares[startRow][startCol].lastMove = true
                squares[endRow][endCol].lastMove = true


                if (piece is Pawn) {
                    piece.moved = true
                }

                if (piece is King) {
                    piece.moved = true
                }

                currentPlayerType = if (currentPlayerType == Type.LIGHT) Type.DARK else Type.LIGHT

                availableMoves = null
                currentlySelectedSquare = null

                moves.add(
                    Move(
                        piece!!.piece,
                        currentPlayerType,
                        startRow = startRow,
                        startCol,
                        endRow,
                        endCol
                    )
                )

                invalidator.canvasInvalidator()
            } else {
                if (boardState[endRow][endCol] != null) {
                    selectSquare(x, y)
                }
            }
        }
    }

    private fun getPiecePosition(x: Float, y: Float): Array<Int> {
        val row = (y / squareSize).toInt().coerceIn(0, 7)
        val col = (x / squareSize).toInt().coerceIn(0, 7)
        return arrayOf(row, col)
    }

    private fun resetMovedFlags() {
        for (row in squares.indices) {
            for (col in squares[row].indices) {
                squares[row][col].lastMove = false
            }
        }
    }

    private fun resetSquareFlags() {
        for (row in squares.indices) {
            for (col in squares[row].indices) {
                squares[row][col].isAvailable = false
                squares[row][col].canBeCaptured = false
            }
        }
    }

    fun selectSquare(x: Float, y: Float) {
        val (row, col) = getPiecePosition(x, y)

        if (row !in 0..7 || col !in 0..7) return

        if (boardState[row][col] == null) {
            return
        }

        val selectedSquare = squares[row][col]

        if (this.currentlySelectedSquare == selectedSquare) {
            return
        }

        this.currentlySelectedSquare?.isSelected = false

        resetSquareFlags()

        this.currentlySelectedSquare = selectedSquare
        currentlySelectedSquare?.isSelected = true

        availableMoves =
            boardState[row][col]?.calculatePossibleMoves(row, col, boardState)

        availableMoves?.forEach {
            squares[it.first][it.second].isAvailable = true
            if (boardState[it.first][it.second] != null) {
                squares[it.first][it.second].canBeCaptured = true
            }
        }
        invalidator.canvasInvalidator()
    }
}