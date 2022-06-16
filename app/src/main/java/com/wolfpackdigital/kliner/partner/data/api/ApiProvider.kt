package com.wolfpackdigital.kliner.partner.data.api

import com.google.gson.GsonBuilder
import com.wolfpackdigital.kliner.partner.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TIMEOUT = 30L

object ApiProvider {

    fun provideAuthAPI(retrofit: Retrofit): AuthAPI = retrofit.create(AuthAPI::class.java)

    fun provideMainAPI(retrofit: Retrofit): MainAPI = retrofit.create(MainAPI::class.java)

    fun provideOkHttpClient(refreshTokenAuthenticator: RefreshTokenAuthenticator): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor())
            .addInterceptor(LocalTimeInterceptor())
            .authenticator(refreshTokenAuthenticator)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return client.build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addConverterFactory(NullOnEmptyConverterFactory())
        .client(okHttpClient)
        .build()

    private val gsonConverterFactory: GsonConverterFactory = let {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        GsonConverterFactory.create(gson)
    }
}
