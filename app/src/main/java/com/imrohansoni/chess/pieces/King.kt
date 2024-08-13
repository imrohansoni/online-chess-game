package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class King(override val type: Type) : IPiece {
    var moved = false

    override var fen: String = if (type == Type.LIGHT) "K" else "k"

    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1),
        Pair(-1, -1), Pair(-1, 1),
        Pair(1, -1), Pair(1, 1)
    )
    override val piece: Piece = Piece.KING

    override fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        for (move in moves) {
            val currentRow = row + move.first
            val currentCol = col + move.second
            if (currentRow !in 0..7 || currentCol !in 0..7) continue

            val targetSquare = chessBoard[currentRow][currentCol]
            if (targetSquare == null || targetSquare.type != type) {
                possibleMoves.add(Pair(currentRow, currentCol))
            }
        }

        return possibleMoves.toTypedArray()
    }

}