package koval.proxyseller.twitter.dto.comment

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Comment creation request")
class CommentCreateRequestDto {
    @Schema(description = "Comment content", example = "Great post!", required = true, maxLength = 500)
    String content

    @Schema(description = "Post ID to comment on", example = "507f1f77bcf86cd799439011", required = true)
    String postId
}
