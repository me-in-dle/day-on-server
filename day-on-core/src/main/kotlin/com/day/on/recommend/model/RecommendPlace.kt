package com.day.on.recommend.model

import com.day.on.recommend.type.RecommendPlaceType

data class RecommendPlace(
    val placeId: Long,
    val placeName: String,
    val placeLType: RecommendPlaceType,
    val placeSType : String,
    val placeSTypeKoreanName: String,
)