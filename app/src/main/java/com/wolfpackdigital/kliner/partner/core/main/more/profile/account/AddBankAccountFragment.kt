package com.wolfpackdigital.kliner.partner.core.main.more.profile.account

import androidx.navigation.fragment.navArgs
import com.wolfpackdigital.kliner.partner.AddBankAccountBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddBankAccountFragment :
    BaseFragment<AddBankAccountBinding, AddBankAccountViewModel>(R.layout.fr_add_bank_account) {

    // Properties

    override val viewModel by viewModel<AddBankAccountViewModel> {
        parametersOf(args.bankAccount)
    }

    private val args by navArgs<AddBankAccountFragmentArgs>()

    // Lifecycle

    override fun setupViews() {}
}
