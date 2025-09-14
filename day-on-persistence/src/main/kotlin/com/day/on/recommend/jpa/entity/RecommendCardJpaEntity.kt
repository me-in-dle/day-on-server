package com.day.on.recommend.jpa.entity

import com.day.on.account.type.System
import com.day.on.place.type.PlaceType
import com.day.on.recommend.jpa.entity.embedded.RecommendPlaceEmbeddedJpaModel
import com.day.on.recommend.model.RecommendCard
import com.day.on.recommend.type.RecommendCardStatus
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Table(
    name = "recommend_card",
    indexes = [
        Index(name = "idx_recommend_card_01", columnList = "account_id"),
        Index(name = "idx_recommend_card_02", columnList = "dailyId"),
    ]
)
@Entity
class RecommendCardJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "account_id", columnDefinition = "BIGINT", nullable = false)
    val accountId: Long,

    @Column(name = "daily_id", columnDefinition = "BIGINT", nullable = false)
    val dailyId: Long,

    @Column(name = "contents", columnDefinition = "TEXT", nullable = true)
    val contents: String,

    @Column(name = "start_time_slot", nullable = true)
    val startTimeSlot: LocalDateTime?,

    @Column(name = "end_time_slot", nullable = true)
    val endTimeSlot: LocalDateTime?,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "place_l_type", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeLType: PlaceType,

    @Embedded
    val recommendPlace: RecommendPlaceEmbeddedJpaModel?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(30)", nullable = false)
    val status: RecommendCardStatus,

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
    fun toDomainModel() = RecommendCard(
        id = id,
        accountId = accountId,
        dailyId = dailyId,
        contents = contents,
        placeLType = placeLType,
        startTimeSlot = startTimeSlot,
        endTimeSlot = endTimeSlot,
        recommendPlace = recommendPlace?.toDomainModel(),
        status = status,
        createdId = createdId,
        createdAt = createdAt,
        updatedId = updatedId,
        updatedAt = updatedAt
    )

    companion object {
        fun from(recommendCard: RecommendCard) = with(recommendCard) {
            RecommendCardJpaEntity(
                id = id,
                accountId = accountId,
                dailyId = dailyId,
                contents = contents,
                placeLType = placeLType,
                startTimeSlot = startTimeSlot,
                endTimeSlot = endTimeSlot,
                recommendPlace = recommendPlace?.let { RecommendPlaceEmbeddedJpaModel.from(it) },
                status = status,
                createdId = createdId,
                createdAt = createdAt,
                updatedId = updatedId,
                updatedAt = updatedAt,
            )
        }
    }
}