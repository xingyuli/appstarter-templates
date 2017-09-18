package com.mobisist.springbootseed.appstarter.infra.service

import org.jooq.*
import org.jooq.impl.DSL

// pagination

data class Pageable(val page: Int, val size: Int, val sort: Sort? = null) {
    val offset: Int = page * size
}

data class Page<T>(val page: Int, val size: Int, val total: Long, val rows: List<T>) {
    constructor(pageable: Pageable, total: Long, rows: List<T>) : this(pageable.page, pageable.size, total, rows)
}

fun <R, E> Page<R>.map(mapper: (R) -> E): Page<E> = Page(page, size, total, rows.map { mapper(it) })

fun <R : Record> SelectLimitStep<R>.limit(pageable: Pageable): SelectForUpdateStep<R> = limit(pageable.size).offset(pageable.offset)

fun <R : Record> DSLContext.fetchPage(select: SelectLimitStep<R>, pageable: Pageable): Page<R> {
    val total = fetchCount(select.query)
    val rows = select.limit(pageable).fetch()
    return Page(pageable, total.toLong(), rows)
}

fun <R : Record, E> DSLContext.fetchPageInto(select: SelectLimitStep<R>, pageable: Pageable, type: Class<E>): Page<E> {
    val total = fetchCount(select.query)
    val rows = select.limit(pageable).fetchInto(type)
    return Page(pageable, total.toLong(), rows)
}


// sorting

data class Sort(val orders: List<Order>) {

    constructor(vararg orders: SortField<*>) : this(orders.map(::Order))

    constructor(vararg orders: Order) : this(orders.toList())

    constructor(vararg fields: String) : this(fields.map { Order(it) })

    constructor(direction: Direction, vararg fields: String) : this(fields.map { Order(it, direction) })

    constructor(direction: Direction, fields: List<String>) : this(fields.map { Order(it, direction) })

    data class Order(val field: SortField<*>) {
        constructor(field: String, direction: Direction = DEFAULT_DIRECTION) : this(DSL.field(field).direction(direction))
    }

    enum class Direction {
        ASC, DESC
    }

    companion object {
        @JvmStatic
        val DEFAULT_DIRECTION = Direction.ASC
    }

}

fun Pageable.with(sort: Sort): Pageable = copy(sort = sort)

fun <T> Field<T>.direction(direction: Sort.Direction): SortField<T> = when(direction) {
    Sort.Direction.ASC -> asc()
    Sort.Direction.DESC -> desc()
}

fun <R : Record> SelectOrderByStep<R>.orderBy(sort: Sort): SelectSeekStepN<R> = orderBy(sort.orders.map { it.field })

fun <R : Record> DSLContext.fetchPage(select: SelectOrderByStep<R>, pageable: Pageable): Page<R> =
        fetchPage(processOrderBy(select, pageable), pageable)

fun <R : Record, E> DSLContext.fetchPageInto(select: SelectOrderByStep<R>, pageable: Pageable, type: Class<E>): Page<E> =
        fetchPageInto(processOrderBy(select, pageable), pageable, type)

private fun <R : Record> processOrderBy(select: SelectOrderByStep<R>, pageable: Pageable): SelectLimitStep<R> =
        if (pageable.sort != null && pageable.sort.orders.isNotEmpty()) select.orderBy(pageable.sort) else select


// condition
operator fun Condition.plus(other: Condition): Condition = and(other)
