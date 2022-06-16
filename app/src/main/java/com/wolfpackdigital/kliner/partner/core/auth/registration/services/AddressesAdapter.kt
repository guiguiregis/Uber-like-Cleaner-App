package com.wolfpackdigital.kliner.partner.core.auth.registration.services

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.ItemAddressBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem

class AddressesAdapter : BaseAdapter<AddressItem, ItemAddressBinding>(R.layout.item_address,
    object : DiffUtil.ItemCallback<AddressItem>() {
        override fun areItemsTheSame(oldItem: AddressItem, newItem: AddressItem) =
            oldItem.streetLine == newItem.streetLine

        override fun areContentsTheSame(oldItem: AddressItem, newItem: AddressItem) =
            oldItem == newItem
    }
) {

    override fun bind(binding: ItemAddressBinding, item: AddressItem) {}
}
