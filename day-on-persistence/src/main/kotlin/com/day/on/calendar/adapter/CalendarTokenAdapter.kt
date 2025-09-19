package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.CalendarTokensEntity
import com.day.on.calendar.model.CalendarTokens
import com.day.on.calendar.repository.CalendarTokenJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarTokenPort
import com.day.on.calendar.usecase.outbound.TokenEncryptionPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class CalendarTokenAdapter(
        private val encryptionPort: TokenEncryptionPort,
        private val jpaRepository: CalendarTokenJpaRepository
) : CalendarTokenPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * save는 upsert 동작: 동일 accountId+connectType이 존재하면 업데이트, 없으면 insert
     */
    @Transactional
    override fun save(token: CalendarTokens) {
        val now = LocalDateTime.now()
        val existing = jpaRepository.findByAccountIdAndConnectType(token.accountId, token.connectType)

        try {
            val encryptedAccessToken = encryptionPort.encrypt(token.accessToken)
            val encryptedRefreshToken = encryptionPort.encrypt(token.refreshToken)

            if (existing != null) {
                val updatedEntity = CalendarTokensEntity(
                        id = existing.id,
                        accountId = existing.accountId,
                        connectType = existing.connectType,
                        accessToken = encryptedAccessToken,
                        refreshToken = encryptedRefreshToken,
                        createdAt = existing.createdAt,
                        updatedAt = now
                )
                jpaRepository.save(updatedEntity)
                logger.debug("Updated encrypted token for accountId=${token.accountId}, connectType=${token.connectType}")
            } else {
                // 새로 삽입
                val entity = CalendarTokensEntity(
                        accountId = token.accountId,
                        connectType = token.connectType,
                        accessToken = encryptedAccessToken,
                        refreshToken = encryptedRefreshToken,
                        createdAt = now,
                        updatedAt = now
                )
                jpaRepository.save(entity)
                logger.debug("Inserted new encrypted token for accountId=${token.accountId}, connectType=${token.connectType}")
            }
        } catch (ex: Exception) {
            logger.error("Failed to save encrypted token for accountId=${token.accountId}, connectType=${token.connectType}", ex)
            throw RuntimeException("토큰 암호화 및 저장 중 오류가 발생했습니다.", ex)
        }
    }

    /**
     * findByAccountIdAndService: 포트 시그니처에 맞춰서 service:String을 받아 처리.
     * service는 ConnectType 이름(예: "GOOGLE", "KAKAO")으로 보냄
     */
    override fun findByAccountIdAndService(accountId: Long, service: String): CalendarTokens? {
        val connectType = try {
            ConnectType.matchConnectType(service)
        } catch (ex: Exception) {
            logger.warn("Invalid connect type: $service", ex)
            return null
        }

        val entity = jpaRepository.findByAccountIdAndConnectType(accountId, connectType) ?: return null

        return try {
            // 복호화
            val decryptedAccessToken = encryptionPort.decrypt(entity.accessToken)
            val decryptedRefreshToken = encryptionPort.decrypt(entity.refreshToken)

            CalendarTokens(
                    accountId = entity.accountId,
                    connectType = entity.connectType,
                    accessToken = decryptedAccessToken,
                    refreshToken = decryptedRefreshToken,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
            )
        } catch (ex: Exception) {
            logger.error("Failed to decrypt token for accountId=$accountId, connectType=$connectType", ex)
            // 복호화 실패 시 예외 처리
            null
        }
    }
}