package com.day.on.location.util

import kotlin.math.*

/**
 * 지리적 계산 유틸리티
 */
class GeometryUtil {

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

}