package koval.proxyseller.twitter.dto.like

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

import java.time.Instant

@TupleConstructor
@Schema(description = "Like data transfer object")
class LikeDto {
    @Schema(description = "Like unique identifier", example = "507f1f77bcf86cd799439014")
    String id

    @Schema(description = "Post ID that was liked", example = "507f1f77bcf86cd799439011")
    String postId

    @Schema(description = "User ID who liked the post", example = "507f1f77bcf86cd799439012")
    String userId

    @Schema(description = "Like creation timestamp", example = "2024-01-01T12:00:00Z")
    Instant createdAt

    @Schema(description = "Like last update timestamp", example = "2024-01-01T12:00:00Z")
    Instant updatedAt
}
