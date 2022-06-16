package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.StartMissionBinding
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.MissionDetailsViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.extraNotNull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StartMissionBottomSheetDialog :
    BaseBottomSheetDialog<StartMissionBinding, MissionDetailsViewModel>(
        R.layout.dialog_start_mission
    ) {
    override val viewModel by sharedViewModel<MissionDetailsViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    val type by extraNotNull<DialogType>(DIALOG_TYPE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogBinding?.type = type
        isCancelable = false
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                MissionDetailsViewModel.Command.CloseStartDialog -> dismiss()
                else -> {
                }
            }
        }
    }

    companion object {
        const val DIALOG_TYPE = "DIALOG_TYPE"

        fun getInstance(type: DialogType): StartMissionBottomSheetDialog {
            val args = bundleOf(DIALOG_TYPE to type)
            return StartMissionBottomSheetDialog().apply {
                arguments = args
            }
        }
    }

    enum class DialogType {
        START,
        TOO_EARLY
    }
}
