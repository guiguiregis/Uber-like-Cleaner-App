package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.TwillioInfoBinding
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.MissionDetailsViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TwillioInfoBottomSheetDialog :
    BaseBottomSheetDialog<TwillioInfoBinding, MissionDetailsViewModel>(
        R.layout.dialog_twillio_info
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
                MissionDetailsViewModel.Command.CloseTwillioInfoDialog -> dismiss()
                else -> {
                }
            }
        }
    }

    companion object {
        fun getInstance() = TwillioInfoBottomSheetDialog()
    }
}
