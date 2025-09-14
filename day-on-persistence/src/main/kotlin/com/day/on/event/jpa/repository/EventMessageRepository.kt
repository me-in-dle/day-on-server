package com.day.on.event.jpa.repository

import com.day.on.event.jpa.entity.EventMessageJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventMessageRepository : JpaRepository<EventMessageJpaEntity, Long>