package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import org.springframework.stereotype.Repository


@Repository
interface BankAccountCacheRepository {
    suspend fun setBankAccountById(id: String, bankAccount: BankAccount)

    suspend fun getBankAccountById(id: String): BankAccount?
}