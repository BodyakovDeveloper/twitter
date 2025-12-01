package koval.proxyseller.twitter.dto.user

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

import java.time.Instant

@TupleConstructor
@Schema(description = "User data transfer object")
class UserDto {
    @Schema(description = "User unique identifier", example = "507f1f77bcf86cd799439011")
    String id

    @Schema(description = "Username", example = "johndoe")
    String username

    @Schema(description = "User email address", example = "john.doe@example.com")
    String email

    @Schema(description = "User creation timestamp", example = "2024-01-01T12:00:00Z")
    Instant createdAt

    @Schema(description = "User last update timestamp", example = "2024-01-01T12:00:00Z")
    Instant updatedAt
}
