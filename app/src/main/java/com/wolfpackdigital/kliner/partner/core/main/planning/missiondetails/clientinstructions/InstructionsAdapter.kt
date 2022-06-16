package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.clientinstructions

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.ClientInstructionItemBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter

class InstructionsAdapter :
    BaseAdapter<String, ClientInstructionItemBinding>(R.layout.item_client_instruction,
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
        }) {
    override fun bind(binding: ClientInstructionItemBinding, item: String) {}
}
