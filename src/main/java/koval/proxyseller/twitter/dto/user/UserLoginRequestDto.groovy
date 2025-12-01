package koval.proxyseller.twitter.dto.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User login request")
record UserLoginRequestDto(
        @Schema(description = "Username", example = "johndoe", required = true)
        String username,
        @Schema(description = "Password", example = "SecurePassword123!", required = true)
        String password
) {
}