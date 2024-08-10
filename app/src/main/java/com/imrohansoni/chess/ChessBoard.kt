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
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider


class ChessBoard(private val context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    // calculating the window width
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
    // calculating the length of the side using screen width and gap
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

    // creating 8*8 2d array for displaying 64 squares in chess board
    private val squares = Array(8) { row ->
        Array(8) { col ->
            Square(row, col, side)
        }
    }

    private fun moveSelectedPiece(x: Float, y: Float): Boolean {
        selectedSquare?.let {
            val (endRow, endCol) = getPiecePosition(x, y)

            // getting row and col from previously selected square
            val startRow = selectedSquare!!.row
            val startCol = selectedSquare!!.col

            // getting the piece using startRow and startCol
            val piece = chessBoardPieces[startRow][startCol]
            // removing the piece from the previous location
            chessBoardPieces[startRow][startCol] = null
            // adding the to the new location
            chessBoardPieces[endRow][endCol] = piece

            invalidate()
            return true
        }
        return false
    }

    private fun getPiecePosition(x: Float, y: Float): Array<Int> {
        val row = ((y - gap) / side).toInt()
        val col = ((x - gap) / side).toInt()
        return arrayOf(row, col)
    }

    private fun selectSquare(x: Float, y: Float) {
        // calculating the row and column number using coordinates
        val (row, col) = getPiecePosition(x, y)

        if (row in 0..7 && col in 0..7) {
            squares[row][col].isSelected = true

            if (chessBoardPieces[row][col] != null) {
                selectedSquare = squares[row][col]
            }
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in squares.indices) {
            for (col in squares[row].indices) {
                val paint = if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
                val square = squares[row][col]

                square.draw(canvas, paint)
                val piece = chessBoardPieces[row][col]
                // if there is a piece in this square call the drawPiece method on the square

                piece?.let {
                    val pieceBitmap = piecesBitmap[piece.FENChar]
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
                if (selectedSquare != null) {
                    if (moveSelectedPiece(event.x, event.y)) {
                        selectedSquare?.isSelected = false
                        selectedSquare = null
                    }
                } else {
                    selectSquare(event.x, event.y)
                }
            }

            MotionEvent.ACTION_MOVE -> {

            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}