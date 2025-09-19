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
        extraParams: Map<String, String> = emptyMap()
    ): String {
        val sb = StringBuilder(authBaseUrl)
        sb.append("?client_id=").append(encode(clientId))
        sb.append("&redirect_uri=").append(encode(redirectUri))
        sb.append("&response_type=code")
        sb.append("&scope=").append(encode(scopeList.joinToString(" ")))
        sb.append("&state=").append(encode(state))
        extraParams.forEach { (k, v) ->
            sb.append("&").append(encode(k)).append("=").append(encode(v))
        }
        return sb.toString()
    }
    private fun encode(v: String) = URLEncoder.encode(v, StandardCharsets.UTF_8.toString())
}