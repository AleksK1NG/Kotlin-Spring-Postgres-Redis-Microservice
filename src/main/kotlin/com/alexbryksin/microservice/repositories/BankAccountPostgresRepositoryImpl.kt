package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class BankAccountPostgresRepositoryImpl(private val template: R2dbcEntityTemplate, private val databaseClient: DatabaseClient) {

    suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount>? = coroutineScope {
        val query = Query.query(Criteria.where("balance").between(min, max))


        val accountsList = template.select(query.with(pageable), BankAccount::class.java)
            .asFlow()
            .buffer(100)
            .toList()

        val count = template.select(query, BankAccount::class.java).count().awaitFirst()

        PageImpl(accountsList, pageable, count)

//            val bankAccounts = template.databaseClient.sql("select * from microservices.bank_accounts where balance between :min and :max")
//                .bind("min", min)
//                .bind("max", max)
//                .map { row, meta ->
//                    BankAccount(
//                        id = row.get("id", UUID::class.java),
//                        email = row.get("email", String::class.java) ?: "",
//                        phone = row.get("phone", String::class.java) ?: "",
//                        balance = row.get("balance", BigDecimal::class.java) ?: BigDecimal.ZERO,
//                        currency = row.get("currency", Currency::class.java) ?: Currency.USD,
//                        createdAt = row.get("createdAt", LocalDateTime::class.java) ?: LocalDateTime.now(),
//                        updatedAt = row.get("updatedAt", LocalDateTime::class.java) ?: LocalDateTime.now(),
//                    )
//                }
//                .flow()
//                .toList()
    }
}