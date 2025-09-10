package com.day.on.account.jpa.entity

import com.day.on.account.model.ConnectAccount
import com.day.on.account.type.ConnectType
import com.day.on.account.type.System
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "connect_account",
    indexes = [
        Index(name = "idx_connect_account_01", columnList = "account_id"),
        Index(name = "idx_connect_account_02", columnList = "email, connect_type"),
    ]
)
class ConnectAccountJpaEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long,
    @Column(name = "email", nullable = false, columnDefinition = "VARCHAR(100)")
    val email: String,
    @Column(name = "account_id", nullable = false, columnDefinition = "BIGINT")
    val accountId: Long,
    @Enumerated(value = STRING)
    @Column(name = "connect_type", nullable = false, columnDefinition = "VARCHAR(20)")
    val connectType: ConnectType,
    @Column(name = "is_email_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    val isEmailVerified: Boolean,
    @CreatedBy
    @Column(name = "created_id", nullable = false, columnDefinition = "VARCHAR(30)")
    val createdId: String = System.SYSTEM_ID.id,
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedBy
    @Column(name = "updated_id", nullable = false, columnDefinition = "VARCHAR(30)")
    val updatedId: String = System.SYSTEM_ID.id,
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomainModel() = com.day.on.account.model.ConnectAccount(
        id = id,
        email = email,
        accountId = accountId,
        connectType = connectType,
        isEmailVerified = isEmailVerified,
        createdId = createdId,
        createdAt = createdAt,
        updatedId = updatedId,
        updatedAt = updatedAt,
    )

    companion object {
        fun from(connectAccount: ConnectAccount) = with(connectAccount) {
            ConnectAccountJpaEntity(
                id = id,
                email = email,
                accountId = accountId,
                connectType = connectType,
                isEmailVerified = isEmailVerified,
                createdId = createdId,
                createdAt = createdAt,
                updatedId = updatedId,
                updatedAt = updatedAt,
            )
        }
    }
}