package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type


class Bishop(override val type: Type) : IPiece {
    override val piece: Piece = Piece.BISHOP
    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1)
    )

    override var fen: String = if (type == Type.LIGHT) "B" else "b"

    override fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        moves.forEach {
            var currentRow = row
            var currentCol = col

            while (true) {
                currentRow += it.first
                currentCol += it.second

                if (currentRow !in 0..7 || currentCol !in 0..7) break

                val targetPiece = chessBoard[currentRow][currentCol]

                if (targetPiece == null) {
                    possibleMoves.add(Pair(currentRow, currentCol))
                } else {
                    if (targetPiece.type != type) {
                        possibleMoves.add(Pair(currentRow, currentCol))
                    }
                    break
                }
            }
        }
        return possibleMoves.toTypedArray()
    }
}