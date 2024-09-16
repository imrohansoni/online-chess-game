package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position


class Rook(override val color: Color,
           override var currentPosition: Position,
           override val bitmap: Bitmap
) : Piece {
    var moved = false
    override val type = Type.ROOK
    override val directions = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var fen = if (color == Color.LIGHT) "R" else "r"
}