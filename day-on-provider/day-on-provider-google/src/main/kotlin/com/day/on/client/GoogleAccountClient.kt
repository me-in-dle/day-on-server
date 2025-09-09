package com.day.on.client

import com.day.on.adapter.ConnectSocialAccountGoogleAdapter
import feign.Headers
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@Component
interface GoogleAccountClient {
    @Headers(ApiClientConfig.CONTENT_TYPE_APPLICATION_JSON)
    @GET("/tokeninfo")
    fun getGoogleResponseByIdToken(
        @Query("id_token") idToken: String,
    ): Call<ConnectSocialAccountGoogleAdapter.GoogleValidatedTokenResponse>
}