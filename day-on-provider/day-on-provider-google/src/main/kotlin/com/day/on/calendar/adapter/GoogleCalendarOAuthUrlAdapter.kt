package com.day.on.calendar.adapter

import com.day.on.calendar.usecase.outbound.CalendarOAuthUrlPort
import org.springframework.stereotype.Component

@Component
class GoogleCalendarOAuthUrlAdapter (private val googleProperties: GoogleCalendarOauthProperties,
                                     private val urlBuilder: GoogleOAuthUrlBuilder) : CalendarOAuthUrlPort{
    private val authBaseUrl = "https://accounts.google.com/o/oauth2/v2/auth"
    // TODO : OAuth calender adapter (추후 공통 재사용 고려)
    override fun createCalendarAuthUrl(): String {
        val state = "google-calendar" // TODO : 보안 고려해보기..
        return urlBuilder.buildAuthUrl(
                authBaseUrl = authBaseUrl,
                clientId = googleProperties.clientId,
                redirectUri = googleProperties.redirectUri,
                scopeList = googleProperties.scope,
                state = state,
                accessType = "offline",
                prompt = "consent"
        )
    }
}