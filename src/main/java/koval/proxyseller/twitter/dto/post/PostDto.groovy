package koval.proxyseller.twitter.dto.post

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema
import koval.proxyseller.twitter.model.comment.Comment
import koval.proxyseller.twitter.model.like.Like

import java.time.Instant

@TupleConstructor
@Schema(description = "Post data transfer object")
class PostDto {
    @Schema(description = "Post unique identifier", example = "507f1f77bcf86cd799439011")
    String id

    @Schema(description = "User ID who created the post", example = "507f1f77bcf86cd799439012")
    String userId

    @Schema(description = "Post content", example = "This is my first post!")
    String content

    @Schema(description = "Image URL", example = "https://example.com/image.jpg")
    String imageUrl

    @Schema(description = "Post creation timestamp", example = "2024-01-01T12:00:00Z")
    Instant createdAt

    @Schema(description = "Post last update timestamp", example = "2024-01-01T12:00:00Z")
    Instant updatedAt

    @Schema(description = "Post location", example = "New York, USA")
    String location

    @Schema(description = "Set of likes on the post")
    Set<Like> likes = new HashSet<>()

    @Schema(description = "Set of comments on the post")
    Set<Comment> comments = new HashSet<>()

    // Add a comment to the post
    void addComment(Comment comment) {
        comments.add(comment)
    }

    // Remove a comment from the post
    void removeComment(Comment comment) {
        comments.remove(comment)
    }

    // Add a like to the post
    void addLike(Like like) {
        likes.add(like)
    }

    // Remove a like from the post
    void removeLike(Like like) {
        likes.remove(like)
    }
}
