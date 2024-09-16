package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position


class Knight(
    override val color: Color,
    override var currentPosition: Position,
    override val bitmap: Bitmap
) : Piece {
    override val type = Type.KNIGHT

    override val directions = arrayOf(
        Pair(-2, -1), Pair(-1, -2), Pair(-2, 1), Pair(-1, 2),
        Pair(2, -1), Pair(1, -2), Pair(2, 1), Pair(1, 2)
    )

    override var fen = if (color == Color.LIGHT) "N" else "n"
}