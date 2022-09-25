package com.alexbryksin.microservice.exceptions

data class InvalidAmountException(override val message: String?) : RuntimeException(message) {
}