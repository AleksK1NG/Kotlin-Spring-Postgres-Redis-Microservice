package com.alexbryksin.microservice.domain

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Table(schema = "microservices", name = "bank_accounts")
data class BankAccount(
    @Id @Column("bank_account_id") val id: UUID?,
    @Column("email") val email: String,
    @Column("phone") val phone: String,
    @Column("balance") val balance: BigDecimal,
    @Column("currency") val currency: String,
    @Column("created_at") val createdAt: LocalDateTime,
    @Column("updated_at") val updatedAt: LocalDateTime,
) {
    companion object { }
}

fun BankAccount.Companion.fromCreateRequest(createBankAccountRequest: CreateBankAccountRequest): BankAccount {
    return BankAccount(
        id = null,
        email = createBankAccountRequest.email,
        phone = createBankAccountRequest.phone,
        balance = createBankAccountRequest.balance,
        currency = createBankAccountRequest.currency,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )
}