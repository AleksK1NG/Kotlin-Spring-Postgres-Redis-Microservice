package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.domain.fromCreateRequest
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.exceptions.BankAccountNotFoundException
import com.alexbryksin.microservice.repositories.BankAccountRepository
import com.alexbryksin.microservice.repositories.RedisCacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*


@Service
class BankAccountServiceImpl(
    private val bankAccountRepository: BankAccountRepository,
    private val redisCacheRepository: RedisCacheRepository,
    private val tracer: Tracer
) : BankAccountService {

    @Transactional
    override suspend fun depositAmount(id: UUID, depositAmountRequest: DepositAmountRequest) =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountServiceImpl.depositAmount")

            try {
                val bankAccount = bankAccountRepository.findById(id) ?: throw BankAccountNotFoundException(id.toString()).also { span.error(it) }

                bankAccount.depositAmount(depositAmountRequest.amount)
                bankAccountRepository.save(bankAccount).also {
                    redisCacheRepository.setKey(id.toString(), it)
                    span.tag("bankAccount", it.toString())
                }
            } finally {
                span.end()
            }
        }

    override suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountServiceImpl.createBankAccount")

            try {
                bankAccountRepository.save(BankAccount.fromCreateRequest(createBankAccountRequest))
                    .also { span.tag("bankAccount", it.toString()) }
            } finally {
                span.end()
            }
        }

    override suspend fun getBankAccountById(id: UUID): BankAccount = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountServiceImpl.getBankAccountById")

        try {
            val cachedBankAccount = redisCacheRepository.getKey(id.toString(), BankAccount::class.java)
            if (cachedBankAccount != null) return@withContext cachedBankAccount

            val bankAccount = bankAccountRepository.findById(id) ?: throw BankAccountNotFoundException(id.toString())

            redisCacheRepository.setKey(id.toString(), bankAccount)
            bankAccount.also { span.tag("bankAccount", it.toString()) }
        } finally {
            span.end()
        }

    }

    override suspend fun getBankAccountByEmail(email: String): BankAccount = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountServiceImpl.getBankAccountByEmail")

        try {
            val cachedBankAccount = redisCacheRepository.getKey(email, BankAccount::class.java)
            if (cachedBankAccount != null) return@withContext cachedBankAccount

            val bankAccount = bankAccountRepository.findByEmail(email)
                ?: throw BankAccountNotFoundException("bank account with email: $email not found").also { span.error(it) }
            redisCacheRepository.setKey(email, bankAccount)
            bankAccount.also { span.tag("bankAccount", it.toString()) }
        } finally {
            span.end()
        }
    }

    override suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount> =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountServiceImpl.findByBalanceAmount")

            try {
                bankAccountRepository.findByBalanceAmount(min, max, pageable)
            } finally {
                span.end()
            }
        }
}