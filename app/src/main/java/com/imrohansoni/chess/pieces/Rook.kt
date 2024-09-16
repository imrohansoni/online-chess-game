package com.imrohansoni.chess.pieces


import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type

class Rook(override val color: Color
) : Piece {
    var moved = false
    override val type = Type.ROOK
    override val directions = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var fen = if (color == Color.LIGHT) "R" else "r"
}