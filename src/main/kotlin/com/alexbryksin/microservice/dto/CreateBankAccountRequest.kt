package com.alexbryksin.microservice.dto

import java.math.BigDecimal

data class CreateBankAccountRequest(
    val email: String,
    val phone: String,
    val balance: BigDecimal,
    val currency: String = "USD"
)
