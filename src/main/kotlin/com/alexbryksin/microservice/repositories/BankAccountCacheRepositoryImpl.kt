package com.alexbryksin.microservice.repositories

import com.alexbryksin.microservice.domain.BankAccount
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
class BankAccountCacheRepositoryImpl(
    private val redissonClient: RedissonReactiveClient,
    private val mapper: ObjectMapper
) : BankAccountCacheRepository {

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
        val bucket = redissonClient.getBucket<String>(id, StringCodec.INSTANCE)
        val serializedBankAccount = mapper.writeValueAsString(bankAccount)
        bucket.set(serializedBankAccount, timeToLiveSeconds, TimeUnit.SECONDS).awaitSingleOrNull()
            .also { log.info("redis set key: $id, value: $serializedBankAccount") }
    }

    override suspend fun getBankAccountById(id: String): BankAccount? = withContext(Dispatchers.IO) {
        val bucket = redissonClient.getBucket<String?>(id, StringCodec.INSTANCE)
        val cachedBankAccount = bucket.get().awaitSingleOrNull() ?: return@withContext null
        mapper.readValue(cachedBankAccount, BankAccount::class.java)
            .also { log.info("redis get key: $id, value: $it") }
    }

    private fun getBankAccountKey(key: String): String = "$bankAccountRedisPrefix:$key"

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountCacheRepositoryImpl::class.java)
        private const val timeToLiveSeconds: Long = 240
        private const val bankAccountRedisPrefix = "bankAccount"
    }
}