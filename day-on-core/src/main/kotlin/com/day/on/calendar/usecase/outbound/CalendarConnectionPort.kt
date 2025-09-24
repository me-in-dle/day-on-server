package com.day.on.calendar.usecase.outbound

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.model.WatchChannel
import java.time.LocalDateTime

interface CalendarConnectionPort {
    fun existsByAccountIdAndIsActive(accountId: Long): Boolean

    fun findByAccountId(accountId: Long): CalendarConnection?

    fun save(connection: CalendarConnection): CalendarConnection

    fun findByChannelAndResource(
        channelId: String,
        resourceId: String,
    ): CalendarConnection?

    fun updateSyncTokenByAccountId(
        accountId: Long,
        connectType: String,
        nextSyncToken: String,
    )

    fun updateSyncToken(
        connectionId: Long,
        syncToken: String,
    )

    fun updateLastSynced(
        connectionId: Long,
        timestamp: LocalDateTime,
    )

    fun updateChannelAndResource(
        accountId: Long,
        provider: ConnectType,
        watch: WatchChannel,
    )
}
