package com.day.on.location.service

import com.day.on.location.usecase.dto.DistrictOperationResponse
import com.day.on.location.usecase.dto.DistrictSearchResponse
import com.day.on.location.usecase.inbound.DistrictUseCase
import com.day.on.location.usecase.outbound.DistrictPort
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

        // TODO 2. 하버사인으로 가까운 3개 추출
//        val nearestDistricts = findNearestDistricts(latitude, longitude, allDistricts)

        // TODO 3. Ray Casting으로 정확한 구역 판별
//        val exactDistrict = findExactDistrict(latitude, longitude, nearestDistricts)

        // TODO 4. DistrictSearchResponse 변환
//        return toDistrictSearchResponse(exactDistrict, latitude, longitude)

        // 임시 반환 (테스트용)
        val firstDistrict = allDistricts.first()
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

}
