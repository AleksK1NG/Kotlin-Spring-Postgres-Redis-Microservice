package com.alexbryksin.microservice.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

data class DepositAmountRequest(@get:DecimalMin(value = "0.0", message = "invalid balance amount") val amount: BigDecimal)
