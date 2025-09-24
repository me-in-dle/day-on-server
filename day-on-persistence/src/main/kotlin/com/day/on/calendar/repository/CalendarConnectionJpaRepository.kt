package com.day.on.calendar.repository

import com.day.on.account.type.ConnectType
import com.day.on.calendar.jpa.CalendarConnectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CalendarConnectionJpaRepository : JpaRepository<CalendarConnectionEntity, Long> {
    fun existsByAccountIdAndIsActive(
        accountId: Long,
        isActive: Boolean = true,
    ): Boolean

    fun findByAccountId(accountId: Long): CalendarConnectionEntity?

    fun findByAccountIdAndProvider(
        accountId: Long,
        provider: ConnectType,
    ): CalendarConnectionEntity?

    fun findByChannelIdAndResourceId(
        channelId: String,
        resourceId: String,
    ): CalendarConnectionEntity?

    @Modifying
    @Query(
        """
        UPDATE CalendarConnectionEntity c
           SET c.syncToken = :syncToken,
               c.updatedAt = :updatedAt
         WHERE c.id = :connectionId
    """,
    )
    fun updateSyncToken(
        @Param("connectionId") connectionId: Long,
        @Param("syncToken") syncToken: String,
        @Param("updatedAt") updatedAt: LocalDateTime,
    ): Int

    @Modifying
    @Query(
        """
        UPDATE CalendarConnectionEntity c
           SET c.lastSynced = :timestamp,
               c.updatedAt = :updatedAt
         WHERE c.id = :connectionId
    """,
    )
    fun updateLastSynced(
        @Param("connectionId") connectionId: Long,
        @Param("timestamp") timestamp: LocalDateTime,
        @Param("updatedAt") updatedAt: LocalDateTime,
    ): Int

    @Modifying
    @Query(
        """
    UPDATE CalendarConnectionEntity c 
       SET c.syncToken = :syncToken, 
           c.lastSynced = :lastSynced,
           c.updatedAt = :updatedAt
     WHERE c.accountId = :accountId AND c.provider = :provider
""",
    )
    fun updateSyncTokenAndLastSynced(
        accountId: Long,
        provider: ConnectType,
        syncToken: String,
        lastSynced: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now(),
    ): Int
}
