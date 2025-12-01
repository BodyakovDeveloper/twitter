package koval.proxyseller.twitter.config

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry)
    }
}
