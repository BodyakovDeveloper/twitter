package koval.proxyseller.twitter.dto.comment

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

import java.time.Instant

@TupleConstructor
@Schema(description = "Comment data transfer object")
class CommentDto {
    @Schema(description = "Comment unique identifier", example = "507f1f77bcf86cd799439013")
    String id

    @Schema(description = "Comment content", example = "Great post!")
    String content

    @Schema(description = "User ID who created the comment", example = "507f1f77bcf86cd799439012")
    String userId

    @Schema(description = "Post ID this comment belongs to", example = "507f1f77bcf86cd799439011")
    String postId

    @Schema(description = "Comment creation timestamp", example = "2024-01-01T12:00:00Z")
    Instant createdAt

    @Schema(description = "Comment last update timestamp", example = "2024-01-01T12:00:00Z")
    Instant updatedAt
}
