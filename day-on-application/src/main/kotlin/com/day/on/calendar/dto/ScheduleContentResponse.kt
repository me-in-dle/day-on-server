package com.day.on.calendar.dto

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.TaskStatus

data class ScheduleContentResponse(
        val id: Long,
        val title: String,
        val contents: String?,
        val useYn: String,
        val tagIds: String?,
        val status: TaskStatus,
        val startTime: String,
        val endTime: String,
        val relationTypes: ConnectType?,
        val location: String?
) {
    companion object {
        fun from(e: ScheduleContent) = ScheduleContentResponse(
                id = e.id,
                title = e.title,
                contents = e.contents,
                useYn = e.useYn,
                tagIds = e.tagIds,
                status = e.status,
                startTime = e.startTime.toString(),
                endTime = e.endTime.toString(),
                relationTypes = e.relationTypes,
                location = e.location
        )
    }
}