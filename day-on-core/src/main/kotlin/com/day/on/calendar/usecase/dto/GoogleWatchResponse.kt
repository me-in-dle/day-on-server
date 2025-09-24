package com.day.on.calendar.usecase.dto

import com.day.on.calendar.model.WatchChannel
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneOffset

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleWatchResponse(
        @JsonProperty("id")
        val id: String,
        @JsonAlias("resource_id")
        val resourceId: String?,
        @JsonProperty("expiration")
        val expiration: String?,
) {
    fun toDomain(): WatchChannel =
            WatchChannel(
                    channelId = id,
                    resourceId = resourceId ?: "",  // 빈값이나 default 가능
                    expiration = expiration?.toLongOrNull()?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime()
                    }
            )
}

