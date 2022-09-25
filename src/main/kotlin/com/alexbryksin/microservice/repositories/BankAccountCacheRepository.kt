package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import org.springframework.stereotype.Repository


@Repository
interface BankAccountCacheRepository {
    suspend fun setBankAccount(id: String, bankAccount: BankAccount)

    suspend fun getBankAccount(id: String): BankAccount?
}