package com.day.on.calendar.service

import com.day.on.calendar.usecase.inbound.CalendarWebhookUseCase
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import com.day.on.calendar.usecase.outbound.CalendarEventSyncPort
import com.day.on.calendar.usecase.outbound.CalendarProviderClientPort
import com.day.on.calendar.usecase.outbound.CalendarTokenPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CalendarWebhookService(
    private val connectionPort: CalendarConnectionPort,
    private val providerClientPort: CalendarProviderClientPort,
    private val eventSyncPort: CalendarEventSyncPort,
    private val tokenPort: CalendarTokenPort,
) : CalendarWebhookUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)
    // TODO : channel Id 갱신하는 거
    @Transactional
    override fun handleWebhookNotification(
        channelId: String,
        resourceId: String,
    ) {
        val connection = connectionPort.findByChannelAndResource(channelId, resourceId) ?: return
        val accountId = connection.accountId
        val accessToken = tokenPort.findByAccountIdAndConnectType(accountId, connection.provider) ?: return

        try {
            val resp =
                providerClientPort.fetchEventsWithSyncToken(
                    connection.provider,
                    accessToken.accessToken,
                    connection.syncToken,
                )

            if (resp.events.isNotEmpty()) {
                eventSyncPort.applyIncrementalChanges(accountId, resp.events) // 범위가 day에 등록된 범위인지 확인 후 갱신
            }

            // syncToken 업데이트
            connectionPort.updateSyncToken(connection.id, resp.nextSyncToken.toString())
            connectionPort.updateLastSynced(connection.id, LocalDateTime.now())
        } catch (ex: Exception) {
            logger.error("Webhook sync failed for accountId=$accountId", ex)
            throw ex
        }
    }
}
