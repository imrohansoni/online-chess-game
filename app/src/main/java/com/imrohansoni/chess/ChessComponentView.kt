package com.imrohansoni.chess


import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.imrohansoni.chess.adapters.CapturedPiece
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.utils.CanvasInvalidator
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.SquareManager

class ChessComponentView(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet), CanvasInvalidator {

    private val controller = ChessBoardController(context, this)
    private val squareManager = SquareManager(context)
    private val chessBoardManger = ChessBoardManager()

    private var onPieceMove: ((Move) -> Unit)? = null
    private var onPieceCapture: ((CapturedPiece) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        SquareManager.squares.forEach {
            it.draw(canvas)
        }

        controller.pieceViews.forEach {
            canvas.drawBitmap(it.bitmap, it.y, it.x, null)
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val position = squareManager.getSquarePosition(x, y)

        if (GameState.selectedSquarePosition == position) return

        val piece = chessBoardManger.getPiece(position)

        GameState.selectedSquarePosition?.let { selectedPosition ->
            if (controller.canMovePiece() && GameState.safeSquares.contains(position)) {
                controller.movePiece(selectedPosition, position)?.let { move ->
                    onPieceMove?.invoke(move)
                    if (move.capturePiece != null) {
                        onPieceCapture?.invoke(
                            CapturedPiece(move.capturePiece.bitmap, move.capturedPieceColor)
                        )
                    }
                }
                return
            }

            if (piece?.color != GameState.currentPlayerColor) {
                controller.resetSelection()
            } else {
                controller.selectSquare(position)
            }
            return
        }

        controller.selectSquare(position)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setOnPieceMoveHandler(callback: (Move) -> Unit) {
        onPieceMove = callback
    }

    fun setOnPieceCaptureHandler(callback: (CapturedPiece) -> Unit) {
        onPieceCapture = callback
    }

    override fun canvasInvalidator() {
        invalidate()
    }
}