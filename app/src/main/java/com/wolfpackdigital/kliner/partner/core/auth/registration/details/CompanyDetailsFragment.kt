package com.wolfpackdigital.kliner.partner.core.auth.registration.details

import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelStoreOwner
import com.wolfpackdigital.kliner.partner.CompanyDetailsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.registration.RegistrationViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyDetailsFragment :
    BaseFragment<CompanyDetailsBinding, CompanyDetailsViewModel>(R.layout.fr_company_details) {

    // Properties

    override val viewModel by viewModel<CompanyDetailsViewModel>()
    private val parentViewModel by
    sharedViewModel<RegistrationViewModel>(from = { parentFragment as ViewModelStoreOwner })

    private val sirenInfoDialog by lazy {
        SirenInfoBottomSheetDialog.getInstance()
    }

    // Lifecycle

    override fun setupViews() {
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCompanyData()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is CompanyDetailsViewModel.Command.ShowSirenDialog -> {
                    sirenInfoDialog.show(
                        childFragmentManager,
                        SirenInfoBottomSheetDialog::class.simpleName
                    )
                }
                is CompanyDetailsViewModel.Command.GoNext ->
                    parentViewModel.goToNextPage()
            }
        }
        viewModel.suggestions.observe(viewLifecycleOwner) {
            binding?.etSearchAddress?.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    it.map { item -> item.streetLine })
            )
            binding?.etSearchAddress?.showDropDown()
        }
        binding?.etSearchAddress?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.addressSelected(position)
            }
    }

    companion object {
        fun newInstance() = CompanyDetailsFragment()
    }
}
