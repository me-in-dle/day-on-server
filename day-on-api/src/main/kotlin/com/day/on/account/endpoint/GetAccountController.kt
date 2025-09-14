package com.day.on.account.endpoint

import com.day.on.account.endpoint.const.HeaderName.ACCOUNT_ID
import com.day.on.account.model.Account
import com.day.on.account.usecase.outbound.AccountQueryPort
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/account")
class GetAccountController(
    private val accountQueryPort: AccountQueryPort,
) {

    @GetMapping("/info")
    fun getAccountInfo(@RequestHeader(ACCOUNT_ID) accountId: Long): ResponseEntity<AccountInfoHttpResponse> =
        ResponseEntity.status(HttpStatus.OK)
            .body(AccountInfoHttpResponse.fromDomainModel(accountQueryPort.findById(accountId)))

    data class AccountInfoHttpResponse(
        @JsonProperty("nickName")
        val nickName: String,
        @JsonProperty("age")
        val age: Int?,
    ) {
        companion object {
            fun fromDomainModel(account: Account): AccountInfoHttpResponse {
                return with(account) {
                    AccountInfoHttpResponse(
                        nickName = nickName,
                        age = age,
                    )
                }
            }
        }
    }
}