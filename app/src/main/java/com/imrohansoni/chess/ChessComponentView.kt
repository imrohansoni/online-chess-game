package com.imrohansoni.chess


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.imrohansoni.chess.adapters.CapturedPiece
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.utils.CanvasInvalidator
import com.imrohansoni.chess.utils.SquareManager

class ChessBoardView(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet), CanvasInvalidator {
    val controller: ChessBoardController = ChessBoardController(context, this)

    private var onPieceMoveHandler: ((Move) -> Unit)? = null
    private var onPieceCaptureHandler: ((CapturedPiece) -> Unit)? = null

    private val paint = Paint().apply {
        color = context.getColor(R.color.notation)
        textSize = 30f // Adjust the text size as needed
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        SquareManager.squares.forEach {
            it.draw(canvas)
        }

        controller.pieceViews.forEach {
            canvas.drawBitmap(it.bitmap, it.y, it.x, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (controller.currentlySelectedSquare != null) {
                    val move = controller.moveSelectedPiece(event.x, event.y)
                    move?.let {
                        onPieceMoveHandler?.invoke(it)

                        if (it.capturePiece != null) {
                            onPieceCaptureHandler?.invoke(
                                CapturedPiece(
                                    it.capturePiece.bitmap,
                                    it.capturedPieceType
                                )
                            )
                        }
                    }

                } else {
                    controller.selectSquare(event.x, event.y)
                }
            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setOnPieceMoveHandler(callback: (Move) -> Unit) {
        onPieceMoveHandler = callback
    }

    fun setOnPieceCaptureHandler(callback: (CapturedPiece) -> Unit) {
        onPieceCaptureHandler = callback
    }

    override fun canvasInvalidator() {
        invalidate()
    }
}