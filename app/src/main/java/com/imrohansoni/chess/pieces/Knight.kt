package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Knight(override val type: Type) : IPiece {
    override val piece: Piece = Piece.KNIGHT

    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(-2, -1), Pair(-1, -2), Pair(-2, 1), Pair(-1, 2),
        Pair(2, -1), Pair(1, -2), Pair(2, 1), Pair(1, 2)
    )

    override var fen: String = if (type == Type.LIGHT) "N" else "n"


    override fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        moves.forEach {
            val x = row + it.first
            val y = col + it.second
            if (x in 0..7 && y in 0..7) {
                if (chessBoard[x][y] == null || chessBoard[x][y]?.type != type) {
                    possibleMoves.add(Pair(x, y))
                }
            }
        }
        return possibleMoves.toTypedArray()
    }
}