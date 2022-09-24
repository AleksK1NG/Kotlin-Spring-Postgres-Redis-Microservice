package com.alexbryksin.microservice.controllers

import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(path = ["/api/v1/bank"])
class BankAccountController {


    @GetMapping(path = ["{id}"])
    suspend fun getBankAccountById(@PathVariable(required = true) id: String) = coroutineScope {
        log.info("GET bank account by ID: $id")
        ResponseEntity.ok("account id: $id")
    }


    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
    }
}