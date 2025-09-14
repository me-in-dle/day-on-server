package com.day.on.place.model

import com.day.on.account.type.System
import com.day.on.place.type.PlaceType
import java.time.LocalDateTime

/* 임시로 생성, 네이버 가게 정보 연동 후 데이터 수정 */
data class Place(
    val id: Long,
    val placeName: String,
    val placeType: PlaceType,
    val placeLink: String?,
    val description: String?,
    val address: String?,
    val roadAddress: String?,
    val latitude: String?,
    val longitude: String?,
    val createdId: String = System.SYSTEM_ID.id,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedId: String = System.SYSTEM_ID.id,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)