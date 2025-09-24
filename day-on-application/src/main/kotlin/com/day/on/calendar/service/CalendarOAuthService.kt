package com.day.on.calendar.service

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.usecase.dto.OAuthStatePayload
import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase
import com.day.on.calendar.usecase.outbound.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class CalendarOAuthService(
    private val statePort: CalendarOAuthStatePort,
    private val calendarOAuthUrlPort: CalendarOAuthUrlPort,
    private val providerClient: CalendarProviderClientPort,
    private val tokenPort: CalendarTokenPort,
    private val eventSyncPort: CalendarEventSyncPort,
    private val connectionPort: CalendarConnectionPort,
    private val asyncWatchRegistrar: AsyncWatchRegistrar,
) : CalendarOAuthUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${calendar.oauth.client-redirect-url:http://localhost:5173/calendar}")
    private lateinit var defaultClientRedirect: String

    // TODO : 경로 추후 수정 gate way + yml
    @Value("\${calendar.webhook.url:https://884d29a33a0c.ngrok-free.app/api/v1/calendar/webhook}")
    private lateinit var webhookUrl: String

    /**
     * 클라이언트에게 전달할 OAuth 인증 URL을 생성.
     * TODO : forwardUrl: 인증 끝난 뒤 사용자에게 다시 보낼 client URL
     */
    override fun generateCalendarOAuthUrl(
        accountId: Long,
        provider: String,
        forwardUrl: String?,
    ): String {
        // 1) connectType 매핑(optional) — state에 기록하면 callback에서 검증 가능
        val connectType =
            try {
                ConnectType.matchConnectType(provider)
            } catch (ex: Exception) {
                // TODO : 예외 던지기
                null
            }

        // 2) state payload 생성
        val payload =
            OAuthStatePayload(
                accountId = accountId,
                forwardUrl = forwardUrl,
                connectType = connectType,
                issuedAtMillis = System.currentTimeMillis(),
                expiresInSeconds = 300L,
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
    override fun handleCallbackAndGetClientRedirect(
        connectType: String,
        code: String,
        state: String?,
    ): String {
        // 1) state 파싱 (accountId, forwardUrl, nonce 등)
        val payload =
            statePort.decode(state)
                ?: throw IllegalArgumentException("Invalid or missing OAuth state") // TODO : 이거 만료됐을 때 예외처리 ex ) 클라 -> 메인화면

        if (payload.connectType != null &&  payload.connectType != ConnectType.matchConnectType(connectType)) {
            throw IllegalArgumentException("Provider mismatch")
        }

        val accountId = payload.accountId ?: throw IllegalArgumentException("State missing accountId")
        val ct = ConnectType.matchConnectType(connectType)

        // 2) provider에 등록된 서버 redirectUri 획득
        val redirectUriUsed = statePort.redirectUriForProvider(connectType)

        // TODO : 기존 연동 상태 확인 후 -> 활성이면 토큰 갱신 , 새 연동이면 연동 (클라이언트 단에서도 중복 연동 안 되게 처리)
        // 3) code -> token
        val token = providerClient.exchangeCodeForToken(ct, code, redirectUriUsed)

        // 4) 토큰 저장
        tokenPort.save(token.copy(accountId = accountId))

        // 5) 일주일치 초기 동기화
        var weekSyncSuccess = false
        var nextSyncToken: String? = null
        try {
            val today = LocalDate.now()
            val weekEnd = today.plusDays(7)

            logger.info("Starting week sync for accountId=$accountId ($today ~ $weekEnd)")

            // daily schedules 미리 범위만큼 적재
            eventSyncPort.createDailySchedulesForRange(accountId, today, weekEnd)

            val (weekEvents, syncToken) =
                providerClient.fetchEventsForDateRange(
                    ct,
                    token.accessToken,
                    today,
                    weekEnd,
                )

            if (weekEvents.isNotEmpty()) {
                eventSyncPort.saveEventsForDateRange(accountId, today, weekEnd, weekEvents)
            }

            weekSyncSuccess = true
            nextSyncToken = syncToken
            logger.info("Week sync completed: ${weekEvents.size} events")
        } catch (ex: Exception) {
            logger.error("Week sync failed for accountId=$accountId", ex)
            throw IllegalStateException("Initial calendar sync failed. Please try again.", ex)
        }
        // 6. 동기화 성공 시에만 connection 저장
        // TODO : 이미 연동된 계정이면 예외던지기.. findByAccountIdProvider(accountId, provider)
        if (weekSyncSuccess) {
            val connection =
                CalendarConnection(
                    id = 0L,
                    accountId = accountId,
                    resourceId = "",
                    channelId = "",
                    syncToken = nextSyncToken ?: "",
                    expiration = null,
                    provider = ct,
                    isActive = true,
                    lastSynced = LocalDateTime.now(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )

            // 기존 연결이 있으면 업데이트, 없으면 생성
            connectionPort.save(connection)
            logger.info("Calendar connection saved: accountId=$accountId, provider=$ct")

            // watch 등록
            asyncWatchRegistrar.registerWatch(accountId, ct, token.accessToken, webhookUrl)
        }

        // 7) 클라이언트로 리다이렉트할 URL 결정 (state.forwardUrl 우선)
        val forward = payload.forwardUrl ?: defaultClientRedirect
        return appendQueryParam(forward, "connected", "true")
    }

    private fun appendQueryParam(
        url: String,
        key: String,
        value: String,
    ): String {
        val sep = if (url.contains("?")) "&" else "?"
        return "$url$sep${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
    }

    // 확장: CalendarTokenResponse -> CalendarToken 변환 헬퍼 (token.toCalendarToken())
}
