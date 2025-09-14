package com.day.on.client

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Configuration
class ApiClientConfig {

    @Bean
    fun applyGoogleAccountClient(): GoogleAccountClient {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_LOGIN_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build(),
            )
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
            .create(GoogleAccountClient::class.java)
    }

    companion object {
        const val CONTENT_TYPE_APPLICATION_JSON = "Content-Type: application/json"
        private const val GOOGLE_LOGIN_URL = "https://oauth2.googleapis.com/"
    }
}