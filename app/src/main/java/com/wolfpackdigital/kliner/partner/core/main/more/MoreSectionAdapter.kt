package com.wolfpackdigital.kliner.partner.core.main.more

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.ItemMoreSectionBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.MoreSectionItem

class MoreSectionAdapter : BaseAdapter<MoreSectionItem, ItemMoreSectionBinding>(
    R.layout.item_more_section, object : DiffUtil.ItemCallback<MoreSectionItem>() {
        override fun areItemsTheSame(oldItem: MoreSectionItem, newItem: MoreSectionItem) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: MoreSectionItem, newItem: MoreSectionItem) =
            oldItem == newItem
    }
) {
    override fun bind(binding: ItemMoreSectionBinding, item: MoreSectionItem) {
    }
}
