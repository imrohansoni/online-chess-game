package com.imrohansoni.chess

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.imrohansoni.chess.models.ChessPiece
import com.imrohansoni.chess.models.Square
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider



class ChessBoard(private val context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val piecesBitmap = ChessPieceBitmapProvider.getPieceBitmaps(context)
    private val pieces = ChessBoardManager.initializeBoard()
    private var selectedSquare: Square? = null


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

    private val squares = Array(8) { row ->
        Array(8) { col ->
            Square(row, col).apply {
                val chessPiece = pieces[row][col]
                chessPiece?.let {
                    chessPieceBitmap = getPieceBitmap(it)?.let { bitmap ->
                        Bitmap.createScaledBitmap(bitmap, side, side, false)
                    }
                }
            }
        }
    }

    private fun movePiece(x: Float, y: Float): Boolean {
        selectedSquare?.let {
            val chessPieceBitmap = squares[it.row][it.col].chessPieceBitmap
            squares[it.row][it.col].chessPieceBitmap = null
            val (toRow, toCol) = findPosition(x, y)
            squares[toRow][toCol].chessPieceBitmap = chessPieceBitmap
            invalidate()
            return true
        }
        return false
    }

    private fun getPieceBitmap(chessPiece: ChessPiece): Bitmap? {
        val piece = "${chessPiece.type.type}_${chessPiece.piece.piece}"
        return piecesBitmap[piece]
    }

    private fun findPosition(x: Float, y: Float): Array<Int> {
        val row = ((y - gap) / side).toInt()
        val col = ((x - gap) / side).toInt()
        return arrayOf(row, col)
    }

    private fun selectSquare(x: Float, y: Float) {
        // calculating the row and column number using coordinates
        val (row, col) = findPosition(x, y)

        if (row in 0..7 && col in 0..7) {
            if (squares[row][col].chessPieceBitmap != null) {
                squares[row][col].isSelected = true
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
                squares[row][col].draw(side, canvas, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (selectedSquare != null) {
                    if (movePiece(event.x, event.y)) {
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

        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}