package koval.proxyseller.twitter.model.post

import koval.proxyseller.twitter.model.comment.Comment
import koval.proxyseller.twitter.model.like.Like
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@Document(collection = "posts")
class Post {
    @Id
    String id
    String userId
    String content
    String imageUrl
    String location
    Set<Like> likes = new HashSet<>()
    Set<Comment> comments = new HashSet<>()
    boolean isDeleted = false
    Instant createdAt
    Instant updatedAt

    // Add like to post
    void addLike(Like like) {
        likes.add(like)
    }

    // Remove like from post
    void removeLike(Like like) {
        likes.remove(like)
    }

    // Add comment to post
    void addComment(Comment comment) {
        comments.add(comment)
    }

    // Remove comment from post
    void removeComment(Comment comment) {
        comments.remove(comment)
    }
}
