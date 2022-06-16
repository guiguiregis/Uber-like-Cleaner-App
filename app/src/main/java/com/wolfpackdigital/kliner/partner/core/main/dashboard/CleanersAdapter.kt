package com.wolfpackdigital.kliner.partner.core.main.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.CleanerBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile

class CleanersAdapter : BaseAdapter<CleanerProfile, CleanerBinding>(R.layout.item_cleaner, object :
    DiffUtil.ItemCallback<CleanerProfile>() {
    override fun areItemsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
        oldItem == newItem
}) {
    override fun bind(binding: CleanerBinding, item: CleanerProfile) {
    }
}
