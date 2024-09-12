package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.PieceType
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.utils.Constants.boardRange


class Bishop(override val color: Color) : Piece {
    override val pieceType = PieceType.BISHOP
    override val directions = arrayOf(
        Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1)
    )
    override var fen = if (color == Color.LIGHT) "B" else "b"
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
                    if (targetPiece.color != color) {
                        availableSquares.add(Position(row, col))
                    }
                    break
                }
            }
        }

        return availableSquares.toTypedArray()
    }
}