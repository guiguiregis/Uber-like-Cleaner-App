package com.wolfpackdigital.kliner.partner.core.auth.onboarding.placeholder

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class OnboardingPlaceholderViewModel(
    private val state: OnboardingPlaceholderFragment.State
) : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val title = MutableLiveData<@StringRes Int>()
    val description = MutableLiveData<@StringRes Int>()
    val drawable = MutableLiveData<@DrawableRes Int>()

    init {
        when (state) {
            OnboardingPlaceholderFragment.State.IncreaseActivity -> {
                title.value = R.string.onboard_increase_activity
                description.value = R.string.onboard_description_1
                drawable.value = R.drawable.onboarding_1
            }
            OnboardingPlaceholderFragment.State.BoostRevenue -> {
                title.value = R.string.onboard_boost_revenue
                description.value = R.string.onboard_description_2
                drawable.value = R.drawable.onboarding_2
            }
            OnboardingPlaceholderFragment.State.WorkBetter -> {
                title.value = R.string.onboard_work_better
                description.value = R.string.onboard_description_3
                drawable.value = R.drawable.onboarding_3
            }
        }
    }

    sealed class Command
}
