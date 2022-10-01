package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
interface BankAccountService {

    suspend fun depositAmount(id: UUID, depositAmountRequest: DepositAmountRequest): BankAccount

    suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount

    suspend fun getBankAccountByEmail(email: String): BankAccount

    suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount>
}