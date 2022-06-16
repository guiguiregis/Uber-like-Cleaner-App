package com.wolfpackdigital.kliner.partner.core.main.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.ItemTipBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.TipItem

class TipsAdapter : BaseAdapter<TipItem, ItemTipBinding>(
    R.layout.item_tip, object : DiffUtil.ItemCallback<TipItem>() {
        override fun areItemsTheSame(oldItem: TipItem, newItem: TipItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TipItem, newItem: TipItem) =
            oldItem == newItem
    }
) {
    override fun bind(binding: ItemTipBinding, item: TipItem) {
    }
}
