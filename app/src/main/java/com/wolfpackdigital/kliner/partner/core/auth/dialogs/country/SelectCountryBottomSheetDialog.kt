package com.wolfpackdigital.kliner.partner.core.auth.dialogs.country

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wolfpackdigital.kliner.partner.DialogSelectCountryBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectCountryBottomSheetDialog :
    BaseBottomSheetDialog<DialogSelectCountryBinding, SelectCountryViewModel>(
        R.layout.dialog_select_country
    ) {

    override val viewModel by sharedViewModel<SelectCountryViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    private val adapter by lazy { CountryAdapter() }
    private val behavior
        get() = (dialog as? BottomSheetDialog)?.behavior

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setupObservers()
    }

    private fun setup() {
        isCancelable = false
        dialogBinding?.rvCountries?.apply {
            adapter = this@SelectCountryBottomSheetDialog.adapter
        }
        dialog?.setOnShowListener {
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.peekHeight =
                dialogBinding?.root?.height ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
        }
    }

    private fun setupObservers() {
        viewModel.countries.observe(viewLifecycleOwner) {}
        viewModel.filteredCountries.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            dialogBinding?.root?.invalidate()
        }
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is SelectCountryViewModel.Command.CloseDialog -> dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.clearQuery()
    }
}
