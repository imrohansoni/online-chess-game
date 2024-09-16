package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type

class Bishop(
    override val color: Color
) : Piece {
    override val type = Type.BISHOP
    override val directions = arrayOf(
        Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1)
    )
    override var fen = if (color == Color.LIGHT) "B" else "b"
}