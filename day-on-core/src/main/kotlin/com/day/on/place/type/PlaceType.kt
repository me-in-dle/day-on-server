package com.day.on.place.type

enum class PlaceType {
    // 음식/맛집
    RESTAURANT,          // 일반 맛집
    CAFE,                // 카페
    BAKERY,              // 빵집
    BAR,                 // 술집/펍
    STREET_FOOD,         // 길거리 음식

    // 자연/야외
    PARK,                // 공원/산책
    MOUNTAIN,            // 등산/산
    BEACH,               // 해변
    RIVER,               // 강가/호수
    TRAIL,               // 트레일/둘레길

    // 문화/예술
    MUSEUM,              // 박물관
    ART_GALLERY,         // 미술관
    THEATER,             // 공연장/연극
    CINEMA,              // 영화관
    LIBRARY,             // 도서관/북카페

    // 쇼핑/여가
    SHOPPING_MALL,       // 쇼핑몰
    TRADITIONAL_MARKET,  // 전통시장
    THEME_PARK,          // 놀이공원
    AQUARIUM,            // 아쿠아리움
    ZOO,                 // 동물원

    // 스포츠/액티비티
    SPORTS_COMPLEX,      // 종합체육관
    GYM,                 // 헬스장
    CLIMBING,            // 실내 클라이밍
    BOWLING,             // 볼링장
    KARAOKE,             // 노래방

    // 여행/휴식
    HOTEL,               // 호텔
    SPA,                 // 스파/찜질방
    RESORT,              // 리조트
    CAMPING,             // 캠핑장
    HANOK_STAY,          // 한옥스테이

    // 기타 경험형
    WORKSHOP,            // 공방/원데이클래스
    EXHIBITION,          // 전시회
    ;

    companion object {
        fun randomTypes(count: Int): List<PlaceType> {
            return entries.shuffled().take(count)
        }
    }
}
