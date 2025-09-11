package com.day.on.account.jpa.entity

import com.day.on.account.model.Account
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "accounts")
class AccountJpaEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long,
    @Column(name = "nick_name", nullable = false, columnDefinition = "VARCHAR(50)")
    val nickName: String,
    @CreatedBy
    @Column(name = "created_id", nullable = false, columnDefinition = "VARCHAR(30)")
    val createdId: String,
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @LastModifiedBy
    @Column(name = "updated_id", nullable = false, columnDefinition = "VARCHAR(30)")
    val updatedId: String,
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
    @Column(name = "age", nullable = true, columnDefinition = "INT")
    val age: Int?,
) {
    fun toDomainModel() = Account(
        id = id,
        nickName = nickName,
        createdId = createdId,
        createdAt = createdAt,
        updatedId = updatedId,
        updatedAt = updatedAt,
        age = age,
    )

    companion object {
        fun from(account: Account) = with(account) {
            AccountJpaEntity(
                id = id,
                nickName = nickName,
                createdId = createdId,
                createdAt = createdAt,
                updatedId = updatedId,
                updatedAt = updatedAt,
                age = age,
            )
        }
    }
}