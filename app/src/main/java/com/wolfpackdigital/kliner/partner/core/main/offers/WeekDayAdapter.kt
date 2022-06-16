package com.wolfpackdigital.kliner.partner.core.main.offers

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.WeekDayBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.WeekDayItem

class WeekDayAdapter : BaseAdapter<WeekDayItem, WeekDayBinding>(
    R.layout.item_weekday, object : DiffUtil.ItemCallback<WeekDayItem>() {
        override fun areItemsTheSame(oldItem: WeekDayItem, newItem: WeekDayItem) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: WeekDayItem, newItem: WeekDayItem) =
            oldItem == newItem
    }
) {
    override fun bind(binding: WeekDayBinding, item: WeekDayItem) {
    }
}
