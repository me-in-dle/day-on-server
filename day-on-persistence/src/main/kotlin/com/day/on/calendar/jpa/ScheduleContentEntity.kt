package com.day.on.calendar.jpa

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "schedule_contents")
class ScheduleContentEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @Column(name = "daily_schedules_id", nullable = false)
        val dailySchedulesId: Long,

        @Column(name = "account_id", nullable = false, columnDefinition = "BIGINT")
        val accountId: Long,

        @Enumerated(EnumType.STRING)
        @Column(name = "relation_types", length = 100)
        val relationTypes: ConnectType?,

        @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
        val title: String,

        @Column(name = "location", columnDefinition = "VARCHAR(100)")
        val location: String?,

        @Column(name = "contents", columnDefinition = "TEXT")
        val contents: String?,

        @Column(name = "use_yn", nullable = false, length = 1)
        val useYn: String = "Y",

        @Column(name = "tag_ids", columnDefinition = "VARCHAR(300)")
        val tagIds: String?,

        @Column(name = "start_time" , nullable = false)
        val startTime: LocalTime,

        @Column(name = "end_time" , nullable = false)
        val endTime: LocalTime,

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        val status: TaskStatus,

        @Column(name = "created_at", nullable = false,)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): ScheduleContent {
        return ScheduleContent(
                id = this.id,
                dailySchedulesId = this.dailySchedulesId,
                accountId = this.accountId,
                relationTypes = this.relationTypes,
                title = this.title,
                location = this.location,
                contents = this.contents,
                useYn = this.useYn,
                tagIds = this.tagIds,
                startTime = this.startTime,
                endTime = this.endTime,
                status = this.status,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: ScheduleContent): ScheduleContentEntity {
            return ScheduleContentEntity(
                    id = domain.id,
                    dailySchedulesId = domain.dailySchedulesId,
                    accountId = domain.accountId,
                    relationTypes = domain.relationTypes,
                    title = domain.title,
                    contents = domain.contents,
                    useYn = domain.useYn,
                    tagIds = domain.tagIds,
                    startTime = domain.startTime,
                    endTime = domain.endTime,
                    location = domain.location,
                    status = domain.status,
                    createdAt = domain.createdAt,
                    updatedAt = domain.updatedAt
            )
        }
    }
}