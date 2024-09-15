package com.imrohansoni.chess

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import com.imrohansoni.chess.databinding.DialogPromotionBinding
import com.imrohansoni.chess.models.Color
import com.imrohansoni.chess.models.PieceType

class PromotionDialog(
    private val context: Context,
    private val color: Color,
    private val onPieceSelected: (pieceType: PieceType) -> Unit
) : Dialog(context) {
    private val layoutInflater = LayoutInflater.from(context)
    private val binding = DialogPromotionBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val (queen, rook, bishop, knight) = if (color == Color.LIGHT) {
            listOf(
                R.drawable.light_queen,
                R.drawable.light_rook,
                R.drawable.light_bishop,
                R.drawable.light_knight
            )
        } else {
            listOf(
                R.drawable.dark_queen,
                R.drawable.dark_rook,
                R.drawable.dark_bishop,
                R.drawable.dark_knight
            )
        }

        binding.queenImageButton.setImageDrawable(AppCompatResources.getDrawable(context, queen))
        binding.rookImageButton.setImageDrawable(AppCompatResources.getDrawable(context, rook))
        binding.bishopImageButton.setImageDrawable(AppCompatResources.getDrawable(context, bishop))
        binding.knightImageButton.setImageDrawable(AppCompatResources.getDrawable(context, knight))

        binding.queenImageButton.setOnClickListener {
            onPieceSelected.invoke(PieceType.QUEEN)
            dismiss()
        }

        binding.rookImageButton.setOnClickListener {
            onPieceSelected.invoke(PieceType.ROOK)
            dismiss()
        }

        binding.bishopImageButton.setOnClickListener {
            onPieceSelected.invoke(PieceType.BISHOP)
            dismiss()
        }

        binding.knightImageButton.setOnClickListener {
            onPieceSelected.invoke(PieceType.KNIGHT)
            dismiss()
        }
    }
}