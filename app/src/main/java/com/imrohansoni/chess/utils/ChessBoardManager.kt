package com.imrohansoni.chess.utils

import com.imrohansoni.chess.models.ChessPiece
import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

object ChessBoardManager {

    fun initializeBoard(): Array<Array<ChessPiece?>> {
        return arrayOf(
            arrayOf(
                ChessPiece(Piece.ROOK, Type.DARK),
                ChessPiece(Piece.KNIGHT, Type.DARK),
                ChessPiece(Piece.BISHOP, Type.DARK),
                ChessPiece(Piece.QUEEN, Type.DARK),
                ChessPiece(Piece.KING, Type.DARK),
                ChessPiece(Piece.BISHOP, Type.DARK),
                ChessPiece(Piece.KNIGHT, Type.DARK),
                ChessPiece(Piece.ROOK, Type.DARK)
            ),
            arrayOf(
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK),
                ChessPiece(Piece.PAWN, Type.DARK)
            ),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOf(
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT),
                ChessPiece(Piece.PAWN, Type.LIGHT)
            ),
            arrayOf(
                ChessPiece(Piece.ROOK, Type.LIGHT),
                ChessPiece(Piece.KNIGHT, Type.LIGHT),
                ChessPiece(Piece.BISHOP, Type.LIGHT),
                ChessPiece(Piece.QUEEN, Type.LIGHT),
                ChessPiece(Piece.KING, Type.LIGHT),
                ChessPiece(Piece.BISHOP, Type.LIGHT),
                ChessPiece(Piece.KNIGHT, Type.LIGHT),
                ChessPiece(Piece.ROOK, Type.LIGHT)
            )
        )
    }
}
