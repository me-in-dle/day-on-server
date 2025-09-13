package com.day.on.websocket.principal

import java.security.Principal

data class StompPrincipal(
    val accountId: String,
) : Principal {
    val attributes: Map<String, Any> = emptyMap()
    override fun getName(): String = accountId
}