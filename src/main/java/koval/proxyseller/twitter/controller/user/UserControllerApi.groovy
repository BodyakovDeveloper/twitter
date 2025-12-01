package koval.proxyseller.twitter.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserUpdateRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Users", description = "User management endpoints")
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearer-jwt")
interface UserControllerApi {

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their unique identifier"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    ])
    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get user by username",
            description = "Retrieves a user by their username"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/username")
    ResponseEntity<UserDto> getUserByUsername(
            @Parameter(description = "Username", required = true) @RequestParam(name = "username") String username
    )

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users"
    )
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    ResponseEntity<List<UserDto>> getAllUsers()

    @Operation(
            summary = "Get all users (paginated)",
            description = "Retrieves a paginated list of all users"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of users")
    @GetMapping("/paginated")
    ResponseEntity<PageResponseDto<UserDto>> getAllUsersPaginated(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "username,asc") @RequestParam(defaultValue = "username,asc") String sort
    )

    @Operation(
            summary = "Update user",
            description = "Updates user information"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    @PutMapping("/{id}")
    ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id,
            @RequestBody UserUpdateRequestDto userUpdateRequestDto
    )

    @Operation(
            summary = "Delete user",
            description = "Soft deletes a user by ID"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUserById(
            @Parameter(description = "User ID", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Get followers",
            description = "Retrieves the list of users following the current user"
    )
    @ApiResponse(responseCode = "200", description = "List of followers")
    @GetMapping("/followers")
    ResponseEntity<List<UserDto>> getFollowers()

    @Operation(
            summary = "Get following",
            description = "Retrieves the list of users the current user is following"
    )
    @ApiResponse(responseCode = "200", description = "List of following users")
    @GetMapping("/following")
    ResponseEntity<List<UserDto>> getFollowing()

    @Operation(
            summary = "Follow user",
            description = "Follows a user by ID"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "Successfully followed user"),
            @ApiResponse(responseCode = "400", description = "Cannot follow yourself"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @PostMapping("/follow/{id}")
    ResponseEntity<Void> followUser(
            @Parameter(description = "User ID to follow", required = true) @PathVariable(name = "id") String id
    )

    @Operation(
            summary = "Unfollow user",
            description = "Unfollows a user by ID"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed user"),
            @ApiResponse(responseCode = "400", description = "Cannot unfollow yourself"),
            @ApiResponse(responseCode = "404", description = "User not found")
    ])
    @PostMapping("/unfollow/{id}")
    ResponseEntity<Void> unfollowUser(
            @Parameter(description = "User ID to unfollow", required = true) @PathVariable(name = "id") String id
    )
}

