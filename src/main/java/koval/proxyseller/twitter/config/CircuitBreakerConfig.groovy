package koval.proxyseller.twitter.config


import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.time.Duration

@Configuration
class CircuitBreakerConfig {

    @Value('${circuit-breaker.sliding-window-size:10}')
    private int slidingWindowSize

    @Value('${circuit-breaker.failure-rate-threshold:50.0}')
    private float failureRateThreshold

    @Value('${circuit-breaker.wait-duration-in-open-state:30}')
    private int waitDurationInOpenState

    @Value('${circuit-breaker.permitted-number-of-calls-in-half-open-state:3}')
    private int permittedNumberOfCallsInHalfOpenState

    @Value('${circuit-breaker.slow-call-rate-threshold:50.0}')
    private float slowCallRateThreshold

    @Value('${circuit-breaker.slow-call-duration-threshold:2}')
    private int slowCallDurationThreshold

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(slidingWindowSize)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slowCallRateThreshold(slowCallRateThreshold)
                .slowCallDurationThreshold(Duration.ofSeconds(slowCallDurationThreshold))
                .build()

        return CircuitBreakerRegistry.of(config)
    }
}

