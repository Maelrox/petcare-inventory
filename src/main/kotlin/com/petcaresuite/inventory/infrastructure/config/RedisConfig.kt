package com.petcaresuite.inventory.infrastructure.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val redisHost: String,
    @Value("\${spring.data.redis.port}") private val redisPort: Int,
    @Value("\${spring.data.redis.password}") private val redisPassword: String,
    ) {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$redisHost:$redisPort")
            .setPassword(redisPassword)
            .setConnectionPoolSize(64)
            .setConnectionMinimumIdleSize(24)
            .setRetryAttempts(3)
            .setRetryInterval(1500)
        return Redisson.create(config)
    }
}