package com.alexbryksin.microservice.controllers

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.services.BankAccountService
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping(path = ["/api/v1/bank"])
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PostMapping
    suspend fun createBankAccount(@RequestBody createBankAccountRequest: CreateBankAccountRequest) = coroutineScope {
        try {
            val bankAccount = bankAccountService.createBankAccount(createBankAccountRequest)
            ResponseEntity.status(201).body(bankAccount).also {
                log.info("created bank account: $bankAccount")
            }
        } catch (ex: Exception) {
            log.error("create bank account ex", ex)
            ResponseEntity.status(500).body(ex.message)
        }
    }

    @GetMapping(path = ["{id}"])
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = coroutineScope {
        try {
            log.info("GET bank account by ID: $id")
            val bankAccount = bankAccountService.getBankAccountById(id)
            ResponseEntity.ok(bankAccount)
        } catch (ex: Exception) {
            log.error("create bank account ex", ex)
            ResponseEntity.status(500).body(ex.message)
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
    }
}