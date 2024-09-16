package com.imrohansoni.chess.utils

import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.pieces.Bishop
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Knight
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.pieces.Piece
import com.imrohansoni.chess.pieces.Queen
import com.imrohansoni.chess.pieces.Rook
import com.imrohansoni.chess.utils.Constant.boardRange

typealias BoardMatrix = Array<Array<Piece?>>

fun findAvailableSquares(
    piece: Piece,
    currentPosition: Position,
    boardMatrix: BoardMatrix
): List<Position> {
    val availableSquares = mutableListOf<Position>()
    val (color, directions) = piece

    if (piece is Pawn) {
        val row1 = currentPosition.row + directions[0].first
        val col1 = currentPosition.col + directions[0].second
        val position1 = Position(row1, col1)

        if (row1 in boardRange && col1 in boardRange) {
            if (boardMatrix[row1][col1] == null) {
                availableSquares.add(position1)
            }
        }

        if (!piece.moved) {
            val row2 = currentPosition.row + directions[1].first
            val col2 = currentPosition.col + directions[1].second

            if (row2 in boardRange && col2 in boardRange) {
                val position2 = Position(row2, col2)
                if (boardMatrix[row1][col1] == null && boardMatrix[row2][col2] == null) {
                    availableSquares.add(position2)
                }
            }
        }

        listOf(directions[2], directions[3]).forEach { (row, col) ->
            val newRow = currentPosition.row + row
            val newCol = currentPosition.col + col

            if (newRow in boardRange && newCol in boardRange) {
                val position = Position(newRow, newCol)
                val targetPiece = boardMatrix[newRow][newCol]
                if (targetPiece != null && targetPiece.color != color) {
                    availableSquares.add(position)
                }
            }
        }
    }

    if (piece is Knight || piece is King) {
        directions.forEach { direction ->
            val row = currentPosition.row + direction.first
            val col = currentPosition.col + direction.second

            if (row in boardRange && col in boardRange) {
                val targetPiece = boardMatrix[row][col]

                if (targetPiece == null || targetPiece.color != color) {
                    availableSquares.add(Position(row, col))
                }
            }
        }
    }

    if (piece is Queen || piece is Bishop || piece is Rook) {
        directions.forEach { direction ->
            var row = currentPosition.row
            var col = currentPosition.col

            while (true) {
                row += direction.first
                col += direction.second

                if (row !in boardRange || col !in boardRange) break

                val targetPiece = boardMatrix[row][col]
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
    }

    return availableSquares
}