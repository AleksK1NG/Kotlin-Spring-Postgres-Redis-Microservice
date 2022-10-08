package com.alexbryksin.microservice.exceptions

import java.lang.RuntimeException

class BankAccountNotFoundException : RuntimeException {
    constructor(id: String?) : super("bank account with id: $id not found")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
