package com.alexbryksin.microservice.domain

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.exceptions.InvalidAmountException
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Table(schema = "microservices", name = "bank_accounts")
data class BankAccount(
    @Id @Column("bank_account_id") val id: UUID?,
    @Column("email") var email: String,
    @Column("phone") var phone: String,
    @Column("balance") var balance: BigDecimal,
    @Column("currency") var currency: String,
    @Column("created_at") var createdAt: LocalDateTime,
    @Column("updated_at") var updatedAt: LocalDateTime,
) : Serializable {

    fun depositAmount(amount: BigDecimal) {
        if (amount.compareTo(BigDecimal.ZERO) == -1) throw InvalidAmountException("invalid amount: $amount")
        balance = balance.add(amount)
        updatedAt = LocalDateTime.now()
    }

    fun withdrawAmount(amount: BigDecimal) {
        val expectedBalance = balance.minus(amount)
        if (expectedBalance.compareTo(BigDecimal.ZERO) == -1) throw InvalidAmountException("not enough balance")
        balance = expectedBalance
        updatedAt = LocalDateTime.now()
    }

    companion object

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