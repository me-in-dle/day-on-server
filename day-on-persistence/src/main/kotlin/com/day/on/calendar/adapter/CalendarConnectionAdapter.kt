package com.day.on.calendar.adapter

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.CalendarConnectionEntity
import com.day.on.calendar.model.CalendarConnection
import com.day.on.calendar.model.WatchChannel
import com.day.on.calendar.repository.CalendarConnectionJpaRepository
import com.day.on.calendar.usecase.outbound.CalendarConnectionPort
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class CalendarConnectionAdapter(private val jpaRepository: CalendarConnectionJpaRepository) : CalendarConnectionPort {
    override fun existsByAccountIdAndIsActive(accountId: Long): Boolean {
        return jpaRepository.existsByAccountIdAndIsActive(accountId, true)
    }

    override fun findByAccountId(accountId: Long): CalendarConnection? {
        return jpaRepository.findByAccountId(accountId)?.toDomain()
    }

    override fun findByChannelAndResource(
        channelId: String,
        resourceId: String,
    ): CalendarConnection? {
        return jpaRepository.findByChannelIdAndResourceId(channelId, resourceId)?.toDomain()
    }


    override fun updateSyncTokenByAccountId(
        accountId: Long,
        connectType: String,
        nextSyncToken: String,
    ) {
        val provider = ConnectType.matchConnectType(connectType)
        val updatedRows =
            jpaRepository.updateSyncTokenAndLastSynced(
                accountId = accountId,
                provider = provider,
                syncToken = nextSyncToken,
                lastSynced = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        if (updatedRows == 0) {
            throw IllegalStateException("No active CalendarConnection found for accountId=$accountId, provider=$provider")
        }
    }


    override fun updateSyncToken(
        connectionId: Long,
        syncToken: String,
    ) {
        jpaRepository.updateSyncToken(
            connectionId = connectionId,
            syncToken = syncToken,
            updatedAt = LocalDateTime.now(),
        )
    }

    override fun updateLastSynced(
        connectionId: Long,
        timestamp: LocalDateTime,
    ) {
        jpaRepository.updateLastSynced(
            connectionId = connectionId,
            timestamp = timestamp,
            updatedAt = LocalDateTime.now(),
        )
    }

    @Transactional(readOnly = false)
    override fun save(connection: CalendarConnection): CalendarConnection {
        val entity = CalendarConnectionEntity.fromDomain(connection)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

}
