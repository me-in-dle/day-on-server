package com.day.on.recommend.jpa.repository

import com.day.on.recommend.jpa.entity.RecommendFeedbackJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendFeedbackJpaEntityRepository : JpaRepository<RecommendFeedbackJpaEntity, Long>