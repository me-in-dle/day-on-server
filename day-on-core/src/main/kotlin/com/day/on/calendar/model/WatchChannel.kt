package com.day.on.calendar.model

import java.time.LocalDateTime

data class WatchChannel(
    val channelId: String,
    val resourceId: String?,
    val expiration: LocalDateTime?,
)
