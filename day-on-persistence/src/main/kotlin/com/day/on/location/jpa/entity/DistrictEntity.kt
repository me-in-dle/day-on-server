package com.day.on.location.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 행정구역 엔티티
 */
@Entity
@Table(
    name = "administrative_districts",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_sig_cd", columnNames = ["sig_cd"])
    ],
    indexes = [
        Index(
            name = "idx_gps_coordinates", 
            columnList = "center_latitude, center_longitude"
        )
    ]
)
@EntityListeners(AuditingEntityListener::class)
class DistrictEntity(
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("행정구역 고유 식별자")
    val id: Long = 0L,

    @Column(name = "sig_cd", nullable = false, length = 10)
    @Comment("행정표준코드(법정동코드) ex. 11440")
    val sigCd: String,

    @Column(name = "sig_kor_nm", nullable = false, length = 50)
    @Comment("행정구역명")
    val sigKorNm: String,

    @Column(name = "geometry_type", nullable = false, length = 20)
    @Comment("GeoJSON Geometry 타입")
    val geometryType: String,

    @Column(name = "coordinates", nullable = false, columnDefinition = "TEXT")
    @Comment("행정구역 경계선 좌표")
    val coordinates: String,
    
    @Column(name = "center_latitude", nullable = false, precision = 10, scale = 8)
    @Comment("행정구역 중심점 위도")
    val centerLatitude: BigDecimal,

    @Column(name = "center_longitude", nullable = false, precision = 11, scale = 8)
    @Comment("행정구역 중심점 경도")
    val centerLongitude: BigDecimal,

    @Column(name = "sido_nm", nullable = false, length = 50)
    @Comment("상위 시도명 ex. 서울특별시")
    val sidoNm: String,

    @Column(name = "district_full_nm", nullable = false, length = 100)
    @Comment("전체 구역명 ex. 서울특별시 마포구")
    val districtFullNm: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    var createdAt: LocalDateTime = LocalDateTime.now()

)
