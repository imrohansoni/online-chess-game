package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.Type

typealias Directions = Array<Pair<Int, Int>>

interface Piece {
    operator fun component1() = color
    operator fun component2() = directions

    val type: Type
    val color: Color
    var fen: String
    val directions: Directions
}