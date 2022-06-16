package com.wolfpackdigital.kliner.partner.core.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.DeleteEmployeeBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeleteEmployeeDialog : BaseBottomSheetDialog<DeleteEmployeeBinding, EmployeeDetailsViewModel>(
    R.layout.dialog_delete_employee
) {
    override val viewModel by sharedViewModel<EmployeeDetailsViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                EmployeeDetailsViewModel.Command.CloseDeleteEmployeeDialog -> dismiss()
            }
        }
    }
}
