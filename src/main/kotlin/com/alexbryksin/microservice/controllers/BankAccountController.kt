package com.alexbryksin.microservice.controllers

import com.alexbryksin.microservice.dto.CreateBankAccountRequest
import com.alexbryksin.microservice.dto.DepositAmountRequest
import com.alexbryksin.microservice.dto.SuccessBankAccountResponse
import com.alexbryksin.microservice.dto.of
import com.alexbryksin.microservice.repositories.BankAccountPostgresRepositoryImpl
import com.alexbryksin.microservice.services.BankAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*


@RestController
@RequestMapping(path = ["/api/v1/bank"])
@Tag(name = "Bank Account", description = "Bank Account controller REST Endpoints")
class BankAccountController(
    private val bankAccountService: BankAccountService,
    private val bankAccountPostgresRepositoryImpl: BankAccountPostgresRepositoryImpl
) {

    @GetMapping(path = ["all/balance"])
    @Operation(method = "findAllAccounts", summary = "find all bank account with given amount range", operationId = "findAllAccounts")
    suspend fun findAllAccounts(
        @RequestParam(name = "min", defaultValue = "0") min: BigDecimal,
        @RequestParam(name = "max", defaultValue = "500000000") max: BigDecimal,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ) = withTimeout(httpTimeoutMillis) {
        ResponseEntity.ok(bankAccountService.findByBalanceAmount(min, max, PageRequest.of(page, size))
            .map { SuccessBankAccountResponse.of(it) }
            .also { log.info("find by balance amount response: $it") })
    }

    @PutMapping(path = ["{id}"])
    @Operation(method = "depositAmount", summary = "deposit amount", operationId = "depositAmount")
    suspend fun depositAmount(@PathVariable(required = true) id: UUID, @RequestBody depositAmountRequest: DepositAmountRequest) = withTimeout(httpTimeoutMillis) {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.depositAmount(id, depositAmountRequest))
            .also { log.info("updated account: $it") })
    }

    @PostMapping
    @Operation(method = "createBankAccount", summary = "create new bank account", operationId = "createBankAccount")
    suspend fun createBankAccount(@RequestBody createBankAccountRequest: CreateBankAccountRequest) = withTimeout(httpTimeoutMillis) {
        bankAccountService.createBankAccount(createBankAccountRequest)
            .let {
                log.info("created bank account: $it")
                ResponseEntity.status(HttpStatus.CREATED).body(SuccessBankAccountResponse.of(it))
            }
    }

    @GetMapping(path = ["{id}"])
    @Operation(method = "getBankAccountById", summary = "get bank account by id", operationId = "getBankAccountById")
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = withTimeout(httpTimeoutMillis) {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.getBankAccountById(id))
            .also { log.info("success get bank account: $it") })
    }

    @GetMapping(path = ["/email/{email}"])
    @Operation(method = "getBankAccountByEmail", summary = "get bank account by email", operationId = "getBankAccountByEmail")
    suspend fun getBankAccountByEmail(@PathVariable(required = true) email: String) = withTimeout(httpTimeoutMillis) {
        ResponseEntity.ok(SuccessBankAccountResponse.of(bankAccountService.getBankAccountByEmail(email))
            .also { log.info("success get bank account bu email: $it") })
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
        private const val httpTimeoutMillis = 3000L
    }
}