package com.wolfpackdigital.kliner.partner.core.main.more.profile.gdpr

import com.wolfpackdigital.kliner.partner.GdprBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.openUrl
import org.koin.androidx.viewmodel.ext.android.viewModel

class GdprFragment : BaseFragment<GdprBinding, GdprViewModel>(R.layout.fr_gdpr) {
    override val viewModel by viewModel<GdprViewModel>()

    override fun setupViews() {
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is GdprViewModel.Command.OpenTermsOfService -> requireContext().openUrl(it.url)
                is GdprViewModel.Command.OpenPrivacyPolicy -> requireContext().openUrl(it.url)
            }
        }
    }
}
