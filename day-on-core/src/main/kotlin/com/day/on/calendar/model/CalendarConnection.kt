package com.day.on.calendar.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime

data class CalendarConnection(
    val id: Long,
    val accountId: Long,
    val provider: ConnectType,
    val accountEmail: String,
    val accountName: String?,
    val isActive: Boolean = true,
    val syncEnabled: Boolean = true,
    val lastSynced: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
