package com.day.on.common.repository

import com.day.on.common.repository.pagination.PageResult
import com.day.on.common.repository.pagination.Pagination
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQuery
import org.hibernate.query.SortDirection

abstract class AbstractQueryDslRepositoryImpl<T, R, S> : QueryDslRepository<T, R, S> {

    abstract fun createBaseQuery(type: Class<T>): JPAQuery<T>

    abstract fun createTotalCountQuery(type: Class<T>): Long

    abstract fun createOrderSpecifier(sortType: Class<R>, direction: SortDirection): OrderSpecifier<*>

    override fun queryPagination(type: Class<T>, sortType: Class<R>, pagination: Pagination): PageResult<T> {
        val totalCount = createTotalCountQuery(type)
        val contents = createBaseQuery(type)
            .offset(pagination.offset.toLong())
            .limit(pagination.size.toLong())
            .orderBy(createOrderSpecifier(sortType, pagination.sortDirection))
            .fetch()

        val totalPages = ((totalCount + pagination.size - 1) / pagination.size).toInt()
        val currentPage = pagination.offset / pagination.size

        return PageResult(
            contents = contents,
            isTotalCountEstimated = true,
            totalPageResult = PageResult.TotalPageResult(
                totalElements = totalCount,
                totalPages = totalPages
            ),
            currentPage = currentPage,
            size = pagination.size,
            hasNext = currentPage < totalPages - 1,
            hasPrevious = currentPage > 0
        )
    }
}