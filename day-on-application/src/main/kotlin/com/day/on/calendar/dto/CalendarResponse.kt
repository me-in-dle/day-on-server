package com.day.on.calendar.dto

data class CalendarResponse(
    val isConnected: Boolean,
    val connectType: String?,
    // val pollIntervalSeconds: Int = 3600, // 클라이언트가 폴링할 기본 간격(1시간) TODO: 추후 yml으로 수
    val schedules: List<ScheduleContentResponse>,
)
