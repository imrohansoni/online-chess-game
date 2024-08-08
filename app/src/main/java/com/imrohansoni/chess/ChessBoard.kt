package com.imrohansoni.chess

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.imrohansoni.chess.models.ChessPiece
import com.imrohansoni.chess.models.Piece
import com.imrohansoni.chess.models.Type

class ChessBoard(private val context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private val lightSquarePaint = Paint().apply {
        color = Color.parseColor("#D5ACFF")
    }

    private val pieces = arrayOf(
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
            ChessPiece(Piece.PAWN, Type.DARK),

            ),
        arrayOfNulls<ChessPiece>(8),
        arrayOfNulls<ChessPiece>(8),
        arrayOfNulls<ChessPiece>(8),
        arrayOfNulls<ChessPiece>(8),
        arrayOf(
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),
            ChessPiece(Piece.PAWN, Type.LIGHT),

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

    private val darkSquarePaint = Paint().apply {
        color = Color.parseColor("#7F00FF")
    }

    private val gap = 40

    private val displayMetrics = DisplayMetrics()

    private val piecesBitmap = mutableMapOf<String, Bitmap>()

    init {
        piecesBitmap["dark_pawn"] = BitmapFactory.decodeResource(resources, R.drawable.dark_pawn)
        piecesBitmap["dark_king"] = BitmapFactory.decodeResource(resources, R.drawable.dark_king)
        piecesBitmap["dark_queen"] = BitmapFactory.decodeResource(resources, R.drawable.dark_queen)
        piecesBitmap["dark_knight"] =
            BitmapFactory.decodeResource(resources, R.drawable.dark_knight)
        piecesBitmap["dark_rook"] = BitmapFactory.decodeResource(resources, R.drawable.dark_rook)
        piecesBitmap["dark_bishop"] =
            BitmapFactory.decodeResource(resources, R.drawable.dark_bishop)


        piecesBitmap["light_pawn"] = BitmapFactory.decodeResource(resources, R.drawable.light_pawn)
        piecesBitmap["light_king"] = BitmapFactory.decodeResource(resources, R.drawable.light_king)
        piecesBitmap["light_queen"] =
            BitmapFactory.decodeResource(resources, R.drawable.light_queen)
        piecesBitmap["light_knight"] =
            BitmapFactory.decodeResource(resources, R.drawable.light_knight)
        piecesBitmap["light_rook"] = BitmapFactory.decodeResource(resources, R.drawable.light_rook)
        piecesBitmap["light_bishop"] =
            BitmapFactory.decodeResource(resources, R.drawable.light_bishop)

    }

    private fun getPieceBitmap(chessPiece: ChessPiece): Bitmap? {
        val piece = "${chessPiece.type.type}_${chessPiece.piece.piece}"
        Log.d("CHESS_PIECES", piece)
        return piecesBitmap[piece]
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowsMatrix = windowManager.currentWindowMetrics
            val bounds = windowsMatrix.bounds
            val width = bounds.width()
            width
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

        val squareWidth = ((width - (gap * 2)) / 8)

        var currentIsLight = true
        var left = 0f
        var top = 0f
        var right = squareWidth.toFloat()
        var bottom = squareWidth.toFloat()
        for (row in 0..7) {
            for (col in 0..7) {
                canvas.drawRect(
                    left + gap,
                    top + gap,
                    right + gap,
                    bottom + gap,
                    if (currentIsLight) lightSquarePaint else darkSquarePaint
                )

                if(pieces[row][col] != null){
                    pieces[row][col]?.let {
                        val pieceBitmap = getPieceBitmap(it)
                        pieceBitmap?.let {
                            val scaledBitmap = Bitmap.createScaledBitmap(pieceBitmap, squareWidth, squareWidth, false)
                            canvas.drawBitmap(scaledBitmap, left + gap , top + gap, null)
                        }
                    }
                }


                left += squareWidth.toFloat()
                right += squareWidth.toFloat()
                currentIsLight = !currentIsLight
            }
            top += squareWidth.toFloat()
            bottom += squareWidth.toFloat()
            left = 0f
            right = squareWidth.toFloat()
            currentIsLight = !currentIsLight
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}