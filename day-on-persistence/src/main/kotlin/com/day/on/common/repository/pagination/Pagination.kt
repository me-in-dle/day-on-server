package com.day.on.common.repository.pagination

import org.hibernate.query.SortDirection

data class Pagination(
    val page: Int,
    val size: Int,
    val sortDirection: SortDirection,
) {
    val offset: Int get() = page * size
}
