package com.day.on.websocket.endpoint

import com.day.on.common.outbound.CachePort
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class WsTicketController(
    private val cachePort: CachePort,
) {
    @PostMapping("/api/ws-ticket")
    fun issueTicket(
        @RequestHeader("X-ACCOUNT-ID") accountId: Long,
        response: HttpServletResponse,
    ): ResponseEntity<Unit> {
        val ticketToken = UUID.randomUUID()

        cachePort.putIfAbsent(
            key = "ws:ticket:$ticketToken",
            value = accountId.toString(),
            ttlMillis = 1000L * 60L * 5L,
        )

        val cookie = ResponseCookie.from("WS-TICKET", ticketToken.toString())
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .maxAge(1000L)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity.ok().build()
    }
}