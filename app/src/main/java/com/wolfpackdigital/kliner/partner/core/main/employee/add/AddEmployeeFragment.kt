package com.wolfpackdigital.kliner.partner.core.main.employee.add

import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.wolfpackdigital.kliner.partner.AddEmployeeBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.services.AddressesAdapter
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.core.main.adapters.CheckboxAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddEmployeeFragment :
    BaseFragment<AddEmployeeBinding, AddEmployeeViewModel>(R.layout.fr_add_employee) {

    // Properties
    private val args by navArgs<AddEmployeeFragmentArgs>()
    override val viewModel by viewModel<AddEmployeeViewModel> {
        parametersOf(args.isOnboarding)
    }
    private val dialogViewModel by viewModel<SelectCountryViewModel>()
    private val countryDialog by lazy { SelectCountryBottomSheetDialog() }
    private val typesOfWorkAdapter by lazy { CheckboxAdapter() }
    private var phoneNumberTextWatcher =
        PhoneNumberFormattingTextWatcher(Constants.DEFAULT_COUNTRY_CODE)
    private val addressesAdapter by lazy { AddressesAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.dialogViewModel = dialogViewModel
        binding?.rvTypesOfWork?.adapter = typesOfWorkAdapter
        binding?.rvAddresses?.adapter = addressesAdapter
        dialogViewModel.loadCountries()
        viewModel.getOptions()
        setupObservers()
    }

    // Setup

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is AddEmployeeViewModel.Command.OpenPrefixPicker -> countryDialog.show(
                    childFragmentManager,
                    SelectCountryBottomSheetDialog::class.simpleName
                )
                is AddEmployeeViewModel.Command.ChangeTextWatcher -> {
                    val editText = binding?.etPhone?.binding?.etContent
                    editText?.removeTextChangedListener(phoneNumberTextWatcher)
                    phoneNumberTextWatcher = PhoneNumberFormattingTextWatcher(it.regionCode)
                    editText?.addTextChangedListener(phoneNumberTextWatcher)
                }
                is AddEmployeeViewModel.Command.GoToDashboard -> {
                    navController?.navigate(
                        AddEmployeeFragmentDirections.actionAddEmployeeFragmentToMainActivity(),
                        MainActivity.getMainActivityExtras()
                    )
                }
            }
        }
        dialogViewModel.selectedItem.observe(viewLifecycleOwner) {
            viewModel.phonePrefix.value = it?.countryCode
        }
        binding?.etEmployeeAddress?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.addressSelected(position)
            }
        viewModel.employeeAddressSuggestions.observe(viewLifecycleOwner) {
            binding?.etEmployeeAddress?.setAdapter(getSuggestionsAdapter(it))
            binding?.etEmployeeAddress?.showDropDown()
        }

        viewModel.selectedAddresses.observe(viewLifecycleOwner, addressesAdapter::submitList)
        binding?.etActivityAreas?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.addActivityArea(position)
                binding?.etActivityAreas?.text?.clear()
            }
        viewModel.activityAreasSuggestions.observe(viewLifecycleOwner) {
            binding?.etActivityAreas?.setAdapter(getSuggestionsAdapter(it))
            binding?.etActivityAreas?.showDropDown()
        }

        viewModel.typesOfWork.observe(viewLifecycleOwner, typesOfWorkAdapter::submitList)
        viewModel.selectedWorkTypes.observe(viewLifecycleOwner) {}
    }

    private fun getSuggestionsAdapter(it: List<AddressItem>) =
        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            it.map { item -> item.streetLine })
}
