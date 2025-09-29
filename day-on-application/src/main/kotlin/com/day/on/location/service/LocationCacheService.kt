package com.day.on.location.service

import com.day.on.location.usecase.dto.CachedDistrict
import com.day.on.location.usecase.outbound.DistrictPort
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * 전체 행정구역 조회 (캐싱용)
 * 1. 하버사인 거리 계산
 * 2. Ray Casting 알고리즘 사용
 */
@Service
class LocationCacheService(
    private val districtPort: DistrictPort
) {

    private val logger = LoggerFactory.getLogger(LocationCacheService::class.java)

    @Cacheable("allDistricts")
    fun getAllDistricts(): List<CachedDistrict> {
        logger.info("Cache Miss: DB에서 전체 행정구역 조회 시작")
        val districts = districtPort.findAll()
        logger.info("메모리에 캐싱된 행정구역 개수: {}개", districts.size)
        return districts
    }

}