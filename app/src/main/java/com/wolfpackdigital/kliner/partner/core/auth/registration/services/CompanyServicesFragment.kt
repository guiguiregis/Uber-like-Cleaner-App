package com.wolfpackdigital.kliner.partner.core.auth.registration.services

import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.wolfpackdigital.kliner.partner.CompanyServicesBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.adapters.CheckboxAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyServicesFragment :
    BaseFragment<CompanyServicesBinding, CompanyServicesViewModel>(R.layout.fr_company_services) {

    // Properties

    override val viewModel by viewModel<CompanyServicesViewModel>()
    private val typesOfServiceAdapter by lazy { CheckboxAdapter() }
    private val typesOfWorkAdapter by lazy { CheckboxAdapter() }
    private val cesuAdapter by lazy { CheckboxAdapter() }
    private val addressesAdapter by lazy { AddressesAdapter() }

    // Lifecycle

    override fun setupViews() {
        viewModel.fetchOptions()
        binding?.rvTypesOfService?.adapter = typesOfServiceAdapter
        binding?.rvTypesOfWork?.adapter = typesOfWorkAdapter
        binding?.rvCesu?.adapter = cesuAdapter
        binding?.rvAddresses?.adapter = addressesAdapter
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.selectedAddresses.observe(viewLifecycleOwner, addressesAdapter::submitList)
        viewModel.typesOfService.observe(viewLifecycleOwner, typesOfServiceAdapter::submitList)
        viewModel.typesOfWork.observe(viewLifecycleOwner, typesOfWorkAdapter::submitList)
        viewModel.cesuItems.observe(viewLifecycleOwner, cesuAdapter::submitList)

        binding?.etSearchAddress?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.addAddress(position)
                binding?.etSearchAddress?.text?.clear()
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

        viewModel.selectedServiceTypes.observe(viewLifecycleOwner) {}
        viewModel.selectedWorkTypes.observe(viewLifecycleOwner) {}
        viewModel.selectedCesu.observe(viewLifecycleOwner) {}
    }

    companion object {
        fun newInstance() = CompanyServicesFragment()
    }
}
