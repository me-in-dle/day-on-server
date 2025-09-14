package com.day.on.recommend.jpa.entity.embedded

import com.day.on.recommend.model.RecommendPlace
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class RecommendPlaceEmbeddedJpaModel(
    @Column(name = "place_id", columnDefinition = "VARCHAR(100)", nullable = true)
    val placeId: Long,
    @Column(name = "place_name", columnDefinition = "VARCHAR(100)", nullable = true)
    val placeName: String,
    @Column(name = "place_s_type", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeSType: String,
    @Column(name = "place_s_type_korean_name", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeSTypeKoreanName: String,
) {
    fun toDomainModel() = RecommendPlace(
        placeId = placeId,
        placeName = placeName,
        placeSType = placeSType,
        placeSTypeKoreanName = placeSTypeKoreanName
    )

    companion object {
        fun from(recommendPlace: RecommendPlace) = with(recommendPlace) {
            RecommendPlaceEmbeddedJpaModel(
                placeId = placeId,
                placeName = placeName,
                placeSType = placeSType,
                placeSTypeKoreanName = placeSTypeKoreanName
            )
        }
    }
}