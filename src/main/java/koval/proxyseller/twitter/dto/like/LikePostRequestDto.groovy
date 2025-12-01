package koval.proxyseller.twitter.dto.like

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Like post request")
class LikePostRequestDto {
    @Schema(description = "Post ID to like", example = "507f1f77bcf86cd799439011", required = true)
    String postId
}
