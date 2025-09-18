package com.day.on.calendar.adapter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.oauth2.calendar")
data class GoogleCalendarOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val scope: List<String>,
    val redirectUri: String,
)