package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
class BankAccountCacheRepositoryImpl(
    private val redissonClient: RedissonReactiveClient,
    private val mapper: ObjectMapper,
    private val tracer: Tracer
) : BankAccountCacheRepository {

    override suspend fun setKey(key: String, value: Any): Unit = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountCacheRepositoryImpl.setKey")

        try {
            val serializedValue = mapper.writeValueAsString(value)
            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .set(serializedValue, timeToLiveSeconds, TimeUnit.SECONDS)
                .awaitSingleOrNull()
                .also {
                    log.info("redis set key: $key, value: $serializedValue")
                    span.tag("key", serializedValue)
                }
        } finally {
            span.end()
        }
    }

    override suspend fun <T> getKey(key: String, clazz: Class<T>): T? = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountCacheRepositoryImpl.getKey")
        try {
            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .get()
                .awaitSingleOrNull()?.let {
                    mapper.readValue(it, clazz).also { value ->
                        log.info("redis get key: $key, value: $value")
                        span.tag("key", value.toString())
                    }
                }
                ?: return@withContext null
        } finally {
            span.end()
        }
    }

    override suspend fun setBankAccountByKey(key: String, bankAccount: BankAccount): Unit =
        withContext(Dispatchers.IO) {
            val bucket = redissonClient.getBucket<String>(getBankAccountKey(key), StringCodec.INSTANCE)
            val serializedBankAccount = mapper.writeValueAsString(bankAccount)
            bucket.set(serializedBankAccount, timeToLiveSeconds, TimeUnit.SECONDS).awaitSingleOrNull()
                .also { log.info("redis set key: $key, value: $serializedBankAccount") }
        }

    override suspend fun getBankAccountByKey(key: String): BankAccount? = withContext(Dispatchers.IO) {
        val bucket = redissonClient.getBucket<String>(getBankAccountKey(key), StringCodec.INSTANCE)
        val cachedBankAccount = bucket.get().awaitSingleOrNull() ?: return@withContext null
        mapper.readValue(cachedBankAccount, BankAccount::class.java)
            .also { log.info("redis get key: $key, value: $it") }
    }

    override suspend fun setBankAccountById(id: String, bankAccount: BankAccount): Unit = withContext(Dispatchers.IO) {
        val serializedBankAccount = mapper.writeValueAsString(bankAccount)
        redissonClient.getBucket<String>(id, StringCodec.INSTANCE)
            .set(serializedBankAccount, timeToLiveSeconds, TimeUnit.SECONDS)
            .awaitSingleOrNull()
            .also { log.info("redis set key: $id, value: $serializedBankAccount") }
    }

    override suspend fun getBankAccountById(id: String): BankAccount? = withContext(Dispatchers.IO) {
        redissonClient.getBucket<String?>(id, StringCodec.INSTANCE)
            .get().awaitSingleOrNull()?.let { mapper.readValue(it, BankAccount::class.java) }
            ?: return@withContext null
    }

    private fun getBankAccountKey(key: String): String = "$bankAccountRedisPrefix:$key"

    private fun getKey(key: String): String = "$prefix:$key"

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountCacheRepositoryImpl::class.java)
        private const val timeToLiveSeconds: Long = 240
        private const val bankAccountRedisPrefix = "bankAccount"
        private const val prefix = "bankAccountMicroservice"
    }
}