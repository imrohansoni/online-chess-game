package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Pawn(override val type: Type) : IPiece {
    var moved: Boolean = false

    override val piece: Piece = Piece.PAWN

    override val moves: Array<Pair<Int, Int>> =
        if (type == Type.LIGHT)
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        else
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))

    override var fen: String = if (type == Type.LIGHT) "P" else "p"

    override fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        val row1 = row + moves[0].first
        val col1 = col + moves[0].second

        if (row1 in 0..7 && col1 in 0..7) {
            if (chessBoard[row1][col1] == null) {
                possibleMoves.add(Pair(row1, col1))
            }
        }

        if (!moved) {
            val row2 = row + moves[1].first
            val col2 = col + moves[1].second

            if (row2 in 0..7 && col2 in 0..7) {
                if (chessBoard[row2][col2] == null) {
                    possibleMoves.add(Pair(row2, col2))
                }
            }

        }

        val row3 = row + moves[2].first
        val col3 = col + moves[2].second

        if (row3 in 0..7 && col3 in 0..7 && chessBoard[row3][col3] != null) {
            if(chessBoard[row3][col3]?.type != this.type){
                possibleMoves.add(Pair(row3, col3))
            }
        }

        val row4 = row + moves[3].first
        val col4 = col + moves[3].second

        if (row4 in 0..7 && col4 in 0..7 && chessBoard[row4][col4] != null) {
            if(chessBoard[row4][col4]?.type != this.type){
                possibleMoves.add(Pair(row4, col4))
            }
        }

        return possibleMoves.toTypedArray()
    }
}