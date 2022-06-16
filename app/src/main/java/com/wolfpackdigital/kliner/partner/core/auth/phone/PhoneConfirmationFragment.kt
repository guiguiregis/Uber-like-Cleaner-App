package com.wolfpackdigital.kliner.partner.core.auth.phone

import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.navigation.fragment.navArgs
import com.wolfpackdigital.kliner.partner.PhoneConfirmationBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PhoneConfirmationFragment :
    BaseFragment<PhoneConfirmationBinding, PhoneConfirmationViewModel>(R.layout.fr_phone_confirmation) {

    // Properties

    override val viewModel by viewModel<PhoneConfirmationViewModel>() {
        parametersOf(args.prefix, args.phoneNumber)
    }
    private val args by navArgs<PhoneConfirmationFragmentArgs>()
    private val dialogViewModel by viewModel<SelectCountryViewModel>()
    private val countryDialog by lazy { SelectCountryBottomSheetDialog() }
    private var phoneNumberTextWatcher =
        PhoneNumberFormattingTextWatcher(Constants.DEFAULT_COUNTRY_CODE)

    // Lifecycle

    override fun setupViews() {
        binding?.dialogViewModel = dialogViewModel
        dialogViewModel.loadCountries(args.prefix)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is PhoneConfirmationViewModel.Command.OpenPrefixPicker -> countryDialog.show(
                    childFragmentManager,
                    SelectCountryBottomSheetDialog::class.simpleName
                )
                is PhoneConfirmationViewModel.Command.ChangeTextWatcher -> {
                    val editText = binding?.etPhone?.binding?.etContent
                    editText?.removeTextChangedListener(phoneNumberTextWatcher)
                    phoneNumberTextWatcher = PhoneNumberFormattingTextWatcher(it.regionCode)
                    editText?.addTextChangedListener(phoneNumberTextWatcher)
                }
            }
        }
        dialogViewModel.selectedItem.observe(viewLifecycleOwner) {
            viewModel.phonePrefix.value = it?.countryCode
        }
    }
}
