package com.imrohansoni.chess.models

import android.graphics.Bitmap

data class PieceView(
    val bitmap: Bitmap,
    var row: Int,
    var col: Int,
    var x: Float,
    var y: Float,
    var isCaptured : Boolean = false
)