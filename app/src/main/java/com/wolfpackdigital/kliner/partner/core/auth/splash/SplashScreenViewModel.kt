package com.wolfpackdigital.kliner.partner.core.auth.splash

import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.useCases.OnboardingStatus
import com.wolfpackdigital.kliner.partner.shared.useCases.RefreshTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val DELAY = 5000L
const val MILLISECOND_MULTIPLIER = 1000L

class SplashScreenViewModel(
    private val refreshTokenUseCase: RefreshTokenUseCase
) : BaseViewModel(), PersistenceService {

    fun goToOnboardingScreen() {
        viewModelScope.launch {
            delay(DELAY)
            if (showOnboarding)
                _baseCmd.value = BaseCommand.PerformNavAction(
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToOnboardingFragment()
                )
            else
                checkAccessToken()
        }
    }

    private suspend fun checkAccessToken() {
        val tokenTimestamp = token?.let {
            // User is authenticated, just need to check the token
            (it.createdAt + it.expiresIn).toLong() * MILLISECOND_MULTIPLIER
        } ?: run {
            // Tokens does not exists, invalidate any
            // user data and go to welcome screen
            goToWelcomeScreen()
            return
        }

        if (tokenTimestamp <= System.currentTimeMillis()) // Tokens are OK but expired
            refreshAccessToken()
        else // Access token is OK, proceed further
            checkUserStatus()
    }

    private fun refreshAccessToken() {
        viewModelScope.launch {
            when (val result = refreshTokenUseCase.executeNow(token?.refreshToken ?: "")) {
                is Result.Success -> {
                    token = result.data
                    // Here we should check if the user is activated or not
                    checkUserStatus()
                }
                is Result.Error -> {
                    // Something went wrong during token refresh API call, invalidate everything
                    goToWelcomeScreen()
                }
            }
        }
    }

    @Suppress("ComplexMethod", "NestedBlockDepth")
    private suspend fun checkUserStatus() {
        cleanerProfile?.let {
            when (it.onboardingStatus) {
                OnboardingStatus.COMPLETE -> {
                    // Profile is complete, but we need to check the cleaner status
                    // depending on its type
                    _baseCmd.value = when (it.kind) {
                        Kind.EMPLOYEE ->
                            // Employees does not care about cleaner status
                            BaseCommand.PerformNavAction(
                                SplashScreenFragmentDirections.actionSplashScreenFragmentToMainActivity(),
                                MainActivity.getMainActivityExtras()
                            )
                        else -> when (it.status) {
                            CleanerStatus.ACTIVE -> {
                                // A SME/Solo partner needs to sign the contract before going further

                                if (contractSignedAt != null && !showNotificationSetting && !showGeolocationSetting)
                                    BaseCommand.PerformNavAction(
                                        SplashScreenFragmentDirections
                                            .actionSplashScreenFragmentToMainActivity(),
                                        MainActivity.getMainActivityExtras()
                                    )
                                else // The user did not sign the contract yet
                                    BaseCommand.PerformNavAction(
                                        SplashScreenFragmentDirections
                                            .actionSplashScreenFragmentToUserVerificationFragment()
                                    )
                            }
                            else ->
                                // This covers the case where a user is still in the 48hr window
                                // for account activation by the back-office admin
                                BaseCommand.PerformNavAction(
                                    SplashScreenFragmentDirections
                                        .actionSplashScreenFragmentToUserVerificationFragment()
                                )
                        }
                    }
                }
                // Something is incomplete in the registration flow,
                // login and go through registration/onboarding again
                else -> goToWelcomeScreen()
            }
        } ?: run {
            // Cleaner profile is not present, invalidate data and restart
            // the registration flow
            goToWelcomeScreen()
        }
    }

    private suspend fun goToWelcomeScreen() {
        deleteAllUserData()
        _baseCmd.value = BaseCommand.PerformNavAction(
            SplashScreenFragmentDirections.actionSplashScreenFragmentToWelcomeFragment()
        )
    }
}
