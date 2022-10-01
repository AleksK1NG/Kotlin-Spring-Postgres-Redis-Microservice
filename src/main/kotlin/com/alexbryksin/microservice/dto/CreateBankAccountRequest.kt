package com.alexbryksin.microservice.dto

import com.alexbryksin.microservice.domain.Currency
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Size

data class CreateBankAccountRequest(
    @get:Email(message = "invalid email") @get:Size(min = 6, max = 60) val email: String,
    @get:Size(min = 6, max = 12) val phone: String,
    @get:DecimalMin(value = "0.0", message = "invalid balance amount") val balance: BigDecimal,
    val currency: Currency = Currency.USD
)
