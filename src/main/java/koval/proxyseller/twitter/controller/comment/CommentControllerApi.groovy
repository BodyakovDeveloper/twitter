package koval.proxyseller.twitter.controller.comment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Comments", description = "Comment management endpoints")
@RequestMapping("/api/v1/comments")
@SecurityRequirement(name = "bearer-jwt")
interface CommentControllerApi {

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment on a post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @PostMapping
    ResponseEntity<CommentDto> createComment(@RequestBody CommentCreateRequestDto commentCreateRequestDto)

    @Operation(
            summary = "Get comments by post ID",
            description = "Retrieves all comments for a specific post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "List of comments"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @GetMapping("/post/{id}")
    ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get comments by post ID (paginated)",
            description = "Retrieves a paginated list of comments for a specific post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Paginated list of comments"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @GetMapping("/post/{id}/paginated")
    ResponseEntity<PageResponseDto<CommentDto>> getCommentsByPostIdPaginated(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,asc") @RequestParam(defaultValue = "createdAt,asc") String sort
    )

    @Operation(
            summary = "Get comments by user ID",
            description = "Retrieves all comments created by a specific user"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "List of user's comments"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/user/{id}")
    ResponseEntity<List<CommentDto>> getCommentsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get comments by user ID (paginated)",
            description = "Retrieves a paginated list of comments created by a specific user"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Paginated list of user's comments"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/user/{id}/paginated")
    ResponseEntity<PageResponseDto<CommentDto>> getCommentsByUserIdPaginated(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort
    )

    @Operation(
            summary = "Get comment by ID",
            description = "Retrieves a comment by its unique identifier"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Comment found"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    ])
    @GetMapping("/{id}")
    ResponseEntity<CommentDto> getCommentById(
            @Parameter(description = "Comment ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Update comment",
            description = "Updates an existing comment"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this comment")
    ])
    @PatchMapping("/{id}")
    ResponseEntity<CommentDto> updateComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable(name = "id") String id,
            @RequestBody CommentUpdateRequestDto commentUpdateRequestDto
    )

    @Operation(
            summary = "Delete comment",
            description = "Soft deletes a comment by ID"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this comment")
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable(name = "id") String id
    )
}

