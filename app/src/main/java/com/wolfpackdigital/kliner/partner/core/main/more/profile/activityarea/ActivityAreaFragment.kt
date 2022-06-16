package com.wolfpackdigital.kliner.partner.core.main.more.profile.activityarea

import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.wolfpackdigital.kliner.partner.ActivityAreaBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.registration.services.AddressesAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityAreaFragment :
    BaseFragment<ActivityAreaBinding, ActivityAreaViewModel>(R.layout.fr_activity_area) {

    // Properties

    override val viewModel by viewModel<ActivityAreaViewModel>()
    private val addressesAdapter by lazy { AddressesAdapter() }

    // Lifecycle

    override fun setupViews() {
        setupObservers()
        binding?.rvAddresses?.adapter = addressesAdapter
    }

    private fun setupObservers() {
        viewModel.selectedAddresses.observe(viewLifecycleOwner, addressesAdapter::submitList)
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
    }
}
