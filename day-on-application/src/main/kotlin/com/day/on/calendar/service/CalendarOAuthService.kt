package com.day.on.calendar.service

import com.day.on.account.type.ConnectType
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.model.ScheduleContent
import com.day.on.calendar.model.WatchChannel
import com.day.on.calendar.type.CalendarIdType
import com.day.on.calendar.usecase.dto.OAuthStatePayload
import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase
import com.day.on.calendar.usecase.outbound.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class CalendarOAuthService(
    private val statePort: CalendarOAuthStatePort,
    private val calendarOAuthUrlPort: CalendarOAuthUrlPort,
    private val providerClient: CalendarProviderClientPort,
    private val tokenPort: CalendarTokenPort,
    private val eventSyncPort: CalendarEventSyncPort,
    private val connectionPort: CalendarConnectionPort,
) : CalendarOAuthUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${calendar.oauth.client-redirect-url:http://localhost:5173/calendar}")
    private lateinit var defaultClientRedirect: String

    // TODO : 경로 추후 수정 gate way + yml
    @Value("\${calendar.webhook.url:https://320f37ba29ab.ngrok-free.app/api/v1/calendar/webhook}")
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
        // TODO : 이미 연동을 한 계정은 예외처리 (test때는 일단 패스)
        val payload = statePort.decode(state)
            ?: throw IllegalArgumentException("Invalid or missing OAuth state")

        if (payload.connectType != null &&
            payload.connectType != ConnectType.matchConnectType(connectType)
        ) {
            throw IllegalArgumentException("Provider mismatch")
        }

        val accountId = payload.accountId
            ?: throw IllegalArgumentException("State missing accountId")
        val ct = ConnectType.matchConnectType(connectType)

        // 1. 외부 API 호출
        val redirectUriUsed = statePort.redirectUriForProvider(connectType)

        val token = providerClient.exchangeCodeForToken(ct, code, redirectUriUsed)

        val today = LocalDate.now()
        val weekEnd = today.plusDays(7)
        logger.info("Starting week sync for accountId=$accountId ($today ~ $weekEnd)")

        val (weekEvents, syncToken) = try {
            providerClient.fetchEventsForDateRange(ct, token.accessToken, today, weekEnd)
        } catch (ex: Exception) {
            logger.error("Week sync failed for accountId=$accountId", ex)
            throw IllegalStateException("Initial calendar sync failed. Please try again.", ex)
        }

        // watch 등록
        val resp = providerClient.registerWatch(ct, token.accessToken, CalendarIdType.PRIMARY, webhookUrl)
        val watchChannel =
            WatchChannel(
                channelId = resp.id,
                resourceId = resp.resourceId?: "",
                expiration = resp.expiration?.toLongOrNull()?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime()
                }
            )
        logger.info("[Watch] 등록 for accountId=$accountId, channelId=${resp.id}, resourceId=${resp.resourceId}")

        // 2. DB 저장 (트랜잭션 안)
        saveIntegrationResults(accountId, ct, token, weekEvents, syncToken, watchChannel)

        // 3. redirect
        val forward = payload.forwardUrl ?: defaultClientRedirect
        return appendQueryParam(forward, "connected", "true")
    }

    fun saveIntegrationResults(accountId: Long, ct: ConnectType, token: CalendarTokens, weekEvents: List<Pair<LocalDate, ScheduleContent>>, syncToken: String?, watchChannel: WatchChannel) {
        // 토큰 저장
        tokenPort.save(token.copy(accountId = accountId))

        // 이벤트 저장
        if (weekEvents.isNotEmpty()) {
            val today = LocalDate.now()
            val weekEnd = today.plusDays(7)
            eventSyncPort.createDailySchedulesForRange(accountId, today, weekEnd)
            eventSyncPort.saveEventsForDateRange(accountId, today, weekEnd, weekEvents)
        }

        // 연결 저장
        val connection = CalendarConnection(
            id = 0L,
            accountId = accountId,
            resourceId = watchChannel.resourceId,
            channelId = watchChannel.channelId,
            syncToken = syncToken,
            expiration = watchChannel.expiration,
            provider = ct,
            isActive = true,
            lastSynced = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        connectionPort.save(connection)

        logger.info("[외부 초기화 연동 성공] saved: accountId=$accountId, provider=$ct")
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
