package com.wolfpackdigital.kliner.partner.core.main.more.unassignedmissions

import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.UnassignedMissionsBinding
import com.wolfpackdigital.kliner.partner.core.main.dashboard.MissionsAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnassignedMissionsFragment :
    BaseFragment<UnassignedMissionsBinding, UnassignedMissionsViewModel>(R.layout.fr_unassigned_missions) {

    // Properties

    override val viewModel by viewModel<UnassignedMissionsViewModel>()
    private val unassignedMissionsAdapter by lazy { MissionsAdapter(viewModel.onMissionSelected) }

    // Lifecycle

    override fun setupViews() {
        viewModel.fetchMissions()
        binding?.rvUnassignedMissions?.adapter = unassignedMissionsAdapter
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.unassignedMissions.observe(
            viewLifecycleOwner,
            unassignedMissionsAdapter::submitList
        )
    }
}
