package com.imrohansoni.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.imrohansoni.chess.models.Square
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider


class ChessBoard(private val context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = DisplayMetrics()

    private val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowsMatrix = windowManager.currentWindowMetrics
        val bounds = windowsMatrix.bounds
        val width = bounds.width()
        width
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    private val gap = 40
    private val side = ((width - (gap * 2)) / 8)
    private val lightSquarePaint = Paint().apply {
        color = context.getColor(R.color.lightSquare)
    }
    private val darkSquarePaint = Paint().apply {
        color = context.getColor(R.color.darkSquare)
    }

    private val piecesBitmap = ChessPieceBitmapProvider.getPieceBitmaps(context, side)
    private val chessBoardPieces = ChessBoardManager.initializeBoard()
    private var selectedSquare: Square? = null
    private var possibleSquares: Array<Pair<Int, Int>>? = null

    private val squares = Array(8) { row ->
        Array(8) { col ->
            Square(row, col, side)
        }
    }

    private fun moveSelectedPiece(x: Float, y: Float) {

        selectedSquare?.let {
            val (endRow, endCol) = getPiecePosition(x, y)

            val startRow = selectedSquare!!.row
            val startCol = selectedSquare!!.col

            if (possibleSquares?.contains(Pair(endRow, endCol)) == true) {

                val piece = chessBoardPieces[startRow][startCol]
                chessBoardPieces[startRow][startCol] = null
                chessBoardPieces[endRow][endCol] = piece

                it.isSelected = false

                possibleSquares?.forEach { square ->
                    squares[square.first][square.second].isPossible = false
                }
                if (piece is Pawn) {
                    piece.moved = true
                }

                if (piece is King) {
                    piece.moved = true
                }

                possibleSquares = null
                selectedSquare = null

                invalidate()
            } else {
                if (chessBoardPieces[endRow][endCol] != null) {
                    selectSquare(x, y)
                }
            }
        }
    }

    private fun getPiecePosition(x: Float, y: Float): Array<Int> {
        val row = ((y - gap) / side).toInt().coerceIn(0, 7)
        val col = ((x - gap) / side).toInt().coerceIn(0, 7)
        return arrayOf(row, col)
    }

    private fun selectSquare(x: Float, y: Float) {
        val (row, col) = getPiecePosition(x, y)
        if(row !in 0..7 || col !in 0..7) return

        val selectedSquare = squares[row][col]

        // Check if the square clicked is the same as the currently selected square
        if (this.selectedSquare == selectedSquare ) {
            return
        } else {
            // Deselect the previously selected square and reset possible moves
            this.selectedSquare?.isSelected = false
            possibleSquares?.forEach { squares[it.first][it.second].isPossible = false }

            // Set the new selected square and calculate possible moves
            this.selectedSquare = selectedSquare
            selectedSquare.isSelected = true
            possibleSquares =
                chessBoardPieces[row][col]?.calculatePossibleMoves(row, col, chessBoardPieces)

            possibleSquares?.forEach { squares[it.first][it.second].isPossible = true }
        }

        // Redraw the board with the new selection
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in squares.indices) {
            for (col in squares[row].indices) {
                val paint = if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
                val square = squares[row][col]

                square.draw(canvas, paint)
                val piece = chessBoardPieces[row][col]

                piece?.let {
                    val pieceBitmap = piecesBitmap[piece.fen]
                    pieceBitmap?.let {
                        square.drawPiece(canvas, it)
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val coords = getPiecePosition(event.x, event.y)
                if (selectedSquare != null) {
                    moveSelectedPiece(event.x, event.y)
                } else {
                    selectSquare(event.x, event.y)
                }
            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}