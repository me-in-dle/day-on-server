package com.day.on.recommend.jpa.entity

import com.day.on.account.type.System
import com.day.on.convert.ListFeedbackActionToStringConverter
import com.day.on.recommend.jpa.entity.embedded.RecommendPlaceEmbeddedJpaModel
import com.day.on.recommend.model.RecommendFeedback
import com.day.on.recommend.type.FeedbackAction
import com.day.on.recommend.type.RecommendTimeSlot
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Table(
    name = "recommend_feedback",
    indexes = [
        Index(name = "idx_recommend_feedback_01", columnList = "account_id"),
        Index(name = "idx_recommend_feedback_02", columnList = "account_id,place_l_type,place_s_type")
    ]
)
@Entity
class RecommendFeedbackJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "account_id", columnDefinition = "BIGINT", nullable = false)
    val accountId: Long,

    @Embedded
    val recommendPlace: RecommendPlaceEmbeddedJpaModel,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "time_slot", columnDefinition = "VARCHAR(30)", nullable = false)
    val timeSlot: RecommendTimeSlot,

    @Column(name = "mismatched_count", columnDefinition = "BIGINT", nullable = false)
    val mismatchedCount: Long,

    @Column(name = "matched_count", columnDefinition = "BIGINT", nullable = false)
    val matchedCount: Long,

    @Column(name = "last_mismatched_count", nullable = true)
    val lastMismatchedTime: LocalDateTime?,

    @Column(name = "last_matched_count", nullable = true)
    val lastMatchedTime: LocalDateTime?,

    @Column(name = "feedback_actions", columnDefinition = "VARCHAR(255)", nullable = false)
    @Convert(converter = ListFeedbackActionToStringConverter::class)
    val feedbackActions: List<FeedbackAction>,

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
    fun toDomainModel() = RecommendFeedback(
        id = id,
        accountId = accountId,
        recommendPlace = recommendPlace.toDomainModel(),
        timeSlot = timeSlot,
        mismatchedCount = mismatchedCount,
        matchedCount = matchedCount,
        lastMismatchedTime = lastMismatchedTime,
        lastMatchedTime = lastMatchedTime,
        feedbackActions = feedbackActions,
        createdId = createdId,
        createdAt = createdAt,
        updatedId = updatedId,
        updatedAt = updatedAt,
    )

    companion object {
        fun from(recommendFeedback: RecommendFeedback) = with(recommendFeedback) {
            RecommendFeedbackJpaEntity(
                id = id,
                accountId = accountId,
                recommendPlace = RecommendPlaceEmbeddedJpaModel.from(recommendPlace),
                timeSlot = timeSlot,
                mismatchedCount = mismatchedCount,
                matchedCount = matchedCount,
                lastMismatchedTime = lastMismatchedTime,
                lastMatchedTime = lastMatchedTime,
                feedbackActions = feedbackActions,
                createdId = createdId,
                createdAt = createdAt,
                updatedId = updatedId,
                updatedAt = updatedAt,
            )
        }
    }
}