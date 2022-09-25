package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.domain.fromCreateRequest
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.repositories.BankAccountCacheRepository
import com.alexbryksin.microservice.repositories.BankAccountRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.util.*


@Service
class BankAccountServiceImpl(
    private val bankAccountRepository: BankAccountRepository,
    private val bankAccountCacheRepository: BankAccountCacheRepository
) : BankAccountService {

    override suspend fun depositAmount(id: UUID, depositAmountRequest: DepositAmountRequest) = coroutineScope {
        val bankAccount = bankAccountRepository.findById(id) ?: throw RuntimeException("bank account with id: $id not found")
        bankAccount.depositAmount(depositAmountRequest.amount)
        bankAccountRepository.save(bankAccount)
            .also { bankAccountCacheRepository.setBankAccount(id.toString(), it) }
    }

    override suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount = coroutineScope {
        val bankAccount = BankAccount.fromCreateRequest(createBankAccountRequest)
        val savedBankAccount = bankAccountRepository.save(bankAccount)
        savedBankAccount
    }

    override suspend fun getBankAccountById(id: UUID): BankAccount = coroutineScope {
        val cachedBankAccount = bankAccountCacheRepository.getBankAccount(id.toString())
        if (cachedBankAccount != null) return@coroutineScope cachedBankAccount
        val bankAccount = bankAccountRepository.findById(id) ?: throw RuntimeException("bank account with id: $id not found")

        bankAccountCacheRepository.setBankAccount(id.toString(), bankAccount)
        bankAccount
    }
}