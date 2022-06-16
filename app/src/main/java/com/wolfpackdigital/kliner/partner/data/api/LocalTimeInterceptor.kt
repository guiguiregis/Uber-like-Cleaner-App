package com.wolfpackdigital.kliner.partner.data.api

import java.time.ZonedDateTime
import okhttp3.Interceptor
import okhttp3.Response

private const val HEADER = "Requester-Local-Time"

class LocalTimeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader(HEADER, ZonedDateTime.now().toOffsetDateTime().toString())
        return chain.proceed(requestBuilder.build())
    }
}
