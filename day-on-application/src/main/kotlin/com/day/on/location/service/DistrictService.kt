package com.day.on.location.service

import com.day.on.location.usecase.dto.CachedDistrict
import com.day.on.location.usecase.dto.DistrictOperationResponse
import com.day.on.location.usecase.dto.DistrictSearchResponse
import com.day.on.location.usecase.inbound.DistrictUseCase
import com.day.on.location.usecase.outbound.DistrictPort
import com.day.on.location.util.GeometryUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 행정구역 UseCase 구현체 (인바운드 어댑터)
 */
@Service
@Transactional(readOnly = true)
class DistrictService(
    private val districtDataPort: DistrictPort,
    private val locationCacheService: LocationCacheService,
    private val geometryUtil: GeometryUtil
) : DistrictUseCase {

    private val logger = LoggerFactory.getLogger(DistrictService::class.java)

    @Transactional
    override fun savePreprocessedDistricts(jsonFilePath: String): DistrictOperationResponse {
        val savedCount = districtDataPort.savePreprocessedDistricts(jsonFilePath)
        return DistrictOperationResponse(
            savedCount = savedCount,
            message = "행정구역 데이터 저장 완료"
        )
    }

    @Transactional
    override fun deleteAllDistricts(confirm: String) {
        districtDataPort.deleteAllDistricts()
    }

    override fun findDistrictByCoordinates(latitude: Double, longitude: Double): DistrictSearchResponse {
        // 1. 캐시에서 전체 행정구역 조회
        val allDistricts = locationCacheService.getAllDistricts()

        logger.info("==========================")
        logger.info("요청 GPS 좌표: 위도={}, 경도={}", latitude, longitude)

        // 2. 하버사인으로 가까운 3개 추출
        val nearestDistricts = findNearestDistricts(latitude, longitude, allDistricts)

        logger.debug("가까운 3개 행정구역 후보:")
        nearestDistricts.forEachIndexed { index, district ->
            val distance = geometryUtil.haversineDistance(
                latitude, longitude,
                district.centerLatitude, district.centerLongitude
            )
            logger.debug("{}. {} (거리: {} km)", index + 1, district.districtFullNm, String.format("%.2f", distance))
        }

        // 3. Ray Casting으로 정확한 구역 판별
        val exactDistrict = findExactDistrict(latitude, longitude, nearestDistricts)

        if (exactDistrict != null) {
            logger.info("최종 판정된 행정구역: {}", exactDistrict.districtFullNm)
        } else {
            logger.warn("최종 판정된 행정구역: {} (근사치)", nearestDistricts.first().districtFullNm)
        }
        logger.info("==========================")


        // TODO 4. DistrictSearchResponse 변환
//        return toDistrictSearchResponse(exactDistrict, latitude, longitude)

        // 임시 반환 (테스트용)
        val firstDistrict = exactDistrict ?: nearestDistricts.first()
        return DistrictSearchResponse(
            sigCd = firstDistrict.sigCd,
            sidoNm = firstDistrict.sidoNm,
            sigKorNm = firstDistrict.sigKorNm,
            districtFullNm = firstDistrict.districtFullNm,
            centerLatitude = firstDistrict.centerLatitude,
            centerLongitude = firstDistrict.centerLongitude,
            distance = 0.0,
            isExactMatch = exactDistrict != null
        )
    }

    /**
     * 가까운 3개 행정구역 추출
     */
    private fun findNearestDistricts(
        userLatitude: Double,
        userLongitude: Double,
        allDistricts: List<CachedDistrict>
    ): List<CachedDistrict> {
        return allDistricts
            .map { district ->
                val distance = geometryUtil.haversineDistance(
                    userLatitude, userLongitude,
                    district.centerLatitude, district.centerLongitude
                )
                district to distance
            }
            .sortedBy { it.second }
            .take(3)
            .map { it.first }
    }

    /**
     * Ray Casting으로 정확한 행정구역 판별
     * 3개 후보 중 사용자가 실제로 위치한 구역 찾기
     */
    private fun findExactDistrict(
        userLatitude: Double,
        userLongitude: Double,
        nearestDistricts: List<CachedDistrict>
    ): CachedDistrict? {
        for (district in nearestDistricts) { // 3개 후보 순회
            val isInside = geometryUtil.isPointInPolygon(
                userLatitude,
                userLongitude,
                district.coordinates
            )

            if (isInside) {
                return district  // 첫 번째로 찾은 구역 반환
            }
        }

        // 3개 모두 해당 안 되면 null (경계선 밖)
        return null
    }

}
