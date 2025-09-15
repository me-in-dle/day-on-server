package com.day.on.calendar.usecase.outbound

interface OAuthTokenPort {
    // TODO : 패키지 이동 고려..
    fun getAccessToken(accountId: Long): String?
    fun refreshAccessToken(accountId: Long): String?
}