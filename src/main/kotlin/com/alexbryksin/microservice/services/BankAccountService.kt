package com.alexbryksin.microservice.services

import com.alexbryksin.microservice.domain.BankAccount
import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import org.springframework.stereotype.Service
import java.util.*


@Service
interface BankAccountService {

    suspend fun createBankAccount(createBankAccountRequest: CreateBankAccountRequest): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount
}