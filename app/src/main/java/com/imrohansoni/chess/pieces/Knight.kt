package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Knight(private val type: Type) : IPiece {
    override val piece: Piece = Piece.KNIGHT

    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(-2, -1), Pair(-1, -2), Pair(-2, 1), Pair(-1, 2),
        Pair(2, -1), Pair(1, -2), Pair(2, 1), Pair(1, 2)
    )

    override var FENChar: String = if (type == Type.LIGHT) "N" else "n"


    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        return arrayOf()
    }
}