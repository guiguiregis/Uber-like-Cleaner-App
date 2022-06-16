package com.wolfpackdigital.kliner.partner.core.main.dashboard

import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.PagerSnapHelper
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.wolfpackdigital.kliner.partner.DashboardBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivityViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment :
    BaseFragment<DashboardBinding, DashboardViewModel>(R.layout.fr_dashboard) {

    // Properties

    override val viewModel by viewModel<DashboardViewModel>()
    private val mainActivityViewModel
            by sharedViewModel<MainActivityViewModel>(from = { activity as ViewModelStoreOwner })

    private val tipsAdapter by lazy { TipsAdapter() }
    private val cleanersAdapter by lazy { CleanersAdapter() }
    private val missionsAdapter by lazy { MissionsAdapter(viewModel.onMissionSelected) }
    private val tipsSnapHelper by lazy { PagerSnapHelper() }
    private val missionsSnapHelper by lazy { PagerSnapHelper() }
    private val picker by lazy { RxImagePicker.with(childFragmentManager) }

    // Lifecycle

    override fun setupViews() {
        viewModel.fetchAllData()
        binding?.rvTips?.apply {
            adapter = tipsAdapter
            tipsSnapHelper.attachToRecyclerView(this)
            binding?.rvTipsIndicator?.attachToRecyclerView(this)
        }
        binding?.rvCleaners?.adapter = cleanersAdapter
        binding?.rvMissions?.apply {
            adapter = missionsAdapter
            missionsSnapHelper.attachToRecyclerView(this)
            binding?.rvMissionsIndicator?.attachToRecyclerView(this)
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.tips.observe(viewLifecycleOwner, tipsAdapter::submitList)
        viewModel.cleaners.observe(viewLifecycleOwner, cleanersAdapter::submitList)
        viewModel.missionsMediator.observe(viewLifecycleOwner, missionsAdapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is DashboardViewModel.Command.OpenOffers -> {
                    mainActivityViewModel.selectedUnconfirmedMission.value = it.offerId
                    mainActivityViewModel.selectTab(R.id.nav_tab_offers)
                }
                DashboardViewModel.Command.UploadNovaCertificate -> {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.uploadCertificate(
                            uri,
                            PhotoTypes.NOVA_CERTIFICATE
                        )
                    }
                }
                DashboardViewModel.Command.UploadInsuranceCertificate -> {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.uploadCertificate(
                            uri,
                            PhotoTypes.INSURANCE_CERTIFICATE
                        )
                    }
                }
            }
        }
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>(Constants.REFRESH_EMPLOYEES)
            ?.observe(viewLifecycleOwner) {
                if (it)
                    viewModel.fetchCompanyCleaners()
            }
    }
}
