package com.imrohansoni.chess

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.imrohansoni.chess.models.Move
import com.imrohansoni.chess.models.PieceView
import com.imrohansoni.chess.models.Square
import com.imrohansoni.chess.models.Type
import com.imrohansoni.chess.pieces.King
import com.imrohansoni.chess.pieces.Pawn
import com.imrohansoni.chess.utils.ChessBoardManager
import com.imrohansoni.chess.utils.ChessPieceBitmapProvider


class ChessBoard(private val context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private val moves = mutableListOf<Move>()

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = DisplayMetrics()

    private val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowsMatrix = windowManager.currentWindowMetrics
        val bounds = windowsMatrix.bounds
        val width = bounds.width()
        width
    } else {
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
    private var currentPlayerType = Type.LIGHT

    private val squareSize = width / 8

    private val lightSquarePaint = Paint().apply {
        color = context.getColor(R.color.lightSquare)
    }

    private val darkSquarePaint = Paint().apply {
        color = context.getColor(R.color.darkSquare)
    }

    private val pieceBitmaps = ChessPieceBitmapProvider.getPieceBitmaps(context, squareSize)
    private val boardState = ChessBoardManager.initializeBoard()
    private var currentlySelectedSquare: Square? = null
    private var availableMoves: Array<Pair<Int, Int>>? = null
    private val pieceViews = mutableListOf<PieceView>()

    init {
        initializePieceViews()
    }


    private val squares = Array(8) { row ->
        Array(8) { col ->
            Square(row, col, squareSize)
        }
    }


    private fun initializePieceViews() {
        for (row in boardState.indices) {
            for (col in boardState[row].indices) {
                boardState[row][col]?.let { piece ->
                    pieceBitmaps[piece.fen]?.let { bitmap ->
                        val x = (squareSize * row).toFloat()
                        val y = (squareSize * col).toFloat()
                        val pieceView = PieceView(bitmap, row, col, x, y)
                        pieceViews.add(pieceView)
                    }
                }
            }
        }
    }

    private fun animatePiece(piece: PieceView, endRow: Int, endCol: Int) {
        val startX = piece.x
        val startY = piece.y

        val endX = (squareSize * endRow).toFloat()
        val endY = (squareSize * endCol).toFloat()

        val animatorX = ValueAnimator.ofFloat(startX, endX)
        val animatorY = ValueAnimator.ofFloat(startY, endY)


        val animatorSet = AnimatorSet().apply {
            playTogether(animatorX, animatorY)
            duration = 200L
        }

        animatorX.addUpdateListener {
            piece.x = it.animatedValue as Float
            invalidate()
        }

        animatorY.addUpdateListener {
            piece.y = it.animatedValue as Float
            invalidate()
        }

        animatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                piece.row = endRow
                piece.col = endCol
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    private fun moveSelectedPiece(x: Float, y: Float) {

        currentlySelectedSquare?.let {
            val (endRow, endCol) = getPiecePosition(x, y)

            val startRow = currentlySelectedSquare!!.row
            val startCol = currentlySelectedSquare!!.col

            if (availableMoves?.contains(Pair(endRow, endCol)) == true) {
                val canMove =
                    boardState[startRow][startCol].let { piece -> piece?.type == currentPlayerType }
                if (!canMove) {
                    return selectSquare(x, y)
                }

                val pieceView = pieceViews.find { p ->
                    p.row == startRow && p.col == startCol
                }

                val capturedPieceView = pieceViews.find { p ->
                    p.row == endRow && p.col == endCol
                }

                pieceView?.let {
                    animatePiece(pieceView, endRow, endCol)
                    pieceView.row = endRow
                    pieceView.col = endCol
                }

                if (capturedPieceView != null) {
                    pieceViews.remove(capturedPieceView)
                }

                val piece = boardState[startRow][startCol]
                boardState[startRow][startCol] = null
                boardState[endRow][endCol] = piece


                it.isSelected = false

                resetSquareFlags()
                resetMovedFlags()
                squares[startRow][startCol].movedSquare = true
                squares[endRow][endCol].movedSquare = true


                if (piece is Pawn) {
                    piece.moved = true
                }

                if (piece is King) {
                    piece.moved = true
                }

                currentPlayerType = if (currentPlayerType == Type.LIGHT) Type.DARK else Type.LIGHT

                availableMoves = null
                currentlySelectedSquare = null

                moves.add(
                    Move(
                        piece!!.piece,
                        currentPlayerType,
                        startRow = startRow,
                        startCol,
                        endRow,
                        endCol
                    )
                )

                invalidate()
            } else {
                if (boardState[endRow][endCol] != null) {
                    selectSquare(x, y)
                }
            }
        }
    }

    private fun getPiecePosition(x: Float, y: Float): Array<Int> {
        val row = (y / squareSize).toInt().coerceIn(0, 7)
        val col = (x / squareSize).toInt().coerceIn(0, 7)
        return arrayOf(row, col)
    }

    private fun resetMovedFlags() {
        for (row in squares.indices) {
            for (col in squares[row].indices) {
                squares[row][col].movedSquare = false
            }
        }
    }

    private fun resetSquareFlags() {
        for (row in squares.indices) {
            for (col in squares[row].indices) {
                squares[row][col].isAvailable = false
                squares[row][col].canBeCaptured = false
            }
        }
    }

    private fun selectSquare(x: Float, y: Float) {
        val (row, col) = getPiecePosition(x, y)

        if (row !in 0..7 || col !in 0..7) return

        if (boardState[row][col] == null) {
            return
        }

        val selectedSquare = squares[row][col]

        if (this.currentlySelectedSquare == selectedSquare) {
            return
        }

        this.currentlySelectedSquare?.isSelected = false

        resetSquareFlags()

        this.currentlySelectedSquare = selectedSquare
        currentlySelectedSquare?.isSelected = true

        availableMoves =
            boardState[row][col]?.calculatePossibleMoves(row, col, boardState)

        availableMoves?.forEach {
            squares[it.first][it.second].isAvailable = true
            if (boardState[it.first][it.second] != null) {
                squares[it.first][it.second].canBeCaptured = true
            }
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in squares.indices) {
            for (col in squares[row].indices) {
                val paint = if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
                val square = squares[row][col]

                square.draw(canvas, paint)
            }

        }

        pieceViews.forEach {
            if (!it.isCaptured) {
                canvas.drawBitmap(it.bitmap, it.y, it.x, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (currentlySelectedSquare != null) {
                    moveSelectedPiece(event.x, event.y)
                } else {
                    selectSquare(event.x, event.y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
            }

            MotionEvent.ACTION_UP -> {

            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun moveToFirstMove() {
        TODO("Not yet implemented")
    }

    fun moveToLastMove() {
        TODO("Not yet implemented")
    }

    fun moveToPreviousMove() {
        TODO("Not yet implemented")
    }

    fun moveToNextMove() {
        TODO("Not yet implemented")
    }
}