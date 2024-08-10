package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Rook(private val type: Type) : IPiece {
    private var moved: Boolean = false

    override val piece: Piece = Piece.ROOK
    override val moves: Array<Pair<Int, Int>> = arrayOf()
    override var FENChar: String = if (type == Type.LIGHT) "R" else "r"

    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        return arrayOf(
            Pair(-1, 0), Pair(1, 0),  // Up, Down
            Pair(0, -1), Pair(0, 1) // Left, Right
        )
    }

    fun hasMoved(): Boolean {
        return moved
    }
}