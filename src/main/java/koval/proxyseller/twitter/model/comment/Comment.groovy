package koval.proxyseller.twitter.model.comment

import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@Document(collection = "comments")
class Comment {
    String id
    String content
    Instant createdAt
    Instant updatedAt
    String userId
    String postId
    boolean isDeleted = false
}
