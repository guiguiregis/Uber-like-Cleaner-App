package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.CancelMissionBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.MissionDetailsViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CancelMissionBottomSheetDialog : BaseBottomSheetDialog<CancelMissionBinding, MissionDetailsViewModel>(
    R.layout.dialog_cancel_mission
) {
    override val viewModel by sharedViewModel<MissionDetailsViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                MissionDetailsViewModel.Command.CloseCancelDialog -> dismiss()
                else -> {
                }
            }
        }
    }

    companion object {

        fun getInstance() = CancelMissionBottomSheetDialog()
    }
}
