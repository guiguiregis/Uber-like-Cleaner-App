package com.wolfpackdigital.kliner.partner.core.main.offers.assign

import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import com.wolfpackdigital.kliner.partner.AssignEmployeeItemBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile

class AssignEmployeeAdapter(
    private val onEmployeeSelected: (employee: CleanerProfile?) -> Unit
) : BaseAdapter<CleanerProfile, AssignEmployeeItemBinding>(
    R.layout.item_assign_employee, object : DiffUtil.ItemCallback<CleanerProfile>() {
        override fun areItemsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CleanerProfile, newItem: CleanerProfile) =
            oldItem == newItem
    }
) {

    private var selectedEmployee: CleanerProfile? = null

    override fun bind(binding: AssignEmployeeItemBinding, item: CleanerProfile) {

        binding.tvEmployeeName.setTextColor(
            getColor(
                binding.root.context, if (item == selectedEmployee) {
                    R.color.colorAccent
                } else
                    R.color.textColor
            )
        )

        binding.clRoot.setOnClickListener {
            selectedEmployee = if (selectedEmployee == item) null else item
            onEmployeeSelected(selectedEmployee)
            notifyDataSetChanged()
        }
    }
}
