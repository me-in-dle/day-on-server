package com.day.on.adapter

import com.day.on.account.model.AuthenticationToken
import com.day.on.account.usecase.outbound.AuthenticationTokenPort
import com.day.on.common.outbound.CachePort
import com.day.on.config.JwtTokenBuilder
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class AuthenticationJwtTokenAdapter(
    private val cachePort: CachePort,
    private val jwtTokenBuilder: JwtTokenBuilder,
) : AuthenticationTokenPort {
    override fun issueAll(accountId: Long): AuthenticationToken {
        jwtTokenBuilder.buildJwtToken(
            subject = accountId.toString(),
            tokenTime = ACCESS_TOKEN_TTL,
        ).let { accessToken ->
            jwtTokenBuilder.buildJwtToken(
                subject = accountId.toString(),
                tokenTime = REFRESH_TOKEN_TTL,
            ).let { refreshToken ->
                cachePort.put(
                    key = "$ACCESS_TOKEN_PREFIX:$accountId",
                    value = refreshToken,
                    ttlMillis = REFRESH_TOKEN_TTL,
                )

                cachePort.put(
                    key = "$REFRESH_TOKEN_PREFIX:$accountId",
                    value = refreshToken,
                    ttlMillis = REFRESH_TOKEN_TTL,
                )
                return AuthenticationToken(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                )
            }
        }
    }

    override fun issueAccessToken(accountId: Long): AuthenticationToken {
        jwtTokenBuilder.buildJwtToken(
            subject = accountId.toString(),
            tokenTime = ACCESS_TOKEN_TTL,
        ).let { accessToken ->
            cachePort.put(
                key = "$ACCESS_TOKEN_PREFIX:$accountId",
                value = accessToken,
                ttlMillis = ACCESS_TOKEN_TTL,
            )
            return AuthenticationToken(
                accessToken = accessToken,
                refreshToken = "",
            )
        }
    }

    override fun issueRefreshToken(accountId: Long): AuthenticationToken {
        jwtTokenBuilder.buildJwtToken(
            subject = accountId.toString(),
            tokenTime = REFRESH_TOKEN_TTL,
        ).let { refreshToken ->
            cachePort.put(
                key = "$REFRESH_TOKEN_PREFIX:$accountId",
                value = refreshToken,
                ttlMillis = REFRESH_TOKEN_TTL,
            )
            return AuthenticationToken(
                accessToken = "",
                refreshToken = refreshToken,
            )
        }
    }

    override fun revokeTokens(accountId: Long) {
        cachePort.delete("$ACCESS_TOKEN_PREFIX:$accountId")
        cachePort.delete("$REFRESH_TOKEN_PREFIX:$accountId")
    }

    companion object {
        private val ACCESS_TOKEN_TTL = TimeUnit.HOURS.toSeconds(1)
        private val REFRESH_TOKEN_TTL = TimeUnit.DAYS.toSeconds(7)
        private const val ACCESS_TOKEN_PREFIX = "access_token"
        private const val REFRESH_TOKEN_PREFIX = "refresh_token"
    }
}