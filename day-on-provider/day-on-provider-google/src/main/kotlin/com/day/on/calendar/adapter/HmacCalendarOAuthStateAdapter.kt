package com.day.on.calendar.adapter

import com.day.on.calendar.usecase.dto.OAuthStatePayload
import com.day.on.calendar.usecase.outbound.CalendarOAuthStatePort
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class HmacCalendarOAuthStateAdapter(
        @Value("\${calendar.oauth.state.secret}") private val secret: String,
        @Value("\${calendar.oauth.redirect-uri:http://localhost:8080/api/v1/calendar/oauth/callback}") private val defaultRedirect: String,
        private val googleCalendarProps: GoogleCalendarOauthProperties
) : CalendarOAuthStatePort {

    private val mapper = jacksonObjectMapper()
    private val HMAC_ALGO = "HmacSHA256"

    /**
     * payload -> state string (b64.sig)
     */
    override fun encode(payload: OAuthStatePayload): String {
        val json = mapper.writeValueAsString(payload)
        val b64 = Base64.getUrlEncoder().withoutPadding().encodeToString(json.toByteArray(Charsets.UTF_8))
        val sig = hmac(b64)
        return "$b64.$sig"
    }

    /**
     * state -> payload (validity + signature 검사)
     */
    override fun decode(state: String?): OAuthStatePayload? {
        if (state.isNullOrBlank()) return null
        val parts = state.split(".")
        if (parts.size != 2) return null
        val (b64, sig) = parts
        if (hmac(b64) != sig) return null // 서명 검증 실패

        val json = try {
            String(Base64.getUrlDecoder().decode(b64), Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val payload = try {
            mapper.readValue<OAuthStatePayload>(json)
        } catch (e: Exception) {
            return null
        }

        // 만료 검사
        val now = System.currentTimeMillis()
        if (now > payload.issuedAtMillis + payload.expiresInSeconds * 1000) return null

        return payload
    }

    /**
     * provider별 서버 redirectUri를 반환.
     */
    override fun redirectUriForProvider(provider: String): String {
        return when(provider?.lowercase()) {
            "google" -> googleCalendarProps.redirectUri
            // TODO : 카카오 추가
            else -> defaultRedirect
        }
    }

    private fun hmac(data: String): String {
        val mac = Mac.getInstance(HMAC_ALGO)
        val keySpec = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), HMAC_ALGO)
        mac.init(keySpec)
        val raw = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw)
    }
}