package com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.delete

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.DeleteAccountBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeleteAccountBottomSheetDialog :
    BaseBottomSheetDialog<DeleteAccountBinding, DeleteAccountViewModel>(
        R.layout.dialog_delete_account
    ) {
    override val viewModel by sharedViewModel<DeleteAccountViewModel>(from = {
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
                DeleteAccountViewModel.Command.CloseDialog -> dismiss()
                is DeleteAccountViewModel.Command.ShowSnackBar -> snackBar(it.message)
            }
        }
    }
}
