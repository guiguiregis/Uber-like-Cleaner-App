package com.wolfpackdigital.kliner.partner.data.api

import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.RefreshTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

private const val MAX_RETRY_COUNT = 2

class RefreshTokenAuthenticator(private val refreshTokenUseCase: RefreshTokenUseCase) :
    Authenticator,
    PersistenceService {

    override fun authenticate(route: Route?, response: Response): Request? {
        val retryCount = response.retryCount
        if (retryCount > MAX_RETRY_COUNT) return null

        return runBlocking {
            when (val refreshTokenResult =
                refreshTokenUseCase.executeNow(token?.refreshToken ?: "")) {
                is Result.Success -> refreshTokenResult.data?.let {
                    token = it
                    response.request.newBuilder().signWithToken(it).build()
                }
                is Result.Error -> null
            }
        }
    }
}

private val Response.retryCount: Int
    get() {
        var currentResponse = priorResponse
        var result = 0
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
