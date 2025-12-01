package koval.proxyseller.twitter.config


import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.time.Duration

@Configuration
class CircuitBreakerConfig {

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .build()

        return CircuitBreakerRegistry.of(config)
    }
}

