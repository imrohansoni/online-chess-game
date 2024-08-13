package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

interface IPiece {
    val piece: Piece
    val type: Type
    val moves: Array<Pair<Int, Int>>
    var fen: String

    fun calculatePossibleMoves(
        row: Int,
        col: Int,
        chessBoard: Array<Array<IPiece?>>
    ): Array<Pair<Int, Int>>
}