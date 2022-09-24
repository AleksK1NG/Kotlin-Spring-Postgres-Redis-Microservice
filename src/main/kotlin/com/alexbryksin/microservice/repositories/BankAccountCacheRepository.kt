package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class BankAccountCacheRepository(private val redissonClient: RedissonReactiveClient) {
    private val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(ParameterNamesModule())
        .registerModule(Jdk8Module())
        .registerModule(JavaTimeModule())
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )

    suspend fun setBankAccount(id: String, bankAccount: BankAccount) {
        try {
            val bucket = redissonClient.getBucket<String>(id, StringCodec.INSTANCE)
            val valueAsString = mapper.writeValueAsString(bankAccount)
            bucket.set(valueAsString).awaitSingleOrNull().also { log.info("redis set key: $id, value: $bankAccount") }
        } catch (ex: Exception) {
            log.error("set", ex)
        }

    }

    suspend fun getBankAccount(id: String): BankAccount? {
        val bucket = redissonClient.getBucket<String?>(id, StringCodec.INSTANCE)
        if (!bucket.isExists.awaitSingle()) return null
        return mapper.readValue(bucket.get().awaitSingle(), BankAccount::class.java)
            .also { log.info("redis get key: $id, value: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountCacheRepository::class.java)
    }
}