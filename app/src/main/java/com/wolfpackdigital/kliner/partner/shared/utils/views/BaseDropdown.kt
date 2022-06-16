package com.wolfpackdigital.kliner.partner.shared.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.wolfpackdigital.kliner.partner.BaseDropdownBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.Rank
import com.wolfpackdigital.kliner.partner.shared.utils.bindingadapters.textColor
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.safeValueOf

class BaseDropdown @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val binding =
        BaseDropdownBinding.inflate(LayoutInflater.from(context), this, true)
}

@Suppress("setKlDropdownSelection")
@BindingAdapter("klDropdownSelection")
fun BaseDropdown.setKlDropdownSelection(text: String?) {
    text ?: return
    binding.etDropdown.apply {
        (0 until adapter.count).forEach { i ->
            if (adapter.getItem(i) == text)
                if (i != selectedItemPosition) {
                    setSelection(i)
                    return@apply
                }
        }
    }
}

@InverseBindingAdapter(attribute = "klDropdownSelection")
fun BaseDropdown.getKlDropdownSelection(): String? = binding.etDropdown.selectedItem as? String

@BindingAdapter("klDropdownSelectionAttrChanged")
fun BaseDropdown.setListener(attrChange: InverseBindingListener) {
    binding.etDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            attrChange.onChange()
            (parent?.getChildAt(0) as? TextView)?.let {
                when (safeValueOf<Rank>(it.text.toString())) {
                    Rank.Gold -> it.textColor(R.color.rank_gold)
                    Rank.Silver -> it.textColor(R.color.rank_silver)
                    Rank.Bronze -> it.textColor(R.color.rank_bronze)
                    else -> it.textColor(R.color.textColor)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}

@BindingAdapter("klDropdownList")
fun BaseDropdown.setKlDropdownList(items: List<Int>?) {
    val resolvedItems = mutableListOf<String>()
    items?.map(context.resources::getString)?.let(resolvedItems::addAll)
    binding.etDropdown.adapter = ArrayAdapter(context, R.layout.dropdown_item, resolvedItems)
}

@BindingAdapter("klDropdownListRaw")
fun BaseDropdown.setKlDropdownListRaw(items: List<GeneralOption>?) {
    val resolvedItems = mutableListOf<String>()
    items?.map(GeneralOption::name)?.let(resolvedItems::addAll)
    binding.etDropdown.adapter = ArrayAdapter(context, R.layout.dropdown_item, resolvedItems)
}

@BindingAdapter("klDropdownEnabled")
fun BaseDropdown.setKlDropdownEnabled(enabled: Boolean?) {
    binding.etDropdown.isEnabled = enabled ?: true
}

@BindingAdapter("klDropdownRankFilter")
fun BaseDropdown.setKlDropdownRankFilter(rank: String?) {
    when (safeValueOf<Rank>(rank)) {
        Rank.Gold,
        Rank.Silver,
        Rank.Bronze -> {
            binding.clDropdown.background =
                ContextCompat.getDrawable(context, R.drawable.bg_spinner_rank)
            binding.etBg.cardElevation = 0f
            binding.etBg.radius = resources.getDimension(R.dimen.btn_corner_radius_small)
            binding.etDropdown.setPopupBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_spinner_rank
                )
            )
        }
        else -> {
            binding.clDropdown.background = null
            binding.etBg.cardElevation = resources.getDimension(R.dimen.edit_text_elevation)
            binding.etBg.radius = resources.getDimension(R.dimen.btn_corner_radius)
            binding.etDropdown.setPopupBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_spinner
                )
            )
        }
    }
}
