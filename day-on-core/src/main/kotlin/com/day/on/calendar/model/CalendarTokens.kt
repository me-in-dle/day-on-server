package com.day.on.calendar.model

data class CalendarTokens(
// TODO : 수정필요 -> 패키지 이동
    val connectionId: Long,
    val accessToken: String,
    val refreshToken: String?,
)
