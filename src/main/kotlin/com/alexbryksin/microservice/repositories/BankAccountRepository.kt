package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface BankAccountRepository : CoroutineSortingRepository<BankAccount, UUID>, BankAccountPostgresRepository {
    suspend fun findByEmail(email: String): BankAccount?
}