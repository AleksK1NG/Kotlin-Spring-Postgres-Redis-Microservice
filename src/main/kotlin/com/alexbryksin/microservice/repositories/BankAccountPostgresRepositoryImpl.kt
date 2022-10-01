package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class BankAccountPostgresRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val tracer: Tracer
) : BankAccountPostgresRepository {

    override suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount> = coroutineScope {
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountPostgresRepositoryImpl.findByBalanceAmount")
            val query = Query.query(Criteria.where("balance").between(min, max))

            try {
                val accountsList = async {
                    template.select(query.with(pageable), BankAccount::class.java)
                        .asFlow()
                        .buffer(100)
                        .toList()
                }

                val totalCount = async { template.select(query, BankAccount::class.java).count().awaitFirst() }

                PageImpl(accountsList.await(), pageable, totalCount.await()).also { spanTagFindByBalanceAmount(span, it) }
            } finally {
                span.end()
            }
        }
    }

    private fun spanTagFindByBalanceAmount(span: Span, data: PageImpl<BankAccount>) {
        span.tag("accountsList", data.content.size.toString())
            .tag("totalCount", data.totalElements.toString())
            .tag("pagination", data.pageable.toString())
    }
}