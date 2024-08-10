package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece

interface IPiece {
    val piece: Piece
    val moves: Array<Pair<Int, Int>>
    var FENChar: String

    fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>>
}