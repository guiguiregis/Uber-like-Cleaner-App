package com.wolfpackdigital.kliner.partner.core.auth.dialogs.contract

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wolfpackdigital.kliner.partner.DialogSignContractBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignContractBottomSheetDialog :
    BaseBottomSheetDialog<DialogSignContractBinding, SignContractViewModel>(
        R.layout.dialog_sign_contract
    ) {

    override val viewModel by sharedViewModel<SignContractViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

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
            behavior?.peekHeight =
                dialogBinding?.root?.height ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
        }
        dialogBinding?.btnViewContract?.paint?.isUnderlineText = true
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                SignContractViewModel.Command.CloseDialog -> dismiss()
                is SignContractViewModel.Command.ShowContract -> {
                    val builder = CustomTabsIntent.Builder().apply {
                        setToolbarColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAccent
                            )
                        )
                    }
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(requireContext(), Uri.parse(it.url))
                }
                is SignContractViewModel.Command.ShowSnackBar -> dialogBinding?.root?.snackBar(
                    it.message
                )
            }
        }
    }
}
