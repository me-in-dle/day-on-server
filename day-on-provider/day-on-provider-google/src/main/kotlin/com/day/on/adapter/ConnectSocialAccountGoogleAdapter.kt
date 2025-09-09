package com.day.on.adapter

import com.day.on.client.GoogleAccountClient
import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.type.System
import com.day.on.account.type.System.SYSTEM_ID
import com.day.on.account.usecase.outbound.ConnectSocialAccountPort
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Qualifier("userAccountGoogleSourceAdapter")
@Component
class ConnectSocialAccountGoogleAdapter(
    private val googleAccountClient: GoogleAccountClient,
) : ConnectSocialAccountPort {

    override fun connect(code: String, connectType: ConnectType): ConnectAccount {
        return runCatching {
            googleAccountClient.getGoogleResponseByIdToken(code)
        }.getOrElse {
            throw IllegalArgumentException("Failed to get Google response: ${it.message}", it)
        }.execute().let { response ->
            when {
                response.isSuccessful -> response.body()?.toSnsAccount()
                    ?: throw IllegalArgumentException("Empty response body from Google API")

                else -> throw IllegalArgumentException("Google API request failed with code: ${response.code()}")
            }
        }
    }

    private fun GoogleValidatedTokenResponse.toSnsAccount() = ConnectAccount(
        id = 0L,
        email = email,
        accountId = 0L,
        connectType = ConnectType.GOOGLE,
        isEmailVerified = emailVerified,
        createdId = SYSTEM_ID.id,
        createdAt = LocalDateTime.now(),
        updatedId = SYSTEM_ID.id,
        updatedAt = LocalDateTime.now(),
    )

    data class GoogleValidatedTokenResponse(
        @JsonProperty("iss") val iss: String,
        @JsonProperty("azp") val azp: String,
        @JsonProperty("aud") val aud: String,
        @JsonProperty("sub") val sub: String,
        @JsonProperty("email") val email: String,
        @JsonProperty("email_verified") val emailVerified: Boolean,
        @JsonProperty("name") val name: String,
        @JsonProperty("picture") val picture: String,
        @JsonProperty("given_name") val givenName: String,
        @JsonProperty("family_name") val familyName: String,
        @JsonProperty("locale") val locale: String,
        @JsonProperty("iat") val iat: String,
        @JsonProperty("exp") val exp: String,
        @JsonProperty("alg") val alg: String,
        @JsonProperty("kid") val kid: String,
        @JsonProperty("typ") val idToken: String,
    )
}