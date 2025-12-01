package koval.proxyseller.twitter.model.like

import groovy.transform.Canonical
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@Document(collection = "likes")
@Canonical
class Like {
    @Id
    String id
    String userId
    String postId
    Instant createdAt
    Instant updatedAt
    boolean isDeleted = false
}
