package koval.proxyseller.twitter.monitoring.health

import groovy.util.logging.Slf4j
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Slf4j
@Component
class RedisHealthIndicator implements HealthIndicator {
    private final RedisConnectionFactory redisConnectionFactory

    RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory
    }

    @Override
    Health health() {
        try {
            def connection = redisConnectionFactory.getConnection()
            def pong = connection.ping()
            connection.close()

            if (pong == "PONG") {
                return Health.up()
                        .withDetail("status", "Connected")
                        .withDetail("response", pong)
                        .build()
            } else {
                return Health.down()
                        .withDetail("status", "Unexpected response")
                        .withDetail("response", pong)
                        .build()
            }
        } catch (Exception e) {
            log.error("Redis health check failed", e)
            return Health.down()
                    .withDetail("error", e.message)
                    .withDetail("status", "Disconnected")
                    .build()
        }
    }
}

