package com.alexbryksin.microservice.repositories

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
                .set(serializedValue, cacheTimeToLiveSeconds, TimeUnit.SECONDS)
                .awaitSingleOrNull()
                .also {
                    log.info("redis set key: $key, value: $serializedValue")
                    span.tag("key", serializedValue)
                }
        } finally {
            span.end()
        }
    }

    override suspend fun setKey(key: String, value: Any, timeToLive: Long, timeUnit: TimeUnit): Unit = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountCacheRepositoryImpl.setKey")

        try {
            val serializedValue = mapper.writeValueAsString(value)
            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .set(serializedValue, timeToLive, timeUnit)
                .awaitSingleOrNull()
                .also {
                    log.info("redis set key: $key, value: $serializedValue, timeToLive: $timeToLive $timeUnit")
                    span.tag("key", key).tag("value", serializedValue).tag("timeToLive", "$timeToLive $timeUnit")
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


    private fun getKey(key: String): String = "$prefix:$key"

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountCacheRepositoryImpl::class.java)
        private const val prefix = "bankAccountMicroservice"
        private const val cacheTimeToLiveSeconds = 250L
    }
}