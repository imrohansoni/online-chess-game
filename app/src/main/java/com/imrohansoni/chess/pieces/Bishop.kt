package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Bishop(private val type: Type) : IPiece {
    override val piece: Piece = Piece.BISHOP
    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1)
    )

    override var FENChar: String = if (type == Type.LIGHT) "B" else "b"

    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        return arrayOf()
    }
}