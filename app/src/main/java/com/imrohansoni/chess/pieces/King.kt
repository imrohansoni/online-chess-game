package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class King(private val type: Type) : IPiece {
    private var moved = false

    override var FENChar: String = if (type == Type.LIGHT) "K" else "k"

    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(-1, 0), Pair(1, 0),  // Up, Down
        Pair(0, -1), Pair(0, 1),  // Left, Right
        Pair(-1, -1), Pair(-1, 1), // Diagonal up-left, up-right
        Pair(1, -1), Pair(1, 1)   // Diagonal down-left, down-right
    )
    override val piece: Piece = Piece.KING

    override fun calculatePossibleMoves(row: Int, col: Int): Array<Pair<Int, Int>> {
        return arrayOf()
    }

    fun hasMoved(): Boolean {
        return moved
    }


}