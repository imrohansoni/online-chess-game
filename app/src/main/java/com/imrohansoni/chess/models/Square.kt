package com.imrohansoni.chess.models

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

// File => column, Rank => row
val files = arrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
val ranks = arrayOf('8', '7', '6', '5', '4', '3', '2', '1')

class Square(val row: Int, val col: Int, val side : Int) {
    val algebraicNotation = "${files[col]}${ranks[row]}"

    var isSelected = false
    private val gap = 40

    fun draw(canvas: Canvas, paint: Paint) {
        val top = (row * side + gap).toFloat()
        val bottom = (top + side)
        val left = (col * side + gap).toFloat()
        val right = (left + side)

        val selectedPaint = Paint().apply {
            color = Color.parseColor("#00BCD4")
        }

        val finalPaint = if (isSelected) selectedPaint else paint

        canvas.drawRect(left, top, right, bottom, finalPaint)
    }

    fun drawPiece(canvas: Canvas, bitmap: Bitmap) {
        val top = (row * side + gap).toFloat()
        val left = (col * side + gap).toFloat()
        canvas.drawBitmap(bitmap, left, top, null)
    }

}