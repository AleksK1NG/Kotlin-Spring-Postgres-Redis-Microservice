package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.domain.fromCreateRequest
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.exceptions.BankAccountNotFoundException
import com.alexbryksin.microservice.repositories.BankAccountCacheRepository
import com.alexbryksin.microservice.repositories.BankAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class BankAccountServiceImpl(
    private val bankAccountRepository: BankAccountRepository,
    private val bankAccountCacheRepository: BankAccountCacheRepository
) : BankAccountService {

    @Transactional
    override suspend fun depositAmount(id: UUID, depositAmountRequest: DepositAmountRequest) =
        withContext(Dispatchers.IO) {
            val bankAccount = bankAccountRepository.findById(id)
                ?: throw BankAccountNotFoundException("bank account with id: $id not found")
            bankAccount.depositAmount(depositAmountRequest.amount)
            bankAccountRepository.save(bankAccount)
                .also { bankAccountCacheRepository.setBankAccountByKey(id.toString(), it) }
        }

    override suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount =
        withContext(Dispatchers.IO) {
            val bankAccount = BankAccount.fromCreateRequest(createBankAccountRequest)
            val savedBankAccount = bankAccountRepository.save(bankAccount)
            savedBankAccount
        }

    override suspend fun getBankAccountById(id: UUID): BankAccount = withContext(Dispatchers.IO) {
        val cachedBankAccount = bankAccountCacheRepository.getBankAccountByKey(id.toString())
        if (cachedBankAccount != null) return@withContext cachedBankAccount
        val bankAccount = bankAccountRepository.findById(id)
            ?: throw BankAccountNotFoundException("bank account with id: $id not found")

        bankAccountCacheRepository.setBankAccountByKey(id.toString(), bankAccount)
        bankAccount
    }

    override suspend fun getBankAccountByEmail(email: String): BankAccount = withContext(Dispatchers.IO) {
        val cachedBankAccount = bankAccountCacheRepository.getBankAccountByKey(email)
        if (cachedBankAccount != null) return@withContext cachedBankAccount

        val bankAccount = bankAccountRepository.findByEmail(email)
            ?: throw BankAccountNotFoundException("bank account with email: $email not found")
        bankAccountCacheRepository.setBankAccountByKey(email, bankAccount)
        bankAccount
    }
}