package com.alexbryksin.microservice.dto

import com.alexbryksin.microservice.domain.Currency
import java.math.BigDecimal

data class CreateBankAccountRequest(
    val email: String,
    val phone: String,
    val balance: BigDecimal,
    val currency: String = Currency.USD.toString()
)
