package com.imrohansoni.chess.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.imrohansoni.chess.R

object ChessPieceBitmapProvider {
    fun getPieceBitmaps(context: Context): Map<String, Bitmap> {
        return mapOf(
            "dark_pawn" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_pawn),
            "dark_king" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_king),
            "dark_queen" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_queen),
            "dark_knight" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_knight),
            "dark_rook" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_rook),
            "dark_bishop" to BitmapFactory.decodeResource(context.resources, R.drawable.dark_bishop),
            "light_pawn" to BitmapFactory.decodeResource(context.resources, R.drawable.light_pawn),
            "light_king" to BitmapFactory.decodeResource(context.resources, R.drawable.light_king),
            "light_queen" to BitmapFactory.decodeResource(context.resources, R.drawable.light_queen),
            "light_knight" to BitmapFactory.decodeResource(context.resources, R.drawable.light_knight),
            "light_rook" to BitmapFactory.decodeResource(context.resources, R.drawable.light_rook),
            "light_bishop" to BitmapFactory.decodeResource(context.resources, R.drawable.light_bishop)
        )
    }
}

