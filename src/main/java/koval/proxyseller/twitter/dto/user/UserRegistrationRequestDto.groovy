package koval.proxyseller.twitter.dto.user

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "User registration request")
class UserRegistrationRequestDto {
    @Schema(description = "User's first name", example = "John", required = true)
    String firstName

    @Schema(description = "User's last name", example = "Doe", required = true)
    String lastName

    @Schema(description = "Unique username", example = "johndoe", required = true)
    String username

    @Schema(description = "User password", example = "SecurePassword123!", required = true, minLength = 8)
    String password

    @Schema(description = "User email address", example = "john.doe@example.com", required = true)
    String email

    @Schema(description = "User age", example = "25", minimum = "18", maximum = "120")
    Integer age
}
