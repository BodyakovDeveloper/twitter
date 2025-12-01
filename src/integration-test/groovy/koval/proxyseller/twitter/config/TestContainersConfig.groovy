package koval.proxyseller.twitter.config

import groovy.util.logging.Slf4j
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@Slf4j
class TestContainersConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static MongoDBContainer mongoDBContainer
    static GenericContainer redisContainer

    static {
        // MongoDB Container
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
                .withReuse(true)
        mongoDBContainer.start()

        // Redis Container
        redisContainer = new GenericContainer(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379)
                .withReuse(true)
        redisContainer.start()
    }

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("Initializing Testcontainers...")
        
        // MongoDB properties
        TestPropertyValues.of(
                "spring.data.mongodb.host=${mongoDBContainer.host}",
                "spring.data.mongodb.port=${mongoDBContainer.firstMappedPort}",
                "spring.data.mongodb.username=test",
                "spring.data.mongodb.password=test",
                "spring.data.mongodb.database=twitter_test"
        ).applyTo(applicationContext.environment)

        // Redis properties
        TestPropertyValues.of(
                "spring.data.redis.host=${redisContainer.host}",
                "spring.data.redis.port=${redisContainer.firstMappedPort}"
        ).applyTo(applicationContext.environment)

        log.info("MongoDB started on port: ${mongoDBContainer.firstMappedPort}")
        log.info("Redis started on port: ${redisContainer.firstMappedPort}")
    }

    static void stopContainers() {
        if (mongoDBContainer != null && mongoDBContainer.isRunning()) {
            mongoDBContainer.stop()
        }
        if (redisContainer != null && redisContainer.isRunning()) {
            redisContainer.stop()
        }
    }
}

