package com.imrohansoni.chess.models

enum class Type {
    LIGHT,
    DARK
}

fun Type.opposite(): Type {
    return if (this == Type.LIGHT) Type.DARK else Type.LIGHT
}