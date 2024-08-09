package com.imrohansoni.chess.models

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class Square(private val row: Int, private val col: Int) {
    var isSelected = false
    var chessPieceBitmap: Bitmap? = null
    private val GAP = 40


    fun draw(side: Int, canvas: Canvas, paint: Paint) {
        val top = (row * side + GAP).toFloat()
        val bottom = (top + side)
        val left = (col * side + GAP).toFloat()
        val right = (left + side)

        canvas.drawRect(left, top, right, bottom, paint)

        chessPieceBitmap?.let {
            canvas.drawBitmap(it, left, top, null)
        }
    }

}