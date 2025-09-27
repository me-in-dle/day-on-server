package com.day.on.location.adapter

import com.day.on.location.exception.LocationErrorCode
import com.day.on.location.exception.LocationException
import com.day.on.location.usecase.dto.DistrictCreateRequest
import com.day.on.location.jpa.repository.DistrictRepository
import com.day.on.location.usecase.outbound.DistrictPort
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 행정구역  Port 구현체 (아웃바운드 어댑터)
 */
@Component
@Transactional
class DistrictAdapter(
    private val districtRepository: DistrictRepository,
    private val objectMapper: ObjectMapper
) : DistrictPort {
    
    private val logger = LoggerFactory.getLogger(DistrictAdapter::class.java)
    
    override fun savePreprocessedDistricts(jsonFilePath: String): Int {
        logger.info("=== 행정구역 데이터 저장 시작 ===")
        
        // 기존 데이터 확인
        val existingCount = districtRepository.count()
        if (existingCount > 0) {
            logger.info("기존 데이터 {}개 발견. 재수집을 원하면 deleteAllDistricts() 호출 후 재실행하세요.", existingCount)
            throw LocationException(LocationErrorCode.DISTRICT_DATA_ALREADY_EXISTS)
        }
        
        logger.info("요청된 파일 경로: {}", jsonFilePath)
        
        // JSON 파일 읽기
        val jsonContent = try {
            val file = java.io.File(jsonFilePath)
            logger.info("파일 절대경로: {}", file.absolutePath)
            logger.info("파일 존재여부: {}", file.exists())
            logger.info("파일 읽기권한: {}", file.canRead())
            
            file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            logger.error("JSON 파일 읽기 실패 - 경로: {}, 에러: {}", jsonFilePath, e.message, e)
            throw LocationException(LocationErrorCode.DISTRICT_JSON_FILE_NOT_FOUND)
        }
        
        // JSON 파싱
        val jsonArray = objectMapper.readTree(jsonContent)
        logger.info("원본 JSON 데이터 개수: {}개", jsonArray.size())
        
        // JSON → Dto 변환 및 유효성 검증
        val validDtoList = jsonArray.mapNotNull { jsonNode ->
            try {
                val dto = parseJsonToDto(jsonNode)
                dto.validate()  // 유효성 검증
                dto // 반환값
            } catch (e: Exception) {
                logger.warn(
                    "Dto 변환 실패 [행정표준코드: {}, 구역명: {}] - 이유: {}",
                    jsonNode.get("sigCd")?.asText() ?: "Unknown",
                    jsonNode.get("districtFullNm")?.asText() ?: "Unknown",
                    e.message
                )
                null // 반환값
            }
        }

        logger.info("JSON → Dto 변환 성공 건수: {}", validDtoList.size)
        
        // Dto → Entity 변환
        val result = validDtoList.map { dto ->
            com.day.on.location.jpa.entity.DistrictEntity(
                sigCd = dto.sigCd,
                sigKorNm = dto.sigKorNm,
                geometryType = dto.geometryType,
                coordinates = dto.coordinates,
                centerLatitude = dto.centerLatitudeBigDecimal,
                centerLongitude = dto.centerLongitudeBigDecimal,
                sidoNm = dto.sidoNm,
                districtFullNm = dto.districtFullNm
            )
        }
        logger.info("Dto → Entity 변환 결과 (성공/전체): {}개/{}개", validDtoList.size, jsonArray.size())
        
        // 일괄 저장
        val savedEntities = districtRepository.saveAll(result)
        logger.info("=== 행정구역 데이터 저장 완료: {}개 구역 저장 ===", savedEntities.size)
        
        return savedEntities.size
    }
    
    override fun deleteAllDistricts() {
        logger.warn("모든 행정구역 데이터 삭제")
        districtRepository.truncateTable()
        logger.warn("삭제 완료")
    }
    
    /**
     * JSON 노드를 DTO로 변환
     */
    private fun parseJsonToDto(jsonNode: JsonNode): DistrictCreateRequest {
        return DistrictCreateRequest(
            sigCd = jsonNode.get("sigCd").asText(),
            sigKorNm = jsonNode.get("sigKorNm").asText(),
            geometryType = jsonNode.get("geometryType").asText(),
            coordinates = jsonNode.get("coordinates").asText(),
            centerLatitude = jsonNode.get("centerLatitude").asDouble(),
            centerLongitude = jsonNode.get("centerLongitude").asDouble(),
            sidoNm = jsonNode.get("sidoNm").asText(),
            districtFullNm = jsonNode.get("districtFullNm").asText()
        )
    }
}
