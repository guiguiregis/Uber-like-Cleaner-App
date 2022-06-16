package com.wolfpackdigital.kliner.partner.shared.utils.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.wolfpackdigital.kliner.partner.IdCardViewBinding
import com.wolfpackdigital.kliner.partner.R

class IdCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding = IdCardViewBinding
        .inflate(LayoutInflater.from(context), this, true)
}

@BindingAdapter("idTextRes")
fun IdCardView.setIdTextRes(idTextRes: String?) {
    idTextRes ?: return
    binding.tvIdentity.text = idTextRes
}

@BindingAdapter(
    value = ["idEnabled", "idImage", "idImageDisabled", "idImageUri"],
    requireAll = true
)
fun IdCardView.setIdEnabled(
    idEnabled: Boolean?,
    idImage: Drawable?,
    idImageDisabled: Drawable?,
    idImageUri: Uri?
) {
    binding.tvIdentity.setTextColor(
        ContextCompat.getColor(
            context,
            if (idEnabled == true)
                R.color.colorAccent
            else
                R.color.textSecondaryColor
        )
    )

    idImageUri?.let {
        binding.ivIdentity.setPadding(0, 0, 0, 0)
        binding.ivIdentity.load(it) {
            crossfade(true)
        }
    } ?: let {
        binding.ivIdentity.setImageDrawable(
            if (idEnabled == true)
                idImage
            else
                idImageDisabled
        )
        val padding = resources.getDimension(R.dimen.margin_12dp).toInt()
        binding.ivIdentity.setPadding(padding, padding, padding, padding)
    }
}
