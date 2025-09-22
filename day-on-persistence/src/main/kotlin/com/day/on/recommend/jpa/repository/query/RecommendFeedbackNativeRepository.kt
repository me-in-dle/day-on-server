package com.day.on.recommend.jpa.repository.query

import com.day.on.place.type.PlaceType
import com.day.on.recommend.jpa.entity.RecommendFeedbackJpaEntity
import com.day.on.recommend.jpa.entity.embedded.RecommendPlaceEmbeddedJpaModel
import com.day.on.recommend.type.RecommendTimeSlot
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface RecommendFeedbackNativeRepository {
    fun findRecommendFeedbackPlaceLTypeBy(
        accountId: Long,
        emptyActiveTimes: List<RecommendTimeSlot>
    ): List<RecommendFeedbackJpaEntity>
}

@Repository
@Transactional(readOnly = true)
class RecommendFeedbackNativeRepositoryImpl(
    private val entityManager: EntityManager,
) : RecommendFeedbackNativeRepository {

    override fun findRecommendFeedbackPlaceLTypeBy(
        accountId: Long,
        emptyActiveTimes: List<RecommendTimeSlot>
    ): List<RecommendFeedbackJpaEntity> {
        val timeSlotParams = emptyActiveTimes.joinToString(",") { "'${it.name}'" }

        val query = """
            WITH ranked_feedback AS (
                SELECT 
                    rf.time_slot,
                    rf.place_l_type,
                    SUM(rf.matched_count) AS total_matched_count,
                    MAX(rf.updated_at) AS last_updated_at,
                    ROW_NUMBER() OVER (
                        PARTITION BY rf.time_slot
                        ORDER BY SUM(rf.matched_count) DESC, MAX(rf.updated_at) DESC
                    ) AS rn
                FROM recommend_feedback rf
                WHERE rf.account_id = :accountId
                    AND rf.time_slot IN ($timeSlotParams)
                    AND rf.updated_at > :targetAt
                    AND rf.place_l_type IS NOT NULL
                GROUP BY rf.time_slot, rf.place_l_type
            )
            SELECT 
                rf.id,
                rf.account_id,
                rf.place_id,
                rf.place_name,
                rf.place_l_type,
                rf.place_s_type,
                rf.place_s_type_korean_name,
                rf.time_slot,
                rf.mismatched_count,
                rf.matched_count,
                rf.last_mismatched_count,
                rf.last_matched_count,
                rf.feedback_actions,
                rf.created_at,
                rf.updated_at,
                rf.created_id,
                rf.updated_id
            FROM recommend_feedback rf
            JOIN ranked_feedback rrf ON rf.time_slot = rrf.time_slot AND rf.place_l_type = rrf.place_l_type
            WHERE rrf.rn <= 2 
            ORDER BY rrf.time_slot, rrf.total_matched_count DESC
        """.trimIndent()

        return entityManager.createNativeQuery(query)
            .setParameter("accountId", accountId)
            .setParameter("targetAt", LocalDateTime.now().minusDays(30))
            .resultList
            .map { row ->
                val result = row as Array<*>
                RecommendFeedbackJpaEntity(
                    id = (result[0] as Number).toLong(),
                    accountId = (result[1] as Number).toLong(),
                    recommendPlace = RecommendPlaceEmbeddedJpaModel(
                        placeId = (result[2] as Number).toLong(),
                        placeName = result[3] as String,
                        placeLType = PlaceType.valueOf(result[4] as String),
                        placeSType = result[5] as? String?,
                        placeSTypeKoreanName = result[6] as? String?
                    ),
                    timeSlot = RecommendTimeSlot.valueOf(result[7] as String),
                    mismatchedCount = (result[8] as Number).toLong(),
                    matchedCount = (result[9] as Number).toLong(),
                    lastMismatchedTime = result[10] as? LocalDateTime,
                    lastMatchedTime = result[11] as? LocalDateTime,
                    feedbackActions = (result[12] as String).split(",")
                        .map { com.day.on.recommend.type.FeedbackAction.valueOf(it) },
                    createdAt = result[13] as LocalDateTime,
                    updatedAt = result[14] as LocalDateTime,
                    createdId = result[15] as String,
                    updatedId = result[16] as String
                )
            }
            .toList()
    }
}