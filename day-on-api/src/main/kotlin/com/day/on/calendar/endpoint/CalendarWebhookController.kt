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
            @RequestHeader("X-Goog-Channel-ID") channelId: String,
            @RequestHeader("X-Goog-Resource-ID") resourceId: String,
            request: HttpServletRequest,
    ): ResponseEntity<Void> {
        logger.info("[Webhook] Received: method=${request.method}, uri=${request.requestURI}")
        logger.info("[Webhook] Headers: ${
            Collections.list(request.headerNames)
                .associateWith { request.getHeader(it) }}")
        webhookUseCase.handleWebhookNotification(channelId, resourceId)
        return ResponseEntity.ok().build() // Google에 ACK
    }
}
