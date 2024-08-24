package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.Constants.boardRange


class Rook(override val type: Type) : ChessPiece {
    var moved = false

    override val piece = Piece.ROOK
    override val directions = arrayOf(
        Pair(-1, 0), Pair(1, 0),
        Pair(0, -1), Pair(0, 1)
    )
    override var fen = if (type == Type.LIGHT) "R" else "r"

    override fun calculatePossibleMoves(
        currentPosition: Position,
        boardState: BoardState
    ): Array<Position> {
        val availableSquares = mutableListOf<Position>()

        directions.forEach {
            var row = currentPosition.row
            var col = currentPosition.col

            while (true) {
                row += it.first
                col += it.second

                if (row !in boardRange || col !in boardRange) break

                val targetPiece = boardState[row][col]

                if (targetPiece == null) {
                    availableSquares.add(Position(row, col))

                } else {
                    if (targetPiece.type != type) {
                        availableSquares.add(Position(row, col))
                    }
                    break
                }
            }
        }
        return availableSquares.toTypedArray()
    }
}