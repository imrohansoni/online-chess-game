package com.imrohansoni.chess.pieces

import android.graphics.Bitmap
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.models.Position

typealias Directions = Array<Pair<Int, Int>>

interface Piece {
    operator fun component1() = color
    operator fun component2() = directions
    operator fun component3() = currentPosition

    val type: Type
    val color: Color
    var fen: String
    val directions: Directions
    var currentPosition: Position
    val bitmap: Bitmap
}