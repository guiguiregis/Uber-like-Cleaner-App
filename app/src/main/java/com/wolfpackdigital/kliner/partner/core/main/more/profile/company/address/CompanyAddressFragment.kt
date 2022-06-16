package com.wolfpackdigital.kliner.partner.core.main.more.profile.company.address

import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.wolfpackdigital.kliner.partner.CompanyAddressBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyAddressFragment :
    BaseFragment<CompanyAddressBinding, CompanyAddressViewModel>(R.layout.fr_company_address) {

    // Properties

    override val viewModel by viewModel<CompanyAddressViewModel>()

    // Lifecycle

    override fun setupViews() {
        setupObservers()
    }

    private fun setupObservers() {
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
}
