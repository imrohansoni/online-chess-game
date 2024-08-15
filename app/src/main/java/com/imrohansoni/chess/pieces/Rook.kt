package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type


class Rook(override val type: Type) : IPiece {
    var moved: Boolean = false

    override val piece: Piece = Piece.ROOK
    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var fen: String = if (type == Type.LIGHT) "R" else "r"

    override fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        if (!moved) {
            val k = if (type == Type.LIGHT) Pair(7, 4) else Pair(0, 4)
            if (chessBoard[k.first][k.second] != null || chessBoard[k.first][k.second]?.piece == Piece.KING) {

            }
        }
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