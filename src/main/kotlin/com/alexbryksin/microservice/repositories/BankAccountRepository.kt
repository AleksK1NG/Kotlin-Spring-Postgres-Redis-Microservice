package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import java.math.BigDecimal
import java.util.*

interface BankAccountRepository : CoroutineSortingRepository<BankAccount, UUID> {

    suspend fun findByEmail(email: String): BankAccount?

    @Modifying
    @Query(
        "INSERT INTO microservices.bank_accounts (bank_account_id, email, phone, balance, currency, created_at, updated_at) VALUES (bank_account_id = \$1, email = \$2, phone = \$3, balance = \$4, currency = \$5, created_at = now(), updated_at = now())"
    )
    suspend fun insert(
        bankAccountId: UUID,
        email: String,
        phone: String,
        balance: BigDecimal,
        currency: String
    )
}