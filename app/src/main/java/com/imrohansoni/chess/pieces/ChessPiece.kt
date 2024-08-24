package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.ChessBoardManager



interface ChessPiece {
    val piece: Piece
    val type: Type
    val directions: Array<Pair<Int, Int>>
    var fen: String

    fun calculatePossibleMoves(
        currentPosition: Position,
        boardState : Array<Array<ChessPiece?>>
    ): Array<Position>
}