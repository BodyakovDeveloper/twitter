package koval.proxyseller.twitter.monitoring

import groovy.util.logging.Slf4j
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

@Slf4j
@Service
class MetricsService {
    private final MeterRegistry meterRegistry

    // Business Metrics
    private final Counter userRegistrationsCounter
    private final Counter userLoginsCounter
    private final Counter postsCreatedCounter
    private final Counter postsDeletedCounter
    private final Counter commentsCreatedCounter
    private final Counter likesCreatedCounter
    private final Counter likesDeletedCounter
    private final Counter followOperationsCounter
    private final Counter unfollowOperationsCounter

    // Error Metrics
    private final Counter authenticationErrorsCounter
    private final Counter authorizationErrorsCounter
    private final Counter validationErrorsCounter
    private final Counter entityNotFoundErrorsCounter

    // Performance Metrics
    private final Timer postCreationTimer
    private final Timer commentCreationTimer
    private final Timer likeOperationTimer
    private final Timer userRegistrationTimer
    private final Timer authenticationTimer

    MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry

        // Initialize business counters
        this.userRegistrationsCounter = Counter.builder("twitter.user.registrations")
                .description("Total number of user registrations")
                .register(meterRegistry)

        this.userLoginsCounter = Counter.builder("twitter.user.logins")
                .description("Total number of user logins")
                .register(meterRegistry)

        this.postsCreatedCounter = Counter.builder("twitter.posts.created")
                .description("Total number of posts created")
                .register(meterRegistry)

        this.postsDeletedCounter = Counter.builder("twitter.posts.deleted")
                .description("Total number of posts deleted")
                .register(meterRegistry)

        this.commentsCreatedCounter = Counter.builder("twitter.comments.created")
                .description("Total number of comments created")
                .register(meterRegistry)

        this.likesCreatedCounter = Counter.builder("twitter.likes.created")
                .description("Total number of likes created")
                .register(meterRegistry)

        this.likesDeletedCounter = Counter.builder("twitter.likes.deleted")
                .description("Total number of likes deleted")
                .register(meterRegistry)

        this.followOperationsCounter = Counter.builder("twitter.follow.operations")
                .description("Total number of follow operations")
                .register(meterRegistry)

        this.unfollowOperationsCounter = Counter.builder("twitter.unfollow.operations")
                .description("Total number of unfollow operations")
                .register(meterRegistry)

        // Initialize error counters
        this.authenticationErrorsCounter = Counter.builder("twitter.errors.authentication")
                .description("Total number of authentication errors")
                .register(meterRegistry)

        this.authorizationErrorsCounter = Counter.builder("twitter.errors.authorization")
                .description("Total number of authorization errors")
                .register(meterRegistry)

        this.validationErrorsCounter = Counter.builder("twitter.errors.validation")
                .description("Total number of validation errors")
                .register(meterRegistry)

        this.entityNotFoundErrorsCounter = Counter.builder("twitter.errors.entity_not_found")
                .description("Total number of entity not found errors")
                .register(meterRegistry)

        // Initialize timers
        this.postCreationTimer = Timer.builder("twitter.posts.creation.time")
                .description("Time taken to create a post")
                .register(meterRegistry)

        this.commentCreationTimer = Timer.builder("twitter.comments.creation.time")
                .description("Time taken to create a comment")
                .register(meterRegistry)

        this.likeOperationTimer = Timer.builder("twitter.likes.operation.time")
                .description("Time taken for like/unlike operations")
                .register(meterRegistry)

        this.userRegistrationTimer = Timer.builder("twitter.user.registration.time")
                .description("Time taken for user registration")
                .register(meterRegistry)

        this.authenticationTimer = Timer.builder("twitter.authentication.time")
                .description("Time taken for authentication")
                .register(meterRegistry)
    }

    // Business metrics
    void incrementUserRegistrations() {
        userRegistrationsCounter.increment()
    }

    void incrementUserLogins() {
        userLoginsCounter.increment()
    }

    void incrementPostsCreated() {
        postsCreatedCounter.increment()
    }

    void incrementPostsDeleted() {
        postsDeletedCounter.increment()
    }

    void incrementCommentsCreated() {
        commentsCreatedCounter.increment()
    }

    void incrementLikesCreated() {
        likesCreatedCounter.increment()
    }

    void incrementLikesDeleted() {
        likesDeletedCounter.increment()
    }

    void incrementFollowOperations() {
        followOperationsCounter.increment()
    }

    void incrementUnfollowOperations() {
        unfollowOperationsCounter.increment()
    }

    // Error metrics
    void incrementAuthenticationErrors() {
        authenticationErrorsCounter.increment()
    }

    void incrementAuthorizationErrors() {
        authorizationErrorsCounter.increment()
    }

    void incrementValidationErrors() {
        validationErrorsCounter.increment()
    }

    void incrementEntityNotFoundErrors() {
        entityNotFoundErrorsCounter.increment()
    }

    // Performance metrics
    void recordPostCreationTime(long duration, TimeUnit unit) {
        postCreationTimer.record(duration, unit)
    }

    void recordCommentCreationTime(long duration, TimeUnit unit) {
        commentCreationTimer.record(duration, unit)
    }

    void recordLikeOperationTime(long duration, TimeUnit unit) {
        likeOperationTimer.record(duration, unit)
    }

    void recordUserRegistrationTime(long duration, TimeUnit unit) {
        userRegistrationTimer.record(duration, unit)
    }

    void recordAuthenticationTime(long duration, TimeUnit unit) {
        authenticationTimer.record(duration, unit)
    }

    // Custom gauge for active users (example)
    void setActiveUsersGauge(int activeUsers) {
        meterRegistry.gauge("twitter.users.active", activeUsers)
    }

    // Custom gauge for total posts
    void setTotalPostsGauge(long totalPosts) {
        meterRegistry.gauge("twitter.posts.total", totalPosts)
    }
}

