package com.wolfpackdigital.kliner.partner.core.auth.registration.details

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.SirenInfoBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SirenInfoBottomSheetDialog :
    BaseBottomSheetDialog<SirenInfoBinding, CompanyDetailsViewModel>(
        R.layout.dialog_siren_info
    ) {
    override val viewModel by sharedViewModel<CompanyDetailsViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        setUrlSpan()
        setupObservers()
    }

    private fun setUrlSpan() {
        val string = SpannableString(getString(R.string.siren_info_description))
        val websiteName = getString(R.string.siren_info_website_name)
        val indexStart = string.indexOf(websiteName, ignoreCase = true)
        val indexEnd = indexStart + websiteName.length
        string.setSpan(
            URLSpan(getString(R.string.siren_info_website_url)),
            indexStart,
            indexEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        dialogBinding?.tvDescription?.movementMethod = LinkMovementMethod()
        dialogBinding?.tvDescription?.text = string
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                CompanyDetailsViewModel.Command.CloseSirenDialog -> dismiss()
                else -> {
                }
            }
        }
    }

    companion object {

        fun getInstance() = SirenInfoBottomSheetDialog()
    }
}
