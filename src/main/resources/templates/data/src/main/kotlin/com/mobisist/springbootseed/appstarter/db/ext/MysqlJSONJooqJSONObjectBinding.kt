package com.mobisist.springbootseed.appstarter.db.ext

import com.mobisist.springbootseed.appstarter.common.jsonFrom
import com.mobisist.springbootseed.appstarter.common.jsonStringify
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.tools.json.JSONArray
import org.jooq.tools.json.JSONObject
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types

class MysqlJSONJooqJSONObjectBinding : Binding<Any, JSONObject> {

    // The converter does all the work
    override fun converter(): Converter<Any, JSONObject> = MysqlJSONJooqJSONObjectConverter()

    override fun register(ctx: BindingRegisterContext<JSONObject>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun sql(ctx: BindingSQLContext<JSONObject>) {
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value()))
    }

    override fun set(ctx: BindingSetStatementContext<JSONObject>) {
        ctx.statement().setString(ctx.index(), ctx.convert(converter()).value()?.toString())
    }

    override fun get(ctx: BindingGetResultSetContext<JSONObject>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetStatementContext<JSONObject>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    override fun set(ctx: BindingSetSQLOutputContext<JSONObject>) {
        throw SQLFeatureNotSupportedException()
    }

    override fun get(ctx: BindingGetSQLInputContext<JSONObject>) {
        throw SQLFeatureNotSupportedException()
    }

}

fun List<*>.toJSONObject(key: String = "rows"): JSONObject {
    val obj = JSONObject()
    obj[key] = this
    return obj
}

/**
 * Reverse operation of [toJSONObject].
 */
fun JSONObject.extractJsonArray(key: String = "rows"): JSONArray? {
    return if (containsKey(key)) JSONArray::class.jsonFrom(this[key].jsonStringify()) else null
}

fun JSONObject.replaceValueOf(key: String, replacement: (Any?) -> Any?) {
    put(key, replacement(get(key)))
}
