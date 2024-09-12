package com.imrohansoni.chess.pieces

import com.imrohansoni.chess.models.PieceType
import com.imrohansoni.chess.models.Position
import com.imrohansoni.chess.models.Color

typealias BoardState = Array<Array<Piece?>>


interface Piece {
    val pieceType: PieceType
    val color: Color
    val directions: Array<Pair<Int, Int>>
    var fen: String

    fun calculatePossibleMoves(
        currentPosition: Position,
        boardState: BoardState
    ): Array<Position>
}