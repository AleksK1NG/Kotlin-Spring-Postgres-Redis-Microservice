package com.alexbryksin.microservice.exceptions

import java.lang.RuntimeException

class BankAccountNotFoundException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
