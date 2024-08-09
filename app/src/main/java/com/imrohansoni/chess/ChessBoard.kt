package com.imrohansoni.chess

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
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

    private fun getPieceBitmap(chessPiece: ChessPiece): Bitmap? {
        val piece = "${chessPiece.type.type}_${chessPiece.piece.piece}"
        Log.d("CHESS_PIECES", piece)
        return piecesBitmap[piece]
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}