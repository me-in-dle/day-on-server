package com.day.on.security

import com.day.on.calendar.usecase.outbound.TokenEncryptionPort
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


@Component
class TokenEncryptionAdapter : TokenEncryptionPort {

    @Value("\${spring.calendar.token.encryption.key}")
    private lateinit var encryptionKey: String

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
        private const val REQUIRED_KEY_LENGTH = 32 // 256-bit
    }

    @PostConstruct
    private fun validateKey() {
        val keyBytes = encryptionKey.toByteArray(Charsets.UTF_8)
        if (keyBytes.size != REQUIRED_KEY_LENGTH) {
            throw IllegalArgumentException(
                    "Encryption key must be exactly $REQUIRED_KEY_LENGTH bytes. Current: ${keyBytes.size} bytes"
            )
        }
    }

    private fun getValidatedKey(): SecretKeySpec {
        val keyBytes = encryptionKey.toByteArray(Charsets.UTF_8)
        // 해시를 사용해 32바이트로 만들기
        val normalizedKey = if (keyBytes.size != REQUIRED_KEY_LENGTH) {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.digest(keyBytes)
        } else {
            keyBytes
        }
        return SecretKeySpec(normalizedKey, ALGORITHM)
    }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = getValidatedKey()

        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val result = ByteArray(GCM_IV_LENGTH + encryptedBytes.size)
        System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH)
        System.arraycopy(encryptedBytes, 0, result, GCM_IV_LENGTH, encryptedBytes.size)

        return Base64.getEncoder().encodeToString(result)
    }

    override fun decrypt(encryptedText: String): String {
        val data = Base64.getDecoder().decode(encryptedText)

        val iv = ByteArray(GCM_IV_LENGTH)
        val encryptedBytes = ByteArray(data.size - GCM_IV_LENGTH)
        System.arraycopy(data, 0, iv, 0, GCM_IV_LENGTH)
        System.arraycopy(data, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = getValidatedKey()
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}