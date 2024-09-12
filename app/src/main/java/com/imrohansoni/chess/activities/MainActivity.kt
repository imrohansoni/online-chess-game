package com.imrohansoni.chess

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imrohansoni.chess.adapters.CapturedPiecesAdapter
import com.imrohansoni.chess.adapters.MovesAdapter
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.utils.ChessBoardManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChessBoardManager.initializeBoard(Type.LIGHT)

        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val chessBoard = findViewById<ChessBoard>(R.id.chess_board)
        val chessBoardView = findViewById<ChessBoardView>(R.id.chess_board_view)
        val previousButton = findViewById<ImageButton>(R.id.previous_button)
        val nextButton = findViewById<ImageButton>(R.id.next_button)
        val movesRecyclerView = findViewById<RecyclerView>(R.id.moves_recycler_view)

        val playerOneCapture = findViewById<RecyclerView>(R.id.player_1_captures)
        val playerTwoCapture = findViewById<RecyclerView>(R.id.player_2_captures)

        val playerOneCaptureAdapter = CapturedPiecesAdapter(mutableListOf())
        val playerTwoCaptureAdapter = CapturedPiecesAdapter(mutableListOf())


        playerOneCapture.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        playerTwoCapture.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)

        playerOneCapture.adapter = playerOneCaptureAdapter
        playerTwoCapture.adapter = playerTwoCaptureAdapter

        chessBoardView.setOnPieceCaptureHandler {
            when (it.type) {
                Type.LIGHT -> {
                    playerOneCaptureAdapter.addCapturedPiece(it)
                }
                Type.DARK -> {
                    playerTwoCaptureAdapter.addCapturedPiece(it)
                }
            }
        }


        movesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = MovesAdapter(mutableListOf())

        movesRecyclerView.adapter = adapter

        chessBoardView.setOnPieceMoveHandler {
            adapter.addMoves(it)
            movesRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        previousButton.setOnClickListener {
            chessBoardView.controller.moveToPreviousMove()
        }

        nextButton.setOnClickListener {
            chessBoardView.controller.moveToNextMove()
        }
    }
}