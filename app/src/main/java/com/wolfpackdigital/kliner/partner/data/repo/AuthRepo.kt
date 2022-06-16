package com.wolfpackdigital.kliner.partner.data.repo

import com.wolfpackdigital.kliner.partner.data.api.AuthAPI
import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.shared.useCases.FCMTokenRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.FCMTokenResult
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry
import com.wolfpackdigital.kliner.partner.shared.useCases.RefreshTokenRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.ResendCodeRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.SignInRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneResult
import org.koin.core.KoinComponent
import org.koin.core.inject

interface AuthRepoI {
    suspend fun signIn(request: SignInRequest)
    suspend fun verifyCode(request: VerifyPhoneRequest): VerifyPhoneResult
    suspend fun resendCode(request: ResendCodeRequest)

    suspend fun logout()

    suspend fun getCountryCodes(): List<ItemCountry>
    suspend fun refreshToken(request: RefreshTokenRequest): Token
    suspend fun sendFCMToken(request: FCMTokenRequest): FCMTokenResult
    suspend fun toggleNotificationsStatus(request: FCMTokenRequest): FCMTokenResult
}

class AuthRepo : KoinComponent, AuthRepoI {
    private val api by inject<AuthAPI>()

    override suspend fun signIn(request: SignInRequest) = api.signIn(request)

    override suspend fun verifyCode(request: VerifyPhoneRequest) = api.verifyCode(request)

    override suspend fun resendCode(request: ResendCodeRequest) =
        api.resendCode(request.phoneNumber)

    override suspend fun getCountryCodes() =
        api.getCountryCodes()

    override suspend fun refreshToken(request: RefreshTokenRequest) =
        api.refreshToken(request)

    override suspend fun sendFCMToken(request: FCMTokenRequest) =
        api.sendFCMToken(request)

    override suspend fun logout() =
        api.deleteSession()

    override suspend fun toggleNotificationsStatus(request: FCMTokenRequest) =
        api.toggleNotificationsStatus(request)
}
