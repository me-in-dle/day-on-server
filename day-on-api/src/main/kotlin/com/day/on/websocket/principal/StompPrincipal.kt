package com.day.on.websocket.principal

import java.security.Principal

data class StompPrincipal(
    val userId: String
) : Principal {
    override fun getName(): String = userId
}