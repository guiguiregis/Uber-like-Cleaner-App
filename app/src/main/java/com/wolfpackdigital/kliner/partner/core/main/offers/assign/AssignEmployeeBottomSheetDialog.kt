package com.wolfpackdigital.kliner.partner.core.main.offers.assign

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wolfpackdigital.kliner.partner.DialogAssignEmployeeBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AssignEmployeeBottomSheetDialog :
    BaseBottomSheetDialog<DialogAssignEmployeeBinding, AssignEmployeeViewModel>(
        R.layout.dialog_assign_employee
    ) {

    override val viewModel by viewModel<AssignEmployeeViewModel> {
        parametersOf(args.missionID, args.isRecurrentMission, args.isRecurrenceChange)
    }

    private val args by navArgs<AssignEmployeeBottomSheetDialogArgs>()

    private val adapter by lazy {
        AssignEmployeeAdapter(viewModel::onEmployeeSelected)
    }

    private val behavior
        get() = (dialog as? BottomSheetDialog)?.behavior

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setupObservers()
    }

    private fun setup() {
        isCancelable = false

        dialogBinding?.rvEmployees?.apply {
            adapter = this@AssignEmployeeBottomSheetDialog.adapter
        }

        dialog?.setOnShowListener {
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.isDraggable = false
            behavior?.peekHeight =
                dialogBinding?.root?.height ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
        }
    }

    private fun setupObservers() {
        viewModel.cleaners.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            dialogBinding?.root?.invalidate()
        }
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is AssignEmployeeViewModel.Command.ShowMessage -> navController?.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.SHOW_MESSAGE, it.message)
                is AssignEmployeeViewModel.Command.CloseDialog -> dismiss()
                AssignEmployeeViewModel.Command.OpenInviteEmployee -> navController?.navigate(
                    R.id.addEmployeeFragment,
                    bundleOf(NavigationKeys.ARG_IS_ONBOARDING to false)
                )
                is AssignEmployeeViewModel.Command.OpenChangeRecurrence ->
                    navController?.navigate(
                        R.id.changeMissionRecurrenceBottomSheetDialog,
                        bundleOf(
                            NavigationKeys.ARG_MISSION_ID to (it.missionId),
                            NavigationKeys.ARG_CLEANER_ID to (it.employeeId)
                        )
                    )
                AssignEmployeeViewModel.Command.RefreshMissionDetails -> navController?.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.REFRESH_MISSION, true)
            }
        }
    }
}
