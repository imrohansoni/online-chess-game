package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class Queen(override val type: Type) : IPiece {
    override val piece: Piece = Piece.QUEEN
    override val moves: Array<Pair<Int, Int>> = arrayOf(
        Pair(1, 1), Pair(-1, -1),
        Pair(-1, 1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var fen: String = if (type == Type.LIGHT) "Q" else "q"
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

                var targetSquare = chessBoard[currentRow][currentCol]
                if (targetSquare == null) {
                    possibleMoves.add(Pair(currentRow, currentCol))
                } else {
                    if (targetSquare.type != type) {
                        possibleMoves.add(Pair(currentRow, currentCol))
                    }
                    break
                }
            }
        }
        return possibleMoves.toTypedArray()
    }
}