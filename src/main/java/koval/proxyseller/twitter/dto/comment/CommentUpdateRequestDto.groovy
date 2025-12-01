package koval.proxyseller.twitter.dto.comment

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Comment update request")
class CommentUpdateRequestDto {
    @Schema(description = "Updated comment content", example = "Updated comment text!", required = true, maxLength = 500)
    String content
}
