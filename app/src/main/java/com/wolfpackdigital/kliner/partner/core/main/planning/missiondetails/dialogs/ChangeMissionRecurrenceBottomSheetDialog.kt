package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wolfpackdigital.kliner.partner.ChangeMissionRecurrenceDialogBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeMissionRecurrenceBottomSheetDialog :
    BaseBottomSheetDialog<ChangeMissionRecurrenceDialogBinding, ChangeMissionRecurrenceViewModel>(
        R.layout.dialog_change_mission_recurrence
    ) {

    override val viewModel by viewModel<ChangeMissionRecurrenceViewModel> {
        parametersOf(args.missionID, args.cleanerID)
    }

    private val args by navArgs<ChangeMissionRecurrenceBottomSheetDialogArgs>()

    private val behavior
        get() = (dialog as? BottomSheetDialog)?.behavior

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setupObservers()
    }

    private fun setup() {
        isCancelable = false

        dialog?.setOnShowListener {
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.isDraggable = false
        }
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is ChangeMissionRecurrenceViewModel.Command.ShowMessage -> navController?.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.SHOW_MESSAGE, it.message)
                is ChangeMissionRecurrenceViewModel.Command.CloseDialog -> dismiss()
                ChangeMissionRecurrenceViewModel.Command.RefreshMissionDetails ->
                    navController?.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.REFRESH_MISSION, true)
            }
        }
    }
}
