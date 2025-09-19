package com.day.on.calendar.adapter

import com.day.on.calendar.usecase.outbound.CalendarOAuthUrlPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleCalendarOAuthUrlAdapter (private val googleProperties: GoogleCalendarOauthProperties,
                                     private val urlBuilder: GoogleOAuthUrlBuilder,
                                     @Value("\${calendar.oauth.redirect-uri:http://localhost:8080/api/v1/calendar/oauth/callback}") private val defaultRedirect: String) : CalendarOAuthUrlPort{
    private val authBaseUrl = "https://accounts.google.com/o/oauth2/v2/auth"
    // TODO : OAuth calender adapter (추후 공통 재사용 고려)
    override fun createCalendarAuthUrl(provider: String, state: String): String{
        val redirect = when(provider.lowercase()) {
            "google" -> googleProperties.redirectUri
            else -> defaultRedirect
        }
        val extra = mapOf(
                "access_type" to "offline",   // refresh token 받을 때 필요
                "prompt" to "consent"        // 추후 -> include_granted_scopes=true 사용 (이미동의한scope에는재동의묻지않는역할)
        )
        return urlBuilder.buildAuthUrl(
                authBaseUrl = authBaseUrl,
                clientId = googleProperties.clientId,
                redirectUri = redirect,
                scopeList = googleProperties.scope,
                state = state,
                extraParams = extra
        )
    }
}