package com.day.on.calendar.usecase.dto

import com.day.on.account.type.ConnectType

data class OAuthStatePayload(
    val accountId: Long?,
    val forwardUrl: String?, // 클라이언트로 리다이렉트
    val connectType: ConnectType?,
    val issuedAtMillis: Long,
    val expiresInSeconds: Long = 300L, // 기본 5분
    val nonce: String? = java.util.UUID.randomUUID().toString()
)