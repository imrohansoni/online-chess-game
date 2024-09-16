package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position

class Bishop(
    override val color: Color,
    override var currentPosition: Position,
    override val bitmap: Bitmap
) : Piece {
    override val type = Type.BISHOP
    override val directions = arrayOf(
        Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1)
    )
    override var fen = if (color == Color.LIGHT) "B" else "b"
}