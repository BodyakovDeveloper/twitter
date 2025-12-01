package koval.proxyseller.twitter.controller.like

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Likes", description = "Like management endpoints")
@RequestMapping("/api/v1/likes")
@SecurityRequirement(name = "bearer-jwt")
interface LikeControllerApi {

    @Operation(
            summary = "Like a post",
            description = "Adds a like to a post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "201", description = "Post liked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @PostMapping("/like")
    ResponseEntity<LikeDto> likePost(@RequestBody LikePostRequestDto likeCreateRequestDto)

    @Operation(
            summary = "Unlike a post",
            description = "Removes a like from a post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "Post unliked successfully"),
            @ApiResponse(responseCode = "404", description = "Like or post not found")
    ])
    @PostMapping("/unlike")
    ResponseEntity<Void> unlikePost(@RequestBody UnlikePostRequestDto unlikePostRequestDto)

    @Operation(
            summary = "Get likes count by post ID",
            description = "Retrieves the total number of likes for a specific post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Likes count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @GetMapping("/post/count/{id}")
    ResponseEntity<Integer> getLikesCountByPostId(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get likes by post ID",
            description = "Retrieves all likes for a specific post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "List of likes"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @GetMapping("/post/{id}")
    ResponseEntity<List<LikeDto>> getLikesByPostId(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id
    )
}

