package com.imrohansoni.chess.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.imrohansoni.chess.R

object ChessPieceBitmapProvider {
    private fun scaledBitMap(
        bitmap: Bitmap, side: Int
    ): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, side, side, false)
    }

    fun getPieceBitmaps(context: Context, side: Int): Map<String, Bitmap> {
        return mapOf(
            "p" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_pawn
                ), side
            ),
            "k" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_king
                ), side
            ),
            "q" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_queen
                ), side
            ),
            "n" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_knight
                ), side
            ),
            "r" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_rook
                ), side
            ),
            "b" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.dark_bishop
                ), side
            ),
            "P" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_pawn
                ), side
            ),
            "K" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_king
                ), side
            ),
            "Q" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_queen
                ), side
            ),
            "N" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_knight
                ), side
            ),
            "R" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_rook
                ), side
            ),
            "B" to scaledBitMap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.light_bishop
                ), side
            )
        )
    }
}

