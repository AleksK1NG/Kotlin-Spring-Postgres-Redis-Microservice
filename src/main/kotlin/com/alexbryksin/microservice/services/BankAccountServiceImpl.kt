package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.domain.fromCreateRequest
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.repositories.BankAccountRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.util.*


@Service
class BankAccountServiceImpl(private val bankAccountRepository: BankAccountRepository) : BankAccountService {
    override suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount = coroutineScope {
        val bankAccount = BankAccount.fromCreateRequest(createBankAccountRequest)
        val savedBankAccount = bankAccountRepository.save(bankAccount)
        savedBankAccount
    }

    override suspend fun getBankAccountById(id: UUID): BankAccount = coroutineScope{
        bankAccountRepository.findById(id) ?: throw RuntimeException("bank account with id: $id not found")
    }
}