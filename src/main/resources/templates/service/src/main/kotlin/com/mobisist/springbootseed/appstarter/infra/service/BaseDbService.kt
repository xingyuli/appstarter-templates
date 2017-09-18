package com.mobisist.springbootseed.appstarter.infra.service

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
abstract class BaseDbService<R : UpdatableRecord<R>, P, ID, D : DAO<R, P, ID>> {

    @Autowired
    protected lateinit var dao: D

    // use DSLContext to generate dynamic sql
    @Autowired
    protected lateinit var create: DSLContext

    @Transactional(readOnly = false)
    open fun save(`object`: P): P {
        val record = create.newRecord(dao.table, `object`)
        record.store()
        return record.into(dao.type)
    }

    @Transactional
    open fun update(`object`: P) = dao.update(`object`)

    @Transactional
    open fun update(objects: Collection<P>) = dao.update(objects)

    @Transactional(readOnly = false)
    open fun save(objects: Collection<P>): Collection<P> {
        val records = objects.map { create.newRecord(dao.table, it) }
        create.batchStore(records).execute()
        return records.map { it.into(dao.type) }
    }

    open fun findOne(id: ID): P? = dao.findById(id)

    open fun exists(id: ID): Boolean = dao.existsById(id)

    open fun findAll(): List<P> = dao.findAll()

    open fun count(): Long = dao.count()

    @Transactional(readOnly = false)
    open fun delete(id: ID): Unit = dao.deleteById(id)

    @Transactional(readOnly = false)
    open fun delete(ids: Collection<ID>): Unit = dao.deleteById(ids)

    @Transactional(readOnly = false)
    open fun deleteAll() {
        create.deleteFrom(dao.table).execute()
    }

    open fun findAll(vararg sort: SortField<Any>): List<P> {
        return create.selectFrom(dao.table).orderBy(*sort).fetchInto(dao.type)
    }

    open fun findAll(pageable: Pageable): Page<P> {
        return create.fetchPageInto(create.selectFrom(dao.table), pageable, dao.type)
    }

}