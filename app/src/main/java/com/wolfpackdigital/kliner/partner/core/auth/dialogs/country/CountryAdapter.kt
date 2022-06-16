package com.wolfpackdigital.kliner.partner.core.auth.dialogs.country

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.databinding.ItemCountryBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry

class CountryAdapter : BaseAdapter<ItemCountry, ItemCountryBinding>(
    R.layout.item_country, object : DiffUtil.ItemCallback<ItemCountry>() {
        override fun areItemsTheSame(oldItem: ItemCountry, newItem: ItemCountry) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ItemCountry, newItem: ItemCountry) =
            oldItem == newItem
    }
) {
    override fun bind(binding: ItemCountryBinding, item: ItemCountry) {}
}
