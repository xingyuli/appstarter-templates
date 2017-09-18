package com.mobisist.springbootseed.appstarter.infra.web.plugin

import com.mobisist.springbootseed.appstarter.infra.service.BaseDbService
import com.mobisist.springbootseed.appstarter.infra.service.Page
import com.mobisist.springbootseed.appstarter.infra.service.Pageable
import org.jooq.Condition

// pagination

data class DataTablePageable(var draw: Int = 0, var start: Int = 0, var length: Int = 10) {
    fun toPageable(): Pageable = Pageable(start / length, if (length == -1) Int.MAX_VALUE else length)
}

data class DataTablePage<out T>(val draw: Int, val recordsTotal: Long, val recordsFiltered: Long, val data: List<T>)

fun <T, E> DataTablePage<T>.map(mapper: (T) -> E): DataTablePage<E> = DataTablePage(draw, recordsTotal, recordsFiltered, data.map { mapper(it) })

private fun <T> Page<T>.toDataTablePage(draw: Int): DataTablePage<T> = DataTablePage(draw, total, total, rows)

fun <P> BaseDbService<*, P, *, *>.findAll(dataTablePageable: DataTablePageable): DataTablePage<P> = dataTablePageable.find { findAll(it) }

fun <T> DataTablePageable.find(finder: (Pageable) -> Page<T>): DataTablePage<T> = finder(toPageable()).toDataTablePage(draw)

// conditions

class PropertyFilter(var data: String = "", var name: String = "", var search: SearchCondition? = null)

class SearchCondition(var value: String = "", var regex: Boolean = false)

fun List<PropertyFilter>.buildConditions(vararg transforms: ConditionTransform): List<Condition> {
    return transforms.mapNotNull { (data, test, block) ->
        firstOrNull { it.data == data }?.search?.value?.takeIf { test(it) }?.let(block)
    }
}

data class ConditionTransform(val data: String,
                              val test: ((String) -> Boolean) = { !it.isBlank() },
                              val block: (String) -> Condition)