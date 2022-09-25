package com.alexbryksin.microservice.controllers

import com.alexbryksin.microservice.exceptions.BankAccountNotFoundException
import com.alexbryksin.microservice.exceptions.ErrorHttpResponse
import com.alexbryksin.microservice.exceptions.InvalidAmountException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime


@Order(2)
@ControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(ex: RuntimeException, request: ServerHttpRequest): ResponseEntity<ErrorHttpResponse> {
        val errorHttpResponse = ErrorHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message ?: "", LocalDateTime.now().toString())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorHttpResponse).also {
            log.error("(GlobalControllerAdvice) RuntimeException", ex)
        }
    }

    @ExceptionHandler(value = [BankAccountNotFoundException::class])
    fun handleBankAccountNotFoundException(ex: BankAccountNotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorHttpResponse> {
        val errorHttpResponse = ErrorHttpResponse(HttpStatus.NOT_FOUND.value(), ex.message ?: "", LocalDateTime.now().toString())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorHttpResponse)
            .also { log.error("(GlobalControllerAdvice) BankAccountNotFoundException NOT_FOUND", ex) }
    }

    @ExceptionHandler(value = [InvalidAmountException::class])
    fun handleInvalidAmountExceptionException(ex: InvalidAmountException, request: ServerHttpRequest): ResponseEntity<ErrorHttpResponse> {
        val errorHttpResponse = ErrorHttpResponse(HttpStatus.BAD_REQUEST.value(), ex.message ?: "", LocalDateTime.now().toString())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorHttpResponse)
            .also { log.error("(GlobalControllerAdvice) InvalidAmountException BAD_REQUEST", ex) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)
    }
}