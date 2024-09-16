package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type


class Knight(
    override val color: Color,
) : Piece {
    override val type = Type.KNIGHT

    override val directions = arrayOf(
        Pair(-2, -1), Pair(-1, -2), Pair(-2, 1), Pair(-1, 2),
        Pair(2, -1), Pair(1, -2), Pair(2, 1), Pair(1, 2)
    )

    override var fen = if (color == Color.LIGHT) "N" else "n"
}