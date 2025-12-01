package koval.proxyseller.twitter.monitoring.health

import groovy.util.logging.Slf4j
import org.bson.Document
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Slf4j
@Component
class MongoHealthIndicator implements HealthIndicator {
    private final MongoTemplate mongoTemplate

    MongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate
    }

    @Override
    Health health() {
        try {
            String databaseName = mongoTemplate.getDb().getName()
            mongoTemplate.getDb().runCommand(new Document("ping", 1))

            return Health.up()
                    .withDetail("database", databaseName)
                    .withDetail("status", "Connected")
                    .build()
        } catch (Exception e) {
            log.error("MongoDB health check failed", e)
            return Health.down()
                    .withDetail("error", e.message)
                    .withDetail("status", "Disconnected")
                    .build()
        }
    }
}

