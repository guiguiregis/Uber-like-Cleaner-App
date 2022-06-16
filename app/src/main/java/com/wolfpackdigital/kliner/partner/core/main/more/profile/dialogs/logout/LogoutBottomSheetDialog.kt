package com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.logout

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.LogoutBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LogoutBottomSheetDialog :
    BaseBottomSheetDialog<LogoutBinding, LogoutViewModel>(
        R.layout.dialog_logout
    ) {
    override val viewModel by sharedViewModel<LogoutViewModel>(from = {
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
                LogoutViewModel.Command.CloseDialog -> dismiss()
                is LogoutViewModel.Command.ShowSnackBar -> snackBar(it.message)
            }
        }
    }
}
