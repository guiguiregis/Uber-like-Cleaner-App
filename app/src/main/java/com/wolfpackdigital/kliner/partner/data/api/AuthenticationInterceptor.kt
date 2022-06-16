package com.wolfpackdigital.kliner.partner.data.api

import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val AUTHORIZATION = "Authorization"
private const val BEARER = "Bearer"

class AuthenticationInterceptor : Interceptor, PersistenceService {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.signWithToken(it)
        }
        return chain.proceed(requestBuilder.build())
    }
}

fun Request.Builder.signWithToken(token: Token): Request.Builder {
    header(AUTHORIZATION, "$BEARER ${token.accessToken}")
    return this
}
