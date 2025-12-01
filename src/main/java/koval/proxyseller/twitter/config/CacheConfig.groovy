package koval.proxyseller.twitter.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("posts", defaultConfig.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("users", defaultConfig.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("comments", defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("likes", defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("userPosts", defaultConfig.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("followingPosts", defaultConfig.entryTtl(Duration.ofMinutes(5)))

        return builder.build()
    }
}

