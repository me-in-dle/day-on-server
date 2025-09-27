package com.day.on.calendar.jpa

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(
    name = "schedule_contents",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_account_relation_event",
            columnNames = ["account_id", "relation_types", "external_event_id"],
        ),
    ],
)
class ScheduleContentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(name = "daily_schedules_id", nullable = false)
    val dailySchedulesId: Long,
    @Column(name = "external_event_id", columnDefinition = "VARCHAR(255)")
    val externalEventId: String?,
    @Column(name = "account_id", nullable = false, columnDefinition = "BIGINT")
    val accountId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_types", length = 100)
    val relationTypes: ConnectType?,
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
    var title: String,
    @Column(name = "location", columnDefinition = "VARCHAR(100)")
    var location: String?,
    @Column(name = "contents", columnDefinition = "TEXT")
    var contents: String?,
    @Column(name = "use_yn", nullable = false, length = 1)
    var useYn: String = "Y",
    @Column(name = "tag_ids", columnDefinition = "VARCHAR(300)")
    var tagIds: String?,
    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,
    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TaskStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): ScheduleContent {
        return ScheduleContent(
            id = this.id,
            dailySchedulesId = this.dailySchedulesId,
            accountId = this.accountId,
            externalEventId = this.externalEventId,
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
            updatedAt = this.updatedAt,
        )
    }
    fun updateFrom(new: ScheduleContent) {
        this.title = new.title
        this.location = new.location
        this.contents = new.contents
        this.useYn = new.useYn
        this.tagIds = new.tagIds
        this.startTime = new.startTime
        this.endTime = new.endTime
        this.status = new.status
        this.updatedAt = LocalDateTime.now()
    }

    companion object {
        fun fromDomain(domain: ScheduleContent): ScheduleContentEntity {
            return ScheduleContentEntity(
                id = domain.id,
                dailySchedulesId = domain.dailySchedulesId,
                accountId = domain.accountId,
                externalEventId = domain.externalEventId,
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
                updatedAt = domain.updatedAt,
            )
        }
    }

}
