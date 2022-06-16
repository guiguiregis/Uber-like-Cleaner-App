package com.wolfpackdigital.kliner.partner.core.main.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.airbnb.paris.extensions.style
import com.wolfpackdigital.kliner.partner.MissionItemBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.utils.bindingadapters.textRes
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class MissionsAdapter(private val onMissionSelected: (Mission) -> Unit = {}) :
    BaseAdapter<Mission, MissionItemBinding>(R.layout.item_mission,
        object : DiffUtil.ItemCallback<Mission>() {
            override fun areItemsTheSame(oldItem: Mission, newItem: Mission) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Mission, newItem: Mission) =
                oldItem == newItem
        }), PersistenceService {
    override fun bind(binding: MissionItemBinding, item: Mission) {
        binding.root.setOnClickListener { onMissionSelected(item) }
        when {
            cleanerProfile?.isEmployee() == true -> {
                handleMissionForEmployees(item, binding)
            }
            else -> {
                when {
                    item.isCancelled -> {
                        binding.tvPrice.style(R.style.TextView_Price_Cancelled)
                        binding.tvPrice.textRes(R.string.cancelled)
                    }
                    else -> {
                        binding.tvPrice.style(R.style.TextView_Price)
                        binding.tvPrice.text = item.price
                    }
                }
            }
        }
    }

    private fun handleMissionForEmployees(
        item: Mission,
        binding: MissionItemBinding
    ) {
        val priceStyle: Int
        val priceTextRes: Int
        when {
            item.isCancelled -> {
                priceStyle = R.style.TextView_Price_Cancelled
                priceTextRes = R.string.cancelled
            }
            item.isConfirmed -> {
                priceStyle = R.style.TextView_Price_Approved
                priceTextRes = R.string.approved
            }
            item.isInProgress -> {
                priceStyle = R.style.TextView_Price_InProgress
                priceTextRes = R.string.in_progress
            }
            item.isFinished -> {
                priceStyle = R.style.TextView_Price_Approved
                priceTextRes = R.string.finished
            }
            else -> {
                priceStyle = R.style.TextView_Price_Pending
                priceTextRes = R.string.pending
            }
        }
        binding.tvPrice.style(priceStyle)
        binding.tvPrice.textRes(priceTextRes)
    }
}
