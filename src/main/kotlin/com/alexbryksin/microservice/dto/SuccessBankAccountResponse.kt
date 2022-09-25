package com.alexbryksin.microservice.dto

import com.alexbryksin.microservice.domain.BankAccount
import java.math.BigDecimal
import java.util.*

data class SuccessBankAccountResponse(
    val id: UUID?,
    val email: String,
    val phone: String,
    val balance: BigDecimal,
    val currency: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {}
}

fun SuccessBankAccountResponse.Companion.of(bankAccount: BankAccount): SuccessBankAccountResponse {
    return SuccessBankAccountResponse(
        id = bankAccount.id,
        email = bankAccount.email,
        phone = bankAccount.phone,
        balance = bankAccount.balance,
        currency = bankAccount.currency,
        createdAt = bankAccount.createdAt.toString(),
        updatedAt = bankAccount.updatedAt.toString(),
    )
}
