package com.day.on.location.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import kotlin.math.*

/**
 * 지리적 계산 유틸리티
 */
@Component
class GeometryUtil(
    private val objectMapper: ObjectMapper
) {

    private val EARTH_RADIUS_KM = 6371.0

    /**
     * 하버사인 공식으로 두 지점 간의 실제 거리(km) 계산
     */
    fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Ray Casting 알고리즘으로 점이 다각형 내부에 있는지 판별
     *
     * @param pointLat 점의 위도
     * @param pointLon 점의 경도
     * @param polygonCoordinates GeoJSON Polygon 좌표 (JSON 문자열)
     * @return 점이 다각형 내부에 있으면 true
     */
    fun isPointInPolygon(
        pointLat: Double,
        pointLon: Double,
        polygonCoordinates: String
    ): Boolean {
        val coordinates = parseCoordinates(polygonCoordinates)

        var inside = false
        var j = coordinates.size - 1

        for (i in coordinates.indices) {
            val xi = coordinates[i][0]  // 경도
            val yi = coordinates[i][1]  // 위도
            val xj = coordinates[j][0]
            val yj = coordinates[j][1]

            val intersect = ((yi > pointLat) != (yj > pointLat)) &&
                    (pointLon < (xj - xi) * (pointLat - yi) / (yj - yi) + xi)

            if (intersect) inside = !inside
            j = i
        }

        return inside
    }

    /**
     * GeoJSON 문자열을 좌표 배열로 파싱
     * [[[[lon, lat], ...]]] → [[lon, lat], ...]
     */
    private fun parseCoordinates(json: String): List<List<Double>> {
        val root = objectMapper.readTree(json)

        // [0][0]으로 실제 좌표 배열 추출
        val ring = root[0][0]

        val result = mutableListOf<List<Double>>()
        ring.forEach { point ->
            result.add(listOf(point[0].asDouble(), point[1].asDouble()))
        }

        return result
    }

}