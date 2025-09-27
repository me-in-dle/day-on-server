package com.day.on.calendar.model

import com.day.on.account.type.ConnectType
import java.time.LocalDateTime

data class CalendarConnection(
    val id: Long,
    val accountId: Long,
    val provider: ConnectType,
    val isActive: Boolean,
    val resourceId: String?, // 구글 채널 구독할 때 받은 리소스 ID
    val channelId: String, // 서버에서 발급한 구독 채널 ID
    val syncToken: String?, // 증분 동기화 토큰
    val expiration: LocalDateTime?,
    val lastSynced: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
