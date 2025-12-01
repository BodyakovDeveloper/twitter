package koval.proxyseller.twitter.dto.post

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Post update request")
class PostUpdateRequestDto {
    @Schema(description = "Image URL for the post", example = "https://example.com/image.jpg")
    String imageUrl

    @Schema(description = "Post content", example = "Updated post content!", maxLength = 280)
    String content

    @Schema(description = "Post location", example = "New York, USA")
    String location
}
