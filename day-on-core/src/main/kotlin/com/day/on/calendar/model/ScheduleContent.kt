package com.day.on.calendar.model

import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleContent(
        val id: Long,
        val dailySchedulesId: Long,
        val userId: String,
        val relationTypes: String?, // 'CALENDAR_GOOGLE_1', 'TODO', 'HABIT', null
        val contents: String?,
        val useYn: String = "Y",
        val tagIds: String?, // JSON 배열

        // 새로 추가된 필드들
        val startTime: LocalTime?,
        val endTime: LocalTime?,
        val isAllDay: Boolean = false,
        val location: String?,
        val priority: Priority = Priority.NORMAL,
        val status: TaskStatus = TaskStatus.PENDING,
        val colorCode: String?,
        val reminderMinutes: Int?,
        val externalEventId: String?, // 외부 캘린더 이벤트 ID
        val connectionId: Long?, // 캘린더 연결 ID
        val sortOrder: Int = 0,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
) {
    fun isCalendarEvent(): Boolean = relationTypes?.startsWith("CALENDAR_") == true
    fun isGeneralTask(): Boolean = relationTypes == null
    fun isTodoTask(): Boolean = relationTypes == "TODO"
    fun isHabitTask(): Boolean = relationTypes == "HABIT"
}
