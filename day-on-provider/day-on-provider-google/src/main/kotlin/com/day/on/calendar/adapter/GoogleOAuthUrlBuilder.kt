package com.day.on.calendar.adapter

import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class GoogleOAuthUrlBuilder {
    // TODO : OAuth URL 빌더 (추후 공통 재사용 고려)

    /**
     * scopeList 는 application.yml에 있는 List<String>을 전달.
     * state 는 필요한 값(예: "google-calendar" 또는 "forwardUrl=...")을 넣음.
     */

    fun buildAuthUrl(
            authBaseUrl: String,
            clientId: String,
            redirectUri: String,
            scopeList: List<String>,
            state: String,
            accessType: String = "offline",
            prompt: String = "consent"
    ): String {
        val scope = scopeList.joinToString(" ")
        val enc = { s: String -> URLEncoder.encode(s, StandardCharsets.UTF_8) }
        return buildString {
            append(authBaseUrl)
            append("?client_id=").append(enc(clientId))
            append("&redirect_uri=").append(enc(redirectUri))
            append("&response_type=code")
            append("&scope=").append(enc(scope))
            append("&access_type=").append(enc(accessType))
            append("&prompt=").append(enc(prompt))
            append("&state=").append(enc(state))
        }
    }
}