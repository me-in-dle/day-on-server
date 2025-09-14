package com.day.on.recommend.model

import com.day.on.place.type.PlaceType

data class RecommendPlace(
    val placeId: Long,
    val placeName: String,
    val placeLType: PlaceType,
    val placeSType : String,
    val placeSTypeKoreanName: String,
)