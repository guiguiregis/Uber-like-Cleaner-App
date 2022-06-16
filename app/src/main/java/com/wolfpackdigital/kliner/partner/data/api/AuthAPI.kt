package com.wolfpackdigital.kliner.partner.data.api

import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.shared.useCases.FCMTokenRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.FCMTokenResult
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry
import com.wolfpackdigital.kliner.partner.shared.useCases.RefreshTokenRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.SignInRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {

    @POST("partners/sessions")
    suspend fun signIn(@Body request: SignInRequest)

    @POST("verifications")
    suspend fun verifyCode(@Body request: VerifyPhoneRequest): VerifyPhoneResult

    @GET("verifications/new")
    suspend fun resendCode(@Query("phone_number") phoneNumber: String)

    @GET("phone_numbers")
    suspend fun updatePhoneNumber(@Query("phone_number") phoneNumber: String)

    @GET("phone_country_codes")
    suspend fun getCountryCodes(): List<ItemCountry>

    @POST("sessions/token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Token

    @POST("devices")
    suspend fun sendFCMToken(@Body request: FCMTokenRequest): FCMTokenResult

    @PATCH("devices")
    suspend fun toggleNotificationsStatus(@Body request: FCMTokenRequest): FCMTokenResult

    @DELETE("sessions")
    suspend fun deleteSession()
}
