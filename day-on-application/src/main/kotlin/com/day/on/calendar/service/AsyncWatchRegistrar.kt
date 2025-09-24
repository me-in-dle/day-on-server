package com.day.on.calendar.service

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.WatchChannel
import com.day.on.calendar.type.CalendarIdType
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset

@Service
class AsyncWatchRegistrar(
        private val providerClient: CalendarProviderClientPort,
        private val connectionPort: CalendarConnectionPort,
) {
    private val logger = LoggerFactory.getLogger(javaClass)


    fun registerWatch(
            accountId: Long,
            ct: ConnectType,
            accessToken: String,
            webhookUrl: String,
    ) {
        try {
            val resp = providerClient.registerWatch(ct, accessToken, CalendarIdType.PRIMARY, webhookUrl)
            val watchChannel =
                    WatchChannel(
                            channelId = resp.id,
                            resourceId = resp.resourceId?: "",
                            expiration = resp.expiration?.toLongOrNull()?.let {
                                Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime()
                            }
                    )

            connectionPort.updateChannelAndResource(accountId, ct, watchChannel)
            logger.info("[Watch] 등록 for accountId=$accountId, channelId=${resp.id}, resourceId=${resp.resourceId}")
        } catch (ex: Exception) {
            logger.error("[watch] 실패 for accountId=$accountId", ex)
        }
    }
}

