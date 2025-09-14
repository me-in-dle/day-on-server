package com.day.on.account.endpoint

import com.day.on.account.config.ClientRedirectUrlProperties
import com.day.on.account.endpoint.const.CookieName.DAY_ON_ACCESS_TOKEN_NAME
import com.day.on.account.endpoint.const.CookieName.DAY_ON_REFRESH_TOKEN_NAME
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.inbound.CreateAccountUseCase
import com.day.on.account.usecase.outbound.AuthenticationTokenPort
import com.day.on.account.usecase.outbound.ConnectSocialAccountPort
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@EnableConfigurationProperties(ClientRedirectUrlProperties::class)
@RequestMapping("/api/v1/account")
class CreateAccountController(
    private val createAccountUseCase: CreateAccountUseCase,
    private val authenticationTokenPort: AuthenticationTokenPort,
    private val connectSocialAccountPort: ConnectSocialAccountPort,
    private val clientRedirectUrlProperties: ClientRedirectUrlProperties,
) {
    @GetMapping("/social/{connectType}")
    fun createAccount(
        @PathVariable("connectType") connectType: String,
        @RequestParam("code", required = true) code: String,
        @RequestParam("state", required = false) state: String?,
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val connectAccount = connectSocialAccountPort.connect(code, ConnectType.matchConnectType(connectType))
        val account = createAccountUseCase.createAccount(connectAccount)
        authenticationTokenPort.issueAll(account.id).let { token ->
            val redirectUrl =
                clientRedirectUrlProperties.url + state?.takeIf { it.isNotBlank() }?.let { "?forwardUrl=$it" }.orEmpty()

            val accessTokenCookie = Cookie(DAY_ON_ACCESS_TOKEN_NAME, token.accessToken).apply {
                isHttpOnly = false
                maxAge = 60 * 60 * 24 * 3
                path = "/"
                setAttribute("SameSite", "Strict")
            }

            val refreshTokenCookie = Cookie(DAY_ON_REFRESH_TOKEN_NAME, token.refreshToken).apply {
                isHttpOnly = false
                maxAge = 60 * 60 * 24 * 7
                path = "/"
                setAttribute("SameSite", "Strict")
            }

            response.addCookie(accessTokenCookie)
            response.addCookie(refreshTokenCookie)

            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build()
        }
    }
}