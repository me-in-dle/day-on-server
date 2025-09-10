package com.day.on.account.endpoint

import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.inbound.CreateAccountUseCase
import com.day.on.account.usecase.outbound.AuthenticationTokenPort
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/account")
class CreateAccountController(
    private val createAccountUseCase: CreateAccountUseCase,
    private val authenticationTokenPort: AuthenticationTokenPort,
) {
    @GetMapping("/social/{connectType}")
    fun createAccount(
        @RequestParam("code") code: String,
        @PathVariable("connectType") connectType: String,
    ): ResponseEntity<AuthenticationTicketResponse> {
        val account = createAccountUseCase.createAccount(null, "code", ConnectType.matchConnectType(connectType))
        authenticationTokenPort.issueAll(account.id).let { token ->
            return ResponseEntity.ok(
                AuthenticationTicketResponse(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                ),
            )
        }
    }

    data class AuthenticationTicketResponse(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("refresh_token") val refreshToken: String,
    )
}