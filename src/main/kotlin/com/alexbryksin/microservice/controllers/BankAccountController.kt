package com.alexbryksin.microservice.controllers

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.dto.SuccessBankAccountResponse
import com.alexbryksin.microservice.dto.of
import com.alexbryksin.microservice.services.BankAccountService
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping(path = ["/api/v1/bank"])
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PutMapping(path = ["{id}"])
    suspend fun depositAmount(@PathVariable(required = true) id: UUID, @RequestBody depositAmountRequest: DepositAmountRequest) = coroutineScope {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.depositAmount(id, depositAmountRequest))
            .also { log.info("updated account: $it") })
    }

    @PostMapping
    suspend fun createBankAccount(@RequestBody createBankAccountRequest: CreateBankAccountRequest) = coroutineScope {
        bankAccountService.createBankAccount(createBankAccountRequest)
            .also {
                log.info("created bank account: $it")
                ResponseEntity.status(HttpStatus.CREATED).body(SuccessBankAccountResponse.of(it))
            }
    }

    @GetMapping(path = ["{id}"])
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = coroutineScope {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.getBankAccountById(id))
            .also { log.info("success get bank account: $it") })
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
    }
}