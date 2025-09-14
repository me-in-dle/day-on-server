package com.day.on.adapter

import org.springframework.boot.context.properties.ConfigurationProperties
@ConfigurationProperties(prefix = "google.oauth2.client")
data class GoogleOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)