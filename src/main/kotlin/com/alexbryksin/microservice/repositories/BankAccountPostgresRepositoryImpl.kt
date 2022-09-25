package com.alexbryksin.microservice.repositories

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository


@Repository
class BankAccountPostgresRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) {

}