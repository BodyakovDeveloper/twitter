package koval.proxyseller.twitter.config

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.model.comment.Comment
import koval.proxyseller.twitter.model.like.Like
import koval.proxyseller.twitter.model.post.Post
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.IndexOperations

import jakarta.annotation.PostConstruct

@Slf4j
@Configuration
class MongoIndexConfig {
    private final MongoTemplate mongoTemplate

    MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate
    }

    @PostConstruct
    void createIndexes() {
        log.info("Creating MongoDB indexes...")

        // Post indexes
        IndexOperations postIndexOps = mongoTemplate.indexOps(Post.class)
        postIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC))
        postIndexOps.ensureIndex(new Index().on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))
        postIndexOps.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC))
        postIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC).on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))

        // Comment indexes
        IndexOperations commentIndexOps = mongoTemplate.indexOps(Comment.class)
        commentIndexOps.ensureIndex(new Index().on("postId", org.springframework.data.domain.Sort.Direction.ASC))
        commentIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC))
        commentIndexOps.ensureIndex(new Index().on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))
        commentIndexOps.ensureIndex(new Index().on("postId", org.springframework.data.domain.Sort.Direction.ASC).on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))
        commentIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC).on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))

        // Like indexes
        IndexOperations likeIndexOps = mongoTemplate.indexOps(Like.class)
        likeIndexOps.ensureIndex(new Index().on("postId", org.springframework.data.domain.Sort.Direction.ASC))
        likeIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC))
        likeIndexOps.ensureIndex(new Index().on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC))
        likeIndexOps.ensureIndex(new Index().on("postId", org.springframework.data.domain.Sort.Direction.ASC).on("userId", org.springframework.data.domain.Sort.Direction.ASC).on("isDeleted", org.springframework.data.domain.Sort.Direction.ASC).unique())

        log.info("MongoDB indexes created successfully")
    }
}

