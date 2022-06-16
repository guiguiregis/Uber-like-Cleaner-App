package com.wolfpackdigital.kliner.partner.core.main.more.profile.company

import com.wolfpackdigital.kliner.partner.CompanyProfileBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyProfileFragment :
    BaseFragment<CompanyProfileBinding, CompanyProfileViewModel>(R.layout.fr_company_profile) {

    // Properties
    override val viewModel by viewModel<CompanyProfileViewModel>()

    // Lifecycle
    override fun setupViews() {
        viewModel.initCleanerProfile()
    }
}
