package koval.proxyseller.twitter.config

import org.springframework.beans.factory.annotation.Value
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

    @Value('${cache.default-ttl:600}')
    private int defaultTtl

    @Value('${cache.posts-ttl:10}')
    private int postsTtl

    @Value('${cache.users-ttl:15}')
    private int usersTtl

    @Value('${cache.comments-ttl:5}')
    private int commentsTtl

    @Value('${cache.likes-ttl:5}')
    private int likesTtl

    @Value('${cache.user-posts-ttl:10}')
    private int userPostsTtl

    @Value('${cache.following-posts-ttl:5}')
    private int followingPostsTtl

    @Bean
    CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(defaultTtl))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("posts", defaultConfig.entryTtl(Duration.ofMinutes(postsTtl)))
                .withCacheConfiguration("users", defaultConfig.entryTtl(Duration.ofMinutes(usersTtl)))
                .withCacheConfiguration("comments", defaultConfig.entryTtl(Duration.ofMinutes(commentsTtl)))
                .withCacheConfiguration("likes", defaultConfig.entryTtl(Duration.ofMinutes(likesTtl)))
                .withCacheConfiguration("userPosts", defaultConfig.entryTtl(Duration.ofMinutes(userPostsTtl)))
                .withCacheConfiguration("followingPosts", defaultConfig.entryTtl(Duration.ofMinutes(followingPostsTtl)))

        return builder.build()
    }
}

