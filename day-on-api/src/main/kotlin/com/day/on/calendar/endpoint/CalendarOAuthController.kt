package com.day.on.calendar.endpoint

import com.day.on.account.endpoint.const.HeaderName
import com.day.on.account.type.ConnectType
import com.day.on.adapter.ConnectSocialAccountGoogleAdapter.Companion.log
import com.day.on.api.response.SuccessResponse
import com.day.on.calendar.usecase.inbound.CalendarOAuthUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarOAuthController(
        private val calendarOAuthUseCase: CalendarOAuthUseCase
) {
    /*
    * 최초 진입시 캘린더 권한 요청
    */
    @GetMapping("/oauth-url")
    fun getOAuthUrl(
            @RequestHeader(HeaderName.ACCOUNT_ID) accountId: Long,
            @RequestParam provider: String,
            @RequestParam(required = false) forwardUrl : String?
    ): SuccessResponse<String> {
        // validate forwardUrl origin (화이트리스트)
//        if (forwardUrl != null && !redirectValidator.isAllowed(forwardUrl)) {
//            throw IllegalArgumentException("invalid redirect url")
//        }
        // TODO: forwardUrl 화이트리스트 검증
        val url = calendarOAuthUseCase.generateCalendarOAuthUrl(accountId, provider, forwardUrl)
        return SuccessResponse.of(url)
    }

    @GetMapping("/oauth/callback/{connectType}")
    fun calendarOAuthCallback(
            @PathVariable("connectType") connectType: String,
            @RequestParam("code") code: String,
            @RequestParam("state", required = false) state: String?
    ): ResponseEntity<Void> {
        log.info("OAuth callback received. connectType=$connectType, code=${code.take(8)}..., state=${state?.take(100)}")
        val clientRedirectUrl = calendarOAuthUseCase.handleCallbackAndGetClientRedirect(connectType, code, state)
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(clientRedirectUrl)).build()
    }

}