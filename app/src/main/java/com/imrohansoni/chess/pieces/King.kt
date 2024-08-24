package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.Constants.boardRange

class King(override val type: Type) : ChessPiece {
    var moved = false
    override var fen = if (type == Type.LIGHT) "K" else "k"

    override val directions = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1),
        Pair(-1, -1), Pair(-1, 1),
        Pair(1, -1), Pair(1, 1)
    )
    override val piece = Piece.KING

    override fun calculatePossibleMoves(
        currentPosition: Position,
        boardState: BoardState
    ): Array<Position> {

        val availableSquares = mutableListOf<Position>()

        for (direction in directions) {
            val row = currentPosition.row + direction.first
            val col = currentPosition.col + direction.second

            if (row !in boardRange || col !in boardRange) continue

            val targetPiece = boardState[row][col]

            if (targetPiece == null || targetPiece.type != type) {
                availableSquares.add(Position(row, col))
            }
        }

        return availableSquares.toTypedArray()
    }

}