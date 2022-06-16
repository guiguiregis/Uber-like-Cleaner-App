package com.wolfpackdigital.kliner.partner.core.auth.dialogs

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.WorkWithKlinerBinding
import com.wolfpackdigital.kliner.partner.core.auth.confirmation.CodeConfirmationViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WorkWithKlinerBottomSheetDialog :
    BaseBottomSheetDialog<WorkWithKlinerBinding, CodeConfirmationViewModel>(
        R.layout.dialog_work_with_kliner
    ) {
    override val viewModel by sharedViewModel<CodeConfirmationViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }
}
