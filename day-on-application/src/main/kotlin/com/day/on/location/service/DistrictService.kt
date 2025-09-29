package com.day.on.location.service

import com.day.on.location.usecase.dto.CachedDistrict
import com.day.on.location.usecase.dto.DistrictOperationResponse
import com.day.on.location.usecase.dto.DistrictSearchResponse
import com.day.on.location.usecase.inbound.DistrictUseCase
import com.day.on.location.usecase.outbound.DistrictPort
import com.day.on.location.util.GeometryUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 행정구역 UseCase 구현체 (인바운드 어댑터)
 */
@Service
@Transactional(readOnly = true)
class DistrictService(
    private val districtDataPort: DistrictPort,
    private val locationCacheService: LocationCacheService
) : DistrictUseCase {

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

        // 2. 하버사인으로 가까운 3개 추출
        val nearestDistricts = findNearestDistricts(latitude, longitude, allDistricts)

        // 테스트용 로그
        println("=== 가까운 3개 행정구역 ===")
        nearestDistricts.forEachIndexed { index, district ->
            val distance = GeometryUtil().haversineDistance(
                latitude, longitude,
                district.centerLatitude, district.centerLongitude
            )
            println("${index + 1}. ${district.districtFullNm} (거리: ${String.format("%.2f", distance)}km)")
        }
        println("==========================")

        // TODO 3. Ray Casting으로 정확한 구역 판별
//        val exactDistrict = findExactDistrict(latitude, longitude, nearestDistricts)

        // TODO 4. DistrictSearchResponse 변환
//        return toDistrictSearchResponse(exactDistrict, latitude, longitude)

        // 임시 반환 (테스트용)
        val firstDistrict = nearestDistricts.first()
        return DistrictSearchResponse(
            sigCd = firstDistrict.sigCd,
            sidoNm = firstDistrict.sidoNm,
            sigKorNm = firstDistrict.sigKorNm,
            districtFullNm = firstDistrict.districtFullNm,
            centerLatitude = firstDistrict.centerLatitude,
            centerLongitude = firstDistrict.centerLongitude,
            distance = 0.0,
            isExactMatch = false
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
        val geometryUtil = GeometryUtil()

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

}
