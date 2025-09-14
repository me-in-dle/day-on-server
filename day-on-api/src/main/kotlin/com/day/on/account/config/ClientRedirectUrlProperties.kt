package com.day.on.account.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.redirect")
data class ClientRedirectUrlProperties(
    val url: String,
)