package com.wolfpackdigital.kliner.partner.core.main.adapters

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.BaseCheckboxBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption

class CheckboxAdapter :
    BaseAdapter<GeneralOption, BaseCheckboxBinding>(
        R.layout.base_checkbox,
        object : DiffUtil.ItemCallback<GeneralOption>() {
            override fun areItemsTheSame(oldItem: GeneralOption, newItem: GeneralOption) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GeneralOption, newItem: GeneralOption) =
                oldItem == newItem
        }) {

    override fun bind(binding: BaseCheckboxBinding, item: GeneralOption) {}
}
