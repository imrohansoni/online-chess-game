package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Color

class Queen(override val color: Color,
            override var currentPosition: Position,
            override val bitmap: Bitmap
) : Piece {
    override val type = Type.QUEEN
    override val directions = arrayOf(
        Pair(1, 1), Pair(-1, -1),
        Pair(-1, 1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )

    override var fen = if (color == Color.LIGHT) "Q" else "q"
}