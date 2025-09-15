package com.day.on.calendar.jpa

import com.day.on.calendar.model.Priority
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "schedule_contents")
data class ScheduleContentEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "daily_schedules_id", nullable = false)
        val dailySchedulesId: Long,

        @Column(name = "user_id", nullable = false, length = 50)
        val userId: String,

        @Column(name = "relation_types", length = 100)
        val relationTypes: String?,

        @Column(name = "contents", columnDefinition = "TEXT")
        val contents: String?,

        @Column(name = "use_yn", length = 1)
        val useYn: String = "Y",

        @Column(name = "tag_ids", columnDefinition = "JSON")
        val tagIds: String?,

        @Column(name = "start_time")
        val startTime: LocalTime?,

        @Column(name = "end_time")
        val endTime: LocalTime?,

        @Column(name = "is_all_day", nullable = false)
        val isAllDay: Boolean = false,

        @Column(name = "location", length = 500)
        val location: String?,

        @Enumerated(EnumType.STRING)
        @Column(name = "priority")
        val priority: Priority = Priority.NORMAL,

        @Enumerated(EnumType.STRING)
        @Column(name = "status")
        val status: TaskStatus = TaskStatus.PENDING,

        @Column(name = "color_code", length = 7)
        val colorCode: String?,

        @Column(name = "reminder_minutes")
        val reminderMinutes: Int?,

        @Column(name = "external_event_id")
        val externalEventId: String?,

        @Column(name = "connection_id")
        val connectionId: Long?,

        @Column(name = "sort_order")
        val sortOrder: Int = 0,

        @Column(name = "created_at")
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): ScheduleContent {
        return ScheduleContent(
                id = this.id,
                dailySchedulesId = this.dailySchedulesId,
                userId = this.userId,
                relationTypes = this.relationTypes,
                contents = this.contents,
                useYn = this.useYn,
                tagIds = this.tagIds,
                startTime = this.startTime,
                endTime = this.endTime,
                isAllDay = this.isAllDay,
                location = this.location,
                priority = this.priority,
                status = this.status,
                colorCode = this.colorCode,
                reminderMinutes = this.reminderMinutes,
                externalEventId = this.externalEventId,
                connectionId = this.connectionId,
                sortOrder = this.sortOrder,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: ScheduleContent): ScheduleContentEntity {
            return ScheduleContentEntity(
                    id = domain.id,
                    dailySchedulesId = domain.dailySchedulesId,
                    userId = domain.userId,
                    relationTypes = domain.relationTypes,
                    contents = domain.contents,
                    useYn = domain.useYn,
                    tagIds = domain.tagIds,
                    startTime = domain.startTime,
                    endTime = domain.endTime,
                    isAllDay = domain.isAllDay,
                    location = domain.location,
                    priority = domain.priority,
                    status = domain.status,
                    colorCode = domain.colorCode,
                    reminderMinutes = domain.reminderMinutes,
                    externalEventId = domain.externalEventId,
                    connectionId = domain.connectionId,
                    sortOrder = domain.sortOrder,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}