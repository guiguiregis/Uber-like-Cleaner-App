package com.wolfpackdigital.kliner.partner.core.main.more

import androidx.core.content.ContextCompat
import com.wolfpackdigital.kliner.partner.MoreBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.customs.TopAndBottomDividerItemDecoration
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.openUrl
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoreFragment : BaseFragment<MoreBinding, MoreViewModel>(R.layout.fr_more) {

    // Properties

    override val viewModel by viewModel<MoreViewModel>()
    private val moreSectionAdapter by lazy { MoreSectionAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.rvMoreSection?.apply {
            adapter = moreSectionAdapter
            val itemDecoration =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.delimiter_decoration_horizontal
                )?.let {
                    TopAndBottomDividerItemDecoration(
                        it
                    )
                }
            itemDecoration?.let { addItemDecoration(it) }
        }
        viewModel.initCleanerProfile()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.moreSection.observe(viewLifecycleOwner, moreSectionAdapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is MoreViewModel.Command.ShowSupport -> {
                    requireContext().openUrl(it.url)
                }
                is MoreViewModel.Command.ShowAbout -> {
                    requireContext().openUrl(it.url)
                }
            }
        }
    }
}
