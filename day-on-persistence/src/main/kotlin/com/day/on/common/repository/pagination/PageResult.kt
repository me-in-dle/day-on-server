package com.day.on.common.repository.pagination

data class PageResult<T>(
    val contents: List<T>,
    val isTotalCountEstimated: Boolean = false,
    val totalPageResult: TotalPageResult?,
    val currentPage: Int,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    data class TotalPageResult(
        val totalElements: Long,
        val totalPages: Int,
    )
}
