package com.wolfpackdigital.kliner.partner.core.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wolfpackdigital.kliner.partner.EmployeeDetailsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.adapters.CheckboxAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EmployeeDetailsDialog :
    BaseBottomSheetDialog<EmployeeDetailsBinding, EmployeeDetailsViewModel>(
        R.layout.dialog_employee_details
    ) {
    override val viewModel by viewModel<EmployeeDetailsViewModel> {
        parametersOf(args.id)
    }

    private val args by navArgs<EmployeeDetailsDialogArgs>()
    private val typesOfWorkAdapter by lazy { CheckboxAdapter() }

    private val behavior
        get() = (dialog as? BottomSheetDialog)?.behavior

    private val deleteEmployeeDialog by lazy { DeleteEmployeeDialog() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setupObservers()
    }

    private fun setup() {
        dialogBinding?.rvTypesOfWork?.adapter = typesOfWorkAdapter
        dialog?.setOnShowListener {
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.peekHeight =
                dialogBinding?.root?.height ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
        }
    }

    private fun setupObservers() {
        viewModel.typesOfWork.observe(viewLifecycleOwner, typesOfWorkAdapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                EmployeeDetailsViewModel.Command.CloseEmployeeDetailsDialog -> dismiss()
                is EmployeeDetailsViewModel.Command.LaunchIntent -> startActivity(it.intent)
                EmployeeDetailsViewModel.Command.OpenMenu -> deleteEmployeeDialog.show(
                    childFragmentManager,
                    DeleteEmployeeDialog::class.simpleName
                )
                is EmployeeDetailsViewModel.Command.ShowMessage -> dialogBinding?.clRoot?.toast(it.text)
                is EmployeeDetailsViewModel.Command.ShowMessageID -> dialogBinding?.clRoot?.toast(it.res)
                is EmployeeDetailsViewModel.Command.RefreshEmployeeList -> {
                    navController?.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.REFRESH_EMPLOYEES, true)
                }
            }
        }
        viewModel.selectedWorkTypes.observe(viewLifecycleOwner) {
            viewModel.updateWorkTypes(it ?: listOf())
        }
    }
}
