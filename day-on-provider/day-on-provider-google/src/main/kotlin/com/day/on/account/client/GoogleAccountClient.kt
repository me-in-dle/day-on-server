package com.day.on.client

import com.day.on.adapter.ConnectSocialAccountGoogleAdapter
import feign.Headers
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Component
interface GoogleAccountClient {

    @FormUrlEncoded
    @POST("/token")
    fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String
    ): Call<ConnectSocialAccountGoogleAdapter.GoogleTokenResponse>

    @Headers(ApiClientConfig.CONTENT_TYPE_APPLICATION_JSON)
    @GET("/tokeninfo")
    fun getGoogleResponseByIdToken(
        @Query("id_token") idToken: String,
    ): Call<ConnectSocialAccountGoogleAdapter.GoogleValidatedTokenResponse>
}