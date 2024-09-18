package com.imrohansoni.chess

import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.GameType
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.Position


enum class GameState {
    FINISHED,
    STARTED,
    CREATED
}

object GameManager {
    var selectedSquarePosition: Position? = null
    var safeSquares: List<Position> = listOf()
    var currentPlayerColor: Color = Color.LIGHT
    val moves: MutableList<Move> = mutableListOf()
    var currentMovePosition = -1
    var gameState: GameState = GameState.CREATED
    var halfMoves = 0
    var fullMoves = 1
    val gameType: GameType = GameType.PASS_AND_PLAY
    val boardPositionFenMap: HashMap<String, Int> = hashMapOf()
}