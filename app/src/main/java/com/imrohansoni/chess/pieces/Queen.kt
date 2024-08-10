package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Queen(private val type: Type) : IPiece {
    override val piece: Piece = Piece.QUEEN
    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(1, 1), Pair(-1, -1),
        Pair(-1, 1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var FENChar: String = if (type == Type.LIGHT) "Q" else "q"
    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        return arrayOf()
    }
}