package koval.proxyseller.twitter.dto.user

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "User update request")
class UserUpdateRequestDto {
    @Schema(description = "User's first name", example = "John")
    String firstName

    @Schema(description = "User's last name", example = "Doe")
    String lastName

    @Schema(description = "Username", example = "johndoe")
    String username

    @Schema(description = "User email address", example = "john.doe@example.com")
    String email

    @Schema(description = "User password", example = "NewSecurePassword123!", minLength = 8)
    String password

    @Schema(description = "User age", example = "25", minimum = "18", maximum = "120")
    Integer age
}
