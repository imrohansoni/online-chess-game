package com.imrohansoni.chess


import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.imrohansoni.chess.utils.CanvasInvalidator

class ChessBoardView(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet), CanvasInvalidator {
    private val controller: ChessBoardController = ChessBoardController(context, this)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in controller.squares.indices) {
            for (col in controller.squares[row].indices) {
                val square = controller.squares[row][col]
                square.draw(canvas)
            }
        }

        controller.pieceViews.forEach {
            canvas.drawBitmap(it.bitmap, it.y, it.x, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (controller.currentlySelectedSquare != null) {
                    controller.moveSelectedPiece(event.x, event.y)
                } else {
                    controller.selectSquare(event.x, event.y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
            }

            MotionEvent.ACTION_UP -> {

            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun moveToFirstMove() {
        TODO("Not yet implemented")
    }

    fun moveToLastMove() {
        TODO("Not yet implemented")
    }

    fun moveToPreviousMove() {
        TODO("Not yet implemented")
    }

    fun moveToNextMove() {
        TODO("Not yet implemented")
    }

    override fun canvasInvalidator() {
        invalidate()
    }
}