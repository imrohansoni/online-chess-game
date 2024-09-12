package com.imrohansoni.chess.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.imrohansoni.chess.R
import com.imrohansoni.chess.utils.ChessBoardManager


class Square(
    private val context: Context,
    private var row: Int,
    private var col: Int,
    private val squareSize: Int
) {

    val algebraicNotation = "${ChessBoardManager.files[col]}${ChessBoardManager.ranks[row]}"

    fun setPosition(row: Int, col: Int) {
        this.row = row
        this.col = col
    }

    fun getPosition(): Position {
        return Position(row, col)
    }

    var isSelected = false
    var isAvailable = false
    var canBeCaptured = false
    var lastMove = false
    var isChecked = false

    private val lightSquarePaint = Paint().apply {
        color = context.getColor(R.color.lightSquare)
    }
    private val darkSquarePaint = Paint().apply {
        color = context.getColor(R.color.darkSquare)
    }

    private val selectedSquarePaint = Paint().apply {
        color = context.getColor(R.color.selectedSquare)
    }

    private val availableSquarePaint = Paint().apply {
        color = context.getColor(R.color.availableSquare)
    }

    private val lastMoveSquarePaint = Paint().apply {
        color = context.getColor(R.color.lastMoveSquare)
    }

    private val checkedPaint = Paint().apply {
        color = context.getColor(R.color.checked)
    }

    private val canBeCapturedSquarePaint = Paint().apply {
        color = context.getColor(R.color.canBeCapturedSquare)
        style = Paint.Style.STROKE
        strokeWidth = 12f
        isAntiAlias = true
    }


    fun draw(canvas: Canvas) {
        val top = (row * squareSize).toFloat()
        val bottom = (top + squareSize)
        val left = (col * squareSize).toFloat()
        val right = (left + squareSize)

        val basePaint =
            if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
        canvas.drawRect(left, top, right, bottom, basePaint)



        when {
            isSelected -> canvas.drawRect(left, top, right, bottom, selectedSquarePaint)
            isAvailable && lastMove -> {
                canvas.drawRect(left, top, right, bottom, lastMoveSquarePaint)
                canvas.drawCircle(centerX(), centerY(), 30f, availableSquarePaint)
            }

            lastMove -> canvas.drawRect(left, top, right, bottom, lastMoveSquarePaint)
            isChecked -> canvas.drawRect(left, top, right, bottom, checkedPaint)
            isAvailable -> canvas.drawCircle(centerX(), centerY(), 30f, availableSquarePaint)

        }

        if (canBeCaptured) {
            val ringRadius = squareSize / 2.2f
            canvas.drawCircle(centerX(), centerY(), ringRadius, canBeCapturedSquarePaint)
        }
    }


    private fun centerX(): Float {
        return (col * squareSize + squareSize / 2).toFloat()
    }

    private fun centerY(): Float {
        return (row * squareSize + squareSize / 2).toFloat()
    }
}