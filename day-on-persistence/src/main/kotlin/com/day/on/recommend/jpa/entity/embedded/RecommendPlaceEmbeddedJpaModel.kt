package com.day.on.recommend.jpa.entity.embedded

import com.day.on.recommend.model.RecommendPlace
import com.day.on.place.type.PlaceType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class RecommendPlaceEmbeddedJpaModel(
    @Column(name = "place_id", columnDefinition = "VARCHAR(100)", nullable = true)
    val placeId: Long,
    @Column(name = "place_name", columnDefinition = "VARCHAR(100)", nullable = true)
    val placeName: String,
    @Enumerated(value = EnumType.STRING)
    @Column(name = "place_l_type", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeLType: PlaceType,
    @Column(name = "place_s_type", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeSType: String,
    @Column(name = "place_s_type_korean_name", columnDefinition = "VARCHAR(255)", nullable = true)
    val placeSTypeKoreanName: String,
) {
    fun toDomainModel() = RecommendPlace(
        placeId = placeId,
        placeName = placeName,
        placeLType = placeLType,
        placeSType = placeSType,
        placeSTypeKoreanName = placeSTypeKoreanName
    )

    companion object {
        fun from(recommendPlace: RecommendPlace) = with(recommendPlace) {
            RecommendPlaceEmbeddedJpaModel(
                placeId = placeId,
                placeName = placeName,
                placeLType = placeLType,
                placeSType = placeSType,
                placeSTypeKoreanName = placeSTypeKoreanName
            )
        }
    }
}