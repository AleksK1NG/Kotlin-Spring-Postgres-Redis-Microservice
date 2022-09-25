package com.alexbryksin.microservice.controllers

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.dto.SuccessBankAccountResponse
import com.alexbryksin.microservice.dto.of
import com.alexbryksin.microservice.services.BankAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping(path = ["/api/v1/bank"])
@Tag(name = "Bank Account", description = "Bank Account controller REST Endpoints")
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PutMapping(path = ["{id}"])
    @Operation(method = "depositAmount", summary = "deposit amount", operationId = "depositAmount")
    suspend fun depositAmount(@PathVariable(required = true) id: UUID, @RequestBody depositAmountRequest: DepositAmountRequest) = coroutineScope {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.depositAmount(id, depositAmountRequest))
            .also { log.info("updated account: $it") })
    }


    @PostMapping
    @Operation(method = "createBankAccount", summary = "create new bank account", operationId = "createBankAccount")
    suspend fun createBankAccount(@RequestBody createBankAccountRequest: CreateBankAccountRequest) = coroutineScope {
        bankAccountService.createBankAccount(createBankAccountRequest)
            .let {
                log.info("created bank account: $it")
                ResponseEntity.status(HttpStatus.CREATED).body(SuccessBankAccountResponse.of(it))
            }
    }

    @GetMapping(path = ["{id}"])
    @Operation(method = "getBankAccountById", summary = "get bank account by id", operationId = "getBankAccountById")
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = coroutineScope {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.getBankAccountById(id))
            .also { log.info("success get bank account: $it") })
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
    }
}