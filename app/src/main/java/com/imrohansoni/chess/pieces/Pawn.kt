package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Pawn(private val type: Type) : IPiece {
    private var moved: Boolean = false
    override val piece: Piece = Piece.PAWN

    override val moves: Array<Pair<Int, Int>> =
        if (type == Type.LIGHT)
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        else
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))

    override var FENChar: String = if (type == Type.LIGHT) "P" else "p"

    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        // Pair(-2, 0) or Pair(2, 0) this pair is only for first move
        // Pair(-1, -1), Pair(-1, 1) or Pair(1, -1) , Pair(1, 1) this pairs is only for capture
        return arrayOf()
    }

    fun hasMoved(): Boolean {
        return moved
    }
}