package com.day.on.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtTokenBuilder : InitializingBean {
    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private lateinit var key: Key

    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun buildJwtToken(
        subject: String,
        tokenTime: Long,
    ): String {
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tokenTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}