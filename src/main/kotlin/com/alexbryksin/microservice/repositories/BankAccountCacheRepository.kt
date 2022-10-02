package com.alexbryksin.microservice.repositories

import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
interface BankAccountCacheRepository {

    suspend fun setKey(key: String, value: Any)

    suspend fun setKey(key: String, value: Any, timeToLive: Long, timeUnit: TimeUnit)

    suspend fun <T> getKey(key: String, clazz: Class<T>): T?
}