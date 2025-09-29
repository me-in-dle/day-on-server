package com.day.on.location.usecase.outbound

import com.day.on.location.usecase.dto.CachedDistrict

/**
 * 행정구역 Port (아웃바운드 포트)
 */
interface DistrictPort {
    
    /**
     * 행정구역 데이터 저장
     */
    fun savePreprocessedDistricts(jsonFilePath: String): Int
    
    /**
     * 행정구역 데이터 삭제
     */
    fun deleteAllDistricts()

    /**
     * 전체 행정구역 조회 (캐싱용)
     * Cache Miss: DB 조회 후 메모리 캐시 저장
     * Cache Hit: 메모리 캐시에서 즉시 반환
     */
    fun findAll(): List<CachedDistrict>

}
