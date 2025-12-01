package koval.proxyseller.twitter.monitoring.health

import groovy.util.logging.Slf4j
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.RuntimeMXBean

@Slf4j
@Component
class ApplicationHealthIndicator implements HealthIndicator {
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean()
    private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean()

    @Override
    Health health() {
        Runtime runtime = Runtime.getRuntime()
        long totalMemory = runtime.totalMemory()
        long freeMemory = runtime.freeMemory()
        long usedMemory = totalMemory - freeMemory
        long maxMemory = runtime.maxMemory()
        double memoryUsagePercent = (usedMemory * 100.0) / maxMemory

        return Health.up()
                .withDetail("uptime", "${runtimeBean.getUptime() / 1000}s")
                .withDetail("memory.used", "${usedMemory / 1024 / 1024}MB")
                .withDetail("memory.total", "${totalMemory / 1024 / 1024}MB")
                .withDetail("memory.max", "${maxMemory / 1024 / 1024}MB")
                .withDetail("memory.usage.percent", "${String.format("%.2f", memoryUsagePercent)}%")
                .withDetail("threads.active", Thread.activeCount())
                .withDetail("status", memoryUsagePercent > 90 ? "WARNING" : "OK")
                .build()
    }
}

