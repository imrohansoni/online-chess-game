package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type

class King(
    override val color: Color
) : Piece {
    var moved = false

    override var fen = if (color == Color.LIGHT) "K" else "k"
    override val directions = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1),
        Pair(-1, -1), Pair(-1, 1),
        Pair(1, -1), Pair(1, 1)
    )
    override val type = Type.KING

//    override fun findAvailableSquares(
//        currentPosition: Position,
//        boardState: BoardState
//    ): Array<Position> {
//
//        val availableSquares = mutableListOf<Position>()
//
//        for (direction in directions) {
//            val row = currentPosition.row + direction.first
//            val col = currentPosition.col + direction.second
//
//            if (row !in boardRange || col !in boardRange) continue
//
//            val targetPiece = boardState[row][col]
//
//            if (targetPiece == null || targetPiece.color != color) {
//                availableSquares.add(Position(row, col))
//            }
//        }
//
//        return availableSquares.toTypedArray()
//    }
}