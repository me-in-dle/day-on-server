package com.day.on.adapter

import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.usecase.outbound.ConnectSocialAccountPort
import com.day.on.client.GoogleAccountClient
import com.day.on.common.logger.Logger
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Qualifier("userAccountGoogleSourceAdapter")
@Component
@EnableConfigurationProperties(GoogleOauthProperties::class)
class ConnectSocialAccountGoogleAdapter(
    private val googleAccountClient: GoogleAccountClient,
    private val googleOauthProperties: GoogleOauthProperties,
) : ConnectSocialAccountPort {

    /* TODO 코루틴 적용 */
    companion object : Logger()

    override fun connect(code: String, connectType: ConnectType): ConnectAccount {
        return runCatching {
            log.info("clientId = ${googleOauthProperties.clientId}, secretKey = ${googleOauthProperties.clientSecret}, redirectUri = ${googleOauthProperties.redirectUri}")
            val codeTokenBody = googleAccountClient.exchangeCodeForToken(
                clientId = googleOauthProperties.clientId,
                clientSecret = googleOauthProperties.clientSecret,
                code = code,
                redirectUri = googleOauthProperties.redirectUri,
            ).execute()
                .takeIf { it.isSuccessful }
                ?.body() ?: throw IllegalArgumentException("Failed to exchange code for token")

            googleAccountClient.getGoogleResponseByIdToken(codeTokenBody.idToken)
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
        name = name ?: "",
        accountId = 0L,
        connectType = ConnectType.GOOGLE,
        isEmailVerified = emailVerified,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class GoogleValidatedTokenResponse(
        @SerializedName("iss") val iss: String?,
        @SerializedName("azp") val azp: String?,
        @SerializedName("aud") val aud: String?,
        @SerializedName("sub") val sub: String?,
        @SerializedName("email") val email: String,
        @SerializedName("email_verified") val emailVerified: Boolean,
        @SerializedName("name") val name: String?,
        @SerializedName("picture") val picture: String?,
        @SerializedName("given_name") val givenName: String?,
        @SerializedName("family_name") val familyName: String?,
        @SerializedName("locale") val locale: String?,
        @SerializedName("iat") val iat: String?,
        @SerializedName("exp") val exp: String?,
        @SerializedName("alg") val alg: String?,
        @SerializedName("kid") val kid: String?,
        @SerializedName("typ") val idToken: String?,
    )

    data class GoogleTokenResponse(
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("id_token") val idToken: String,
        @SerializedName("expires_in") val expiresIn: Double?,
        @SerializedName("token_type") val tokenType: String?,
        @SerializedName("scope") val scope: String?,
        @SerializedName("refresh_token") val refreshToken: String? = null
    )
}