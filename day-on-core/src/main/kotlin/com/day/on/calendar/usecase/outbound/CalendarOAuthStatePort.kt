package com.day.on.calendar.usecase.outbound

import com.day.on.calendar.usecase.dto.OAuthStatePayload

interface CalendarOAuthStatePort {
    fun encode(payload: OAuthStatePayload): String
    fun decode(state: String?): OAuthStatePayload?
    /**
     * provider 별로 사용되는 redirectUri가 다르면 여기서 제공하도록 해도 됨
     */
    fun redirectUriForProvider(provider: String): String
}