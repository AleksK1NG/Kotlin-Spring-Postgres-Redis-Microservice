package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import org.springframework.stereotype.Service
import java.util.*


@Service
interface BankAccountService {

    suspend fun depositAmount(id: UUID, depositAmountRequest: DepositAmountRequest): BankAccount

    suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount

    suspend fun getBankAccountByEmail(email: String): BankAccount
}