package koval.proxyseller.twitter.dto.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User login response")
record UserLoginResponseDto(
        @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
        String token
) {
}