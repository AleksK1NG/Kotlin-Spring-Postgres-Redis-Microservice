package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


@Service
class BankAccountCacheRepository(private val redissonClient: RedissonReactiveClient, private val mapper: ObjectMapper) {

    suspend fun setBankAccount(id: String, bankAccount: BankAccount) {
        val bucket = redissonClient.getBucket<String>(id, StringCodec.INSTANCE)
        val serializedBankAccount = mapper.writeValueAsString(bankAccount)
        bucket.set(serializedBankAccount, timeToLiveSeconds, TimeUnit.SECONDS).awaitSingleOrNull()
            .also { log.info("redis set key: $id, value: $serializedBankAccount") }
    }

    suspend fun getBankAccount(id: String): BankAccount? {
        val bucket = redissonClient.getBucket<String?>(id, StringCodec.INSTANCE)
        val cachedBankAccount = bucket.get().awaitSingleOrNull() ?: return null
        return mapper.readValue(cachedBankAccount, BankAccount::class.java)
            .also { log.info("redis get key: $id, value: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountCacheRepository::class.java)
        private const val timeToLiveSeconds: Long = 15
    }
}