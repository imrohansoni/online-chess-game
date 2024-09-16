package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.utils.ChessBoardManager


class Pawn(
    override val color: Color,
    override var currentPosition: Position,
    override val bitmap: Bitmap
) : Piece {
    var moved = false
    override var fen = if (color == Color.LIGHT) "P" else "p"
    override val type = Type.PAWN

    override val directions = when {
        color == Color.LIGHT && ChessBoardManager.primaryPlayerColor == Color.LIGHT -> {
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        }

        color == Color.LIGHT && ChessBoardManager.primaryPlayerColor == Color.DARK -> {
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))
        }

        color == Color.DARK && ChessBoardManager.primaryPlayerColor == Color.LIGHT -> {
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))
        }

        else -> {
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        }
    }

}