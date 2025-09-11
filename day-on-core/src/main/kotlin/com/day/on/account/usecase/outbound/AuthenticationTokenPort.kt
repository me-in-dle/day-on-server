package com.day.on.account.usecase.outbound

import com.day.on.account.model.AuthenticationToken

interface AuthenticationTokenPort {
    fun issueAll(accountId: Long): AuthenticationToken

    fun issueAccessToken(accountId: Long): AuthenticationToken

    fun issueRefreshToken(accountId: Long): AuthenticationToken

    fun revokeTokens(accountId: Long)
}