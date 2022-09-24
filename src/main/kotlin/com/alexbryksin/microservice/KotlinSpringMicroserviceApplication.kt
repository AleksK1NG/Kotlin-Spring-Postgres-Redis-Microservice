package com.alexbryksin.microservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringMicroserviceApplication

fun main(args: Array<String>) {
	runApplication<KotlinSpringMicroserviceApplication>(*args)
}
