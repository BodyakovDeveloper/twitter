package koval.proxyseller.twitter.controller.post

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Posts", description = "Post management endpoints")
@RequestMapping("/api/v1/posts")
@SecurityRequirement(name = "bearer-jwt")
interface PostControllerApi {

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post for the authenticated user"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    @PostMapping
    ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto createPostRequestDto)

    @Operation(
            summary = "Get post by ID",
            description = "Retrieves a post by its unique identifier"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    ])
    @GetMapping("/{id}")
    ResponseEntity<PostDto> getPostById(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get all posts",
            description = "Retrieves a list of all posts"
    )
    @ApiResponse(responseCode = "200", description = "List of posts")
    @GetMapping
    ResponseEntity<List<PostDto>> getAllPosts()

    @Operation(
            summary = "Get all posts (paginated)",
            description = "Retrieves a paginated list of all posts"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of posts")
    @GetMapping("/paginated")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsPaginated(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort
    )

    @Operation(
            summary = "Get posts by current user",
            description = "Retrieves all posts created by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "List of user's posts")
    @GetMapping("/user")
    ResponseEntity<List<PostDto>> getAllPostsByCurrentUser()

    @Operation(
            summary = "Get posts by current user (paginated)",
            description = "Retrieves a paginated list of posts created by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of user's posts")
    @GetMapping("/user/paginated")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByCurrentUserPaginated(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort
    )

    @Operation(
            summary = "Get posts by user ID",
            description = "Retrieves all posts created by a specific user"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "List of user's posts"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/user/{id}")
    ResponseEntity<List<PostDto>> getAllPostsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get posts by user ID (paginated)",
            description = "Retrieves a paginated list of posts created by a specific user"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Paginated list of user's posts"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/user/{id}/paginated")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByUserIdPaginated(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort
    )

    @Operation(
            summary = "Get posts from following users",
            description = "Retrieves all posts from users that the current user is following"
    )
    @ApiResponse(responseCode = "200", description = "List of posts from following users")
    @GetMapping("/following")
    ResponseEntity<List<PostDto>> getAllPostsByFollowingUsers()

    @Operation(
            summary = "Get posts from following users (paginated)",
            description = "Retrieves a paginated list of posts from users that the current user is following"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of posts from following users")
    @GetMapping("/following/paginated")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByFollowingUsersPaginated(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort
    )

    @Operation(
            summary = "Update post",
            description = "Updates an existing post"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this post")
    ])
    @PutMapping("/{id}")
    ResponseEntity<PostDto> updatePost(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id,
            @RequestBody PostUpdateRequestDto postUpdateRequestDto
    )

    @Operation(
            summary = "Delete post",
            description = "Soft deletes a post by ID"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this post")
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePostById(
            @Parameter(description = "Post ID", required = true) @PathVariable(name = "id") String id
    )
}

