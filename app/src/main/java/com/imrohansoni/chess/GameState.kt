package com.imrohansoni.chess

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.GameType
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.Position
import java.util.Stack

object GameState {
    var selectedSquarePosition: Position? = null
    var safeSquares: List<Position> = listOf()
    var currentPlayerColor: Color = Color.LIGHT
    val undoStack: Stack<Move> = Stack()
    var redoStack: Stack<Move> = Stack()
    val gameType : GameType = GameType.PASS_AND_PLAY
}