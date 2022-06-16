package com.wolfpackdigital.kliner.partner.core.main.offers

import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.wolfpackdigital.kliner.partner.OffersBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivityViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OffersFragment : BaseFragment<OffersBinding, OffersViewModel>(R.layout.fr_offers) {

    // Properties

    override val viewModel by viewModel<OffersViewModel>()
    private val mainActivityViewModel
            by sharedViewModel<MainActivityViewModel>(from = { activity as ViewModelStoreOwner })

    private val adapter by lazy { OffersAdapter() }
    private val snapHelper by lazy { PagerSnapHelper() }

    private val listObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mainActivityViewModel.selectedUnconfirmedMission.value?.let { missionId ->
                viewModel.offers.value?.indexOfFirst { it.id == missionId }?.let { position ->
                    if (position >= 0) {
                        binding?.rvOffers?.scrollToPosition(position)
                        mainActivityViewModel.selectedUnconfirmedMission.value = null
                    }
                }
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }
    }

    // Lifecycle

    override fun onResume() {
        super.onResume()
        adapter.registerAdapterDataObserver(listObserver)
    }

    override fun onPause() {
        super.onPause()
        adapter.unregisterAdapterDataObserver(listObserver)
    }

    override fun setupViews() {
        viewModel.fetchAllData()
        binding?.rvOffers?.adapter = adapter
        binding?.rvOffers?.apply {
            snapHelper.attachToRecyclerView(this)
            binding?.rvIndicator?.attachToRecyclerView(this)
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.offers.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is OffersViewModel.Command.LaunchIntent -> startActivity(it.intent)
            }
        }
        mainActivityViewModel.selectedUnconfirmedMission.observe(
            viewLifecycleOwner,
            { listObserver.onChanged() }
        )
        navController?.currentBackStackEntry
            ?.savedStateHandle?.let { savedStateHandle ->
                savedStateHandle.getLiveData<String>(Constants.SHOW_MESSAGE)
                    .observe(viewLifecycleOwner) { message ->
                        message?.let {
                            snackBar(
                                message,
                                bottomMargin = resources.getDimensionPixelSize(R.dimen.margin_56dp)
                            )
                            savedStateHandle.set(Constants.SHOW_MESSAGE, null)
                        }
                    }
            }
    }
}
