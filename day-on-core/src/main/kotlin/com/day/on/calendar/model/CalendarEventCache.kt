package com.day.on.calendar.model

import java.time.LocalDateTime

data class CalendarEventCache(
// TODO : 수정필요 하드코딩..
    val id: String, // 외부 이벤트 ID
    val connectionId: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val location: String?,
    val startDatetime: String?,
    val endDatetime: String?,
    val startDate: String?, // 종일 이벤트용 (YYYY-MM-DD)
    val endDate: String?,
    val timezone: String = "Asia/Seoul",
    val status: String = "CONFIRMED",
    val externalCreated: String,
    val externalUpdated: String,
    val cachedAt: LocalDateTime
)
