package com.wolfpackdigital.kliner.partner.core.auth.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wolfpackdigital.kliner.partner.core.auth.onboarding.placeholder.OnboardingPlaceholderFragment

class OnboardingAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> OnboardingPlaceholderFragment.newInstance(OnboardingPlaceholderFragment.State.IncreaseActivity)
        1 -> OnboardingPlaceholderFragment.newInstance(OnboardingPlaceholderFragment.State.BoostRevenue)
        2 -> OnboardingPlaceholderFragment.newInstance(OnboardingPlaceholderFragment.State.WorkBetter)
        else -> Fragment()
    }
}
