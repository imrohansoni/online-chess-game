package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.Constants.boardRange

typealias BoardState = Array<Array<ChessPiece?>>

class Pawn(override val type: Type) : ChessPiece {
    var moved = false
    override val piece = Piece.PAWN
    override val directions = when {
        type == Type.LIGHT && ChessBoardManager.playerType == Type.LIGHT -> {
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        }

        type == Type.LIGHT && ChessBoardManager.playerType == Type.DARK -> {
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))
        }

        type == Type.DARK && ChessBoardManager.playerType == Type.LIGHT -> {
            arrayOf(Pair(1, 0), Pair(2, 0), Pair(1, -1), Pair(1, 1))
        }

        else -> {
            arrayOf(Pair(-1, 0), Pair(-2, 0), Pair(-1, -1), Pair(-1, 1))
        }
    }
    override var fen = if (type == Type.LIGHT) "P" else "p"

    override fun calculatePossibleMoves(
        currentPosition: Position, boardState: BoardState
    ): Array<Position> {
        val availableSquares = mutableListOf<Position>()

        val row1 = currentPosition.row + directions[0].first
        val col1 = currentPosition.col + directions[0].second
        val position1 = Position(row1, col1)

        if (row1 in boardRange && col1 in boardRange) {
            if (boardState[row1][col1] == null) {
                availableSquares.add(position1)
            }
        }

        if (!moved) {
            val row2 = currentPosition.row + directions[1].first
            val col2 = currentPosition.col + directions[1].second

            if (row2 in boardRange && col2 in boardRange) {
                val position2 = Position(row2, col2)
                if (boardState[row1][col1] == null && boardState[row2][col2] == null) {
                    availableSquares.add(position2)
                }
            }
        }

        val row3 = currentPosition.row + directions[2].first
        val col3 = currentPosition.col + directions[2].second

        if (row3 in boardRange && col3 in boardRange) {
            val position3 = Position(row3, col3)
            boardState[row3][col3]?.let {
                if (it.type != type) {
                    availableSquares.add(position3)
                }
            }
        }

        val row4 = currentPosition.row + directions[3].first
        val col4 = currentPosition.col + directions[3].second

        if (row4 in boardRange && col4 in boardRange) {
            val position4 = Position(row4, col4)
            boardState[row4][col4]?.let {
                if (it.type != type) {
                    availableSquares.add(position4)
                }
            }
        }

        return availableSquares.toTypedArray()
    }
}