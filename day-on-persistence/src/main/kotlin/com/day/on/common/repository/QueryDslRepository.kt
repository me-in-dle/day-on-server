package com.day.on.common.repository

import com.day.on.common.repository.pagination.PageResult
import com.day.on.common.repository.pagination.Pagination

interface QueryDslRepository<T, R, S> {
    fun queryPagination(type: Class<T>, sortType: Class<R>, pagination: Pagination): PageResult<T>
}