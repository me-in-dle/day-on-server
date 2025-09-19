package com.day.on.calendar.service

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.usecase.dto.OAuthStatePayload
import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase

import com.day.on.calendar.usecase.outbound.*
import com.day.on.common.outbound.LockManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

@Service
class CalendarOAuthService(
        private val statePort: CalendarOAuthStatePort,
        private val calendarOAuthUrlPort: CalendarOAuthUrlPort,
        private val providerClient: CalendarProviderClientPort,
        private val tokenPort: CalendarTokenPort,
        private val eventSyncPort: CalendarEventSyncPort,
        private val connectionPort: CalendarConnectionPort,
        private val lockManager: LockManager,
        private val objectMapper: ObjectMapper

) : CalendarOAuthUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${calendar.oauth.client-redirect-url:http://localhost:5173/calendar}")
    private lateinit var defaultClientRedirect: String
    /**
     * 클라이언트에게 전달할 OAuth 인증 URL을 생성.
     * TODO : forwardUrl: 인증 끝난 뒤 사용자에게 다시 보낼 client URL
     */
    override fun generateCalendarOAuthUrl(accountId: Long, provider: String, forwardUrl: String?): String {
        // 1) connectType 매핑(optional) — state에 기록하면 callback에서 검증 가능
        val connectType = try {
            ConnectType.matchConnectType(provider)
        } catch (ex: Exception) {
            null // 또는 예외 던지기
        }

        // 2) state payload 생성
        val payload = OAuthStatePayload(
                accountId = accountId,
                forwardUrl = forwardUrl,
                connectType = connectType,
                issuedAtMillis = System.currentTimeMillis(),
                expiresInSeconds = 300L
        )

        // 3) state 인코딩
        val state = statePort.encode(payload)

        // 4) provider별 URL 생성
        return calendarOAuthUrlPort.createCalendarAuthUrl(provider, state)
    }

    /**
     * provider callback 처리:
     *  - state 검증
     *  - code -> token (provider 호출)
     *  - 토큰 저장
     *  - 이번달 동기화(save to DB + cache)
     *  - 연결 정보 저장(isActive=true)
     *  - 클라이언트로 리다이렉트할 URL 반환
     */
    @Transactional
    override fun handleCallbackAndGetClientRedirect(connectType: String, code: String, state: String?): String {

        // 1) state 파싱 (accountId, forwardUrl, nonce 등)
        val payload = statePort.decode(state)
                ?: throw IllegalArgumentException("Invalid or missing OAuth state")
        if (payload.connectType != null && payload.connectType != ConnectType.matchConnectType(connectType)) {
            throw IllegalArgumentException("Provider mismatch")
        }

        val accountId = payload.accountId ?: throw IllegalArgumentException("State missing accountId")
        val ct = ConnectType.matchConnectType(connectType)

        // 2) provider에 등록된 서버 redirectUri 획득
        val redirectUriUsed = statePort.redirectUriForProvider(connectType)

        // 3) code -> token
        val token = providerClient.exchangeCodeForToken(ct, code, redirectUriUsed)

        // 4) 토큰 저장
        tokenPort.save(token.copy(accountId = accountId))

        // 5) 이번 달 이벤트 전체 동기화 시도
        try {
            val now = LocalDateTime.now()
            val ym = YearMonth.of(now.year, now.monthValue)
            val events = providerClient.fetchEventsForMonth(ct, token.accessToken, ym.year, ym.monthValue)
            logger.info("Fetched events for ${ym}: size=${events.size}, events=$events")
            // TODO : 분산락
            try {
                eventSyncPort.saveMonthly(accountId, ym.year, ym.monthValue, events)

                logger.info("Fetched events for $ym : size=${events.size}")
                try {
                    logger.debug("Fetched events JSON: ${objectMapper.writeValueAsString(events)}")
                } catch (ex: Exception) {
                    logger.warn("Failed to log events as JSON", ex)
                }
            } catch (ex: Exception) {
                // DB 저장 실패면 기록하고 정책에 따라 처리하되, 캐시 실패 때문에 전체 롤백되는 건 막자!!!!!
                logger.warn("initial sync failed", ex)
            }

        } catch (ex: Exception) {
            // 초기 동기화 실패: 정책에 따라 롤백
            // TODO : 정책 따라서 수정하기
            logger.warn("initial sync failed", ex)
        }

        // 6) CalendarConnection 저장 및 업데이트 (isActive = true)
        val connection = CalendarConnection(
                id = 0L,
                accountId = accountId,
                provider = ct,
                isActive = true,
                lastSynced = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )
        // 분산락 or account id, connect type 유니크 upsert
        connectionPort.save(connection)

        // 7) 클라이언트로 리다이렉트할 URL 결정 (state.forwardUrl 우선)
        val forward = payload.forwardUrl ?: defaultClientRedirect
        return appendQueryParam(forward, "connected", "true")
    }

    private fun appendQueryParam(url: String, key: String, value: String): String {
        val sep = if (url.contains("?")) "&" else "?"
        return "$url$sep${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
    }

    // 확장: CalendarTokenResponse -> CalendarToken 변환 헬퍼 (token.toCalendarToken())

}