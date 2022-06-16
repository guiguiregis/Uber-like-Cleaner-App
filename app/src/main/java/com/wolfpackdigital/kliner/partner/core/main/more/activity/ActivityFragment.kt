package com.wolfpackdigital.kliner.partner.core.main.more.activity

import com.wolfpackdigital.kliner.partner.ActivityBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityFragment :
    BaseFragment<ActivityBinding, ActivityViewModel>(R.layout.fr_activity) {

    // Properties

    override val viewModel by viewModel<ActivityViewModel>()
    private val transactionsAdapter by lazy { TransactionsAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.rvTransactions?.adapter = transactionsAdapter
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.companyActivity.observe(viewLifecycleOwner) {
            transactionsAdapter.submitList(it)
        }
    }
}
