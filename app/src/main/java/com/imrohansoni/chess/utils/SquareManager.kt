package com.imrohansoni.chess.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import com.imrohansoni.chess.Square
import com.imrohansoni.chess.models.Position

class SquareManager(private val context: Context) {
    companion object {
        val squares = mutableListOf<Square>()
    }

    private val windowManager =
        getSystemService(context, WindowManager::class.java) as WindowManager
    private val displayMetrics = DisplayMetrics()

    private val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowsMatrix = windowManager.currentWindowMetrics
        val bounds = windowsMatrix.bounds
        bounds.width()
    } else {
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
    val squareSize = width / 8

    fun getSquarePosition(x: Float, y: Float): Position {
        val row = (y / squareSize).toInt().coerceIn(0, 7)
        val col = (x / squareSize).toInt().coerceIn(0, 7)
        return Position(row, col)
    }

    fun resetSquares() {
        squares.clear()
    }

    fun resetSquareHighlights() {
        squares.forEach {
            it.isSelected = false
            it.isSafeSquare = false
            it.canBeCaptured = false
        }
    }

    fun addSquare(
        position: Position,
        isSelected: Boolean = false,
        canBeCaptured: Boolean = false,
        isSafeSquare: Boolean = false,
        lastMove: Boolean = false,
        isChecked: Boolean = false
    ): Square {
        val existingSquare = squares.find { square -> square.getPosition() == position }

        if (existingSquare != null) {
            existingSquare.isSelected = isSelected || existingSquare.isSelected
            existingSquare.canBeCaptured = canBeCaptured || existingSquare.canBeCaptured
            existingSquare.isSafeSquare = isSafeSquare || existingSquare.isSafeSquare
            existingSquare.lastMove = lastMove || existingSquare.lastMove
            existingSquare.isChecked = isChecked || existingSquare.isChecked

            return existingSquare
        } else {
            val newSquare = Square(context, position.row, position.col, squareSize).apply {
                this.isSelected = isSelected
                this.canBeCaptured = canBeCaptured
                this.isSafeSquare = isSafeSquare
                this.lastMove = lastMove
                this.isChecked = isChecked
            }
            squares.add(newSquare)
            return newSquare
        }
    }
}
