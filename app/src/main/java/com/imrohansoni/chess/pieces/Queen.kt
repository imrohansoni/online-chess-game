package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type

class Queen(override val color: Color) : Piece {
    override val type = Type.QUEEN
    override val directions = arrayOf(
        Pair(1, 1), Pair(-1, -1),
        Pair(-1, 1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )

    override var fen = if (color == Color.LIGHT) "Q" else "q"
}