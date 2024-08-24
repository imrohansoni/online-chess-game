package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.Constants.boardRange


class Knight(override val type: Type) : ChessPiece {
    override val piece = Piece.KNIGHT

    override val directions = arrayOf(
        Pair(-2, -1), Pair(-1, -2), Pair(-2, 1), Pair(-1, 2),
        Pair(2, -1), Pair(1, -2), Pair(2, 1), Pair(1, 2)
    )

    override var fen = if (type == Type.LIGHT) "N" else "n"


    override fun calculatePossibleMoves(
        currentPosition: Position,
        boardState: BoardState
    ): Array<Position> {
        val availableSquares = mutableListOf<Position>()
        directions.forEach {
            val row = currentPosition.row + it.first
            val col = currentPosition.col + it.second

            if (row in boardRange && col in boardRange) {
                val targetPiece = boardState[row][col]
                if (targetPiece == null || targetPiece.type != type
                ) {
                    availableSquares.add(Position(row, col))
                }
            }
        }
        return availableSquares.toTypedArray()
    }
}