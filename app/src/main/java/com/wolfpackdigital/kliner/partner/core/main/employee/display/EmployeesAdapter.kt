package com.wolfpackdigital.kliner.partner.core.main.employee.display

import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.EmployeeItemBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile

class EmployeesAdapter :
    BaseAdapter<CleanerProfile, EmployeeItemBinding>(R.layout.item_employee, object :
        DiffUtil.ItemCallback<CleanerProfile>() {
        override fun areItemsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
            oldItem == newItem
    }) {
    override fun bind(binding: EmployeeItemBinding, item: CleanerProfile) {
    }
}
