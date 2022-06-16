package com.wolfpackdigital.kliner.partner.core.main.more.activity

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.ItemTransactionBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.TransactionItem

class TransactionsAdapter : BaseAdapter<TransactionItem, ItemTransactionBinding>(
    R.layout.item_transaction, object : DiffUtil.ItemCallback<TransactionItem>() {
        override fun areItemsTheSame(oldItem: TransactionItem, newItem: TransactionItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TransactionItem, newItem: TransactionItem) =
            oldItem == newItem
    }
) {
    override fun bind(binding: ItemTransactionBinding, item: TransactionItem) {
    }
}
