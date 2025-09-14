package com.day.on.recommend.jpa.repository

import com.day.on.recommend.jpa.entity.RecommendCardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendCardJpaEntityRepository : JpaRepository<RecommendCardJpaEntity, Long>