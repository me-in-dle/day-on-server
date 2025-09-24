package com.day.on.calendar.endpoint

import com.day.on.calendar.usecase.inbound.CalendarWebhookUseCase
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarWebhookController(
    private val webhookUseCase: CalendarWebhookUseCase,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/webhook")
    fun receiveWebhook(
            @RequestHeader("x-goog-channel-id") channelId: String,
            @RequestHeader("x-goog-resource-id") resourceId: String,
            @RequestHeader("x-goog-resource-state") resourceState: String,
            request: HttpServletRequest,
    ): ResponseEntity<Void> {
        logger.info("[Webhook] Received headers: channelId=$channelId, resourceId=$resourceId, state=$resourceState")

        if (channelId != null && resourceId != null && resourceState == "exists") {
            try {
                webhookUseCase.handleWebhookNotification(channelId, resourceId)
                logger.info("[Webhook] Processing completed")
            } catch (e: Exception) {
                logger.error("[Webhook] Processing failed", e)
            }
        } else {
            logger.info("[Webhook] Skipping - missing headers or sync state")
        }
        return ResponseEntity.ok().build() // Google에 ACK
    }
}
