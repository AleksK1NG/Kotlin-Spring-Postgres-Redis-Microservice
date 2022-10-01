package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
interface BankAccountPostgresRepository {
    suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount>
}