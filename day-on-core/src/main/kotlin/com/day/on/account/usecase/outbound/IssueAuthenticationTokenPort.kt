package com.day.on.account.usecase.outbound

import com.day.on.account.model.AuthenticationToken
import java.util.concurrent.TimeUnit

interface IssueAuthenticationTokenPort {
    fun issueAll(accountId: Long): AuthenticationToken

    fun issueToken(accountId: Long, timeUnit: TimeUnit): AuthenticationToken
}