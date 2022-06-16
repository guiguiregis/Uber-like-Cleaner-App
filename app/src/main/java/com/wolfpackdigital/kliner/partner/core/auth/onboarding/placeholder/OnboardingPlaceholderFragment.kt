package com.wolfpackdigital.kliner.partner.core.auth.onboarding.placeholder

import androidx.core.os.bundleOf
import com.wolfpackdigital.kliner.partner.OnboardingPlaceholderBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val STATE_KEY = "state_key"

class OnboardingPlaceholderFragment :
    BaseFragment<OnboardingPlaceholderBinding, OnboardingPlaceholderViewModel>(R.layout.fr_onboarding_placeholder) {

    // Properties
    private val state by lazy { arguments?.getSerializable(STATE_KEY) as State }

    override val viewModel by viewModel<OnboardingPlaceholderViewModel> {
        parametersOf(state)
    }

    // Lifecycle

    override fun setupViews() {
    }

    enum class State {
        IncreaseActivity,
        BoostRevenue,
        WorkBetter
    }

    companion object {
        fun newInstance(state: State): OnboardingPlaceholderFragment {
            return OnboardingPlaceholderFragment().apply {
                arguments = bundleOf(STATE_KEY to state)
            }
        }
    }
}
