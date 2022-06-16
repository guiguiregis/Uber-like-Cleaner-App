package com.wolfpackdigital.kliner.partner.core.main.more.profile.company.name

import com.wolfpackdigital.kliner.partner.CompanyNameBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyNameFragment :
    BaseFragment<CompanyNameBinding, CompanyNameViewModel>(R.layout.fr_company_name) {

    // Properties

    override val viewModel by viewModel<CompanyNameViewModel>()

    // Lifecycle

    override fun setupViews() {}
}
