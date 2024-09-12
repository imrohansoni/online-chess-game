package com.imrohansoni.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.Constants.boardRange
import com.imrohansoni.chess.utils.SquareManager

class ChessBoardView(
    private val context: Context,
    private val attributeSet: AttributeSet
) : View(context, attributeSet) {

    private val squareState = SquareManager(context)

    private val paint = Paint().apply {
        color = context.getColor(R.color.notation)
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val square = Square(context, 0, 0, squareState.squareSize)

    override fun onDraw(canvas: Canvas) {
        for (row in boardRange) {
            for (col in boardRange) {
                square.setPosition(row, col)
                square.draw(canvas)
            }
        }

        ChessBoardManager.ranks.forEachIndexed { i, rank ->
            canvas.drawText(
                rank.toString(), 20f, (i * squareState.squareSize).toFloat() + 40f, paint
            )
        }

        ChessBoardManager.files.forEachIndexed { i, file ->
            canvas.drawText(
                file.toString().uppercase(),
                ((i + 1) * squareState.squareSize).toFloat() - 40f,
                (8 * squareState.squareSize).toFloat() - 10f,
                paint
            )
        }


    }
}