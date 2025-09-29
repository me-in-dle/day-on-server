package com.day.on.location.jpa.repository

import com.day.on.location.jpa.entity.DistrictEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * 행정구역 JPA Repository
 */
interface DistrictRepository : JpaRepository<DistrictEntity, Long> {

    /**
     * 행정구역 데이터 삭제
     */
    @Modifying
    @Query(value = "TRUNCATE TABLE administrative_districts", nativeQuery = true)
    fun truncateTable()

}