package com.imrohansoni.chess.utils

import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.pieces.Bishop
import com.imrohansoni.chess.pieces.IPiece
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Knight
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.pieces.Queen
import com.imrohansoni.chess.pieces.Rook

object ChessBoardManager {
    fun initializeBoard(): Array<Array<IPiece?>> {
        return arrayOf(
            arrayOf(
                Rook(Type.DARK),
                Knight(Type.DARK),
                Bishop(Type.DARK),
                Queen(Type.DARK),
                King(Type.DARK),
                Bishop(Type.DARK),
                Knight(Type.DARK),
                Rook(Type.DARK)
            ),
            arrayOf(
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK),
                Pawn(Type.DARK)
            ),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOfNulls(8),
            arrayOf(
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT),
                Pawn(Type.LIGHT)
            ),
            arrayOf(
                Rook(Type.LIGHT),
                Knight(Type.LIGHT),
                Bishop(Type.LIGHT),
                Queen(Type.LIGHT),
                King(Type.LIGHT),
                Bishop(Type.LIGHT),
                Knight(Type.LIGHT),
                Rook(Type.LIGHT)
            )
        )
    }
}
