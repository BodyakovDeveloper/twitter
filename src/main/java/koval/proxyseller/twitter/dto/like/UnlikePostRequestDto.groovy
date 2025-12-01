package koval.proxyseller.twitter.dto.like

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Unlike post request")
class UnlikePostRequestDto {
    @Schema(description = "Post ID to unlike", example = "507f1f77bcf86cd799439011", required = true)
    String postId
}
