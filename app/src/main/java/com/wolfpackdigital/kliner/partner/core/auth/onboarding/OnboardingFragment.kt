package com.wolfpackdigital.kliner.partner.core.auth.onboarding

import androidx.viewpager2.widget.ViewPager2
import com.wolfpackdigital.kliner.partner.OnboardingBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ITEM_COUNT = 3

class OnboardingFragment :
    BaseFragment<OnboardingBinding, OnboardingViewModel>(R.layout.fr_onboarding) {

    // Properties

    override val viewModel by viewModel<OnboardingViewModel>()

    private val onPageChanged = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.pageSelected(position)
        }
    }

    override fun setupViews() {
        binding?.viewPager?.apply {
            adapter = OnboardingAdapter(this@OnboardingFragment)
            registerOnPageChangeCallback(onPageChanged)
        }
        binding?.pageIndicator?.apply {
            setIndicatorDrawable(
                R.drawable.page_indicator_normal,
                R.drawable.page_indicator_selected
            )
            val size = resources.getDimension(R.dimen.page_indicator_diameter).toInt()
            setIndicatorSize(size, size, size, size)
            setIndicatorGap(size * 2)
            setupWithViewPager(binding?.viewPager ?: return@apply)
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                OnboardingViewModel.Command.NextPage -> {
                    val nextItem = ((binding?.viewPager?.currentItem ?: 0) + 1) % ITEM_COUNT
                    if (nextItem != 0)
                        binding?.viewPager?.setCurrentItem(nextItem, true)
                    else
                        viewModel.openWelcomeScreen()
                }
                OnboardingViewModel.Command.SkipOnboarding -> {
                    viewModel.openWelcomeScreen()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.viewPager?.unregisterOnPageChangeCallback(onPageChanged)
    }
}
