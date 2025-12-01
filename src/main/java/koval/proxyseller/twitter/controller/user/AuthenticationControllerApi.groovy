package koval.proxyseller.twitter.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserLoginRequestDto
import koval.proxyseller.twitter.dto.user.UserLoginResponseDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Authentication", description = "Authentication and registration endpoints")
@RequestMapping("/api/v1/auth")
interface AuthenticationControllerApi {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided information"
    )
    @ApiResponses(value = [
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    ])
    @PostMapping("/registration")
    ResponseEntity<UserDto> registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto)

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses(value = [
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = UserLoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content
            )
    ])
    @PostMapping("/login")
    ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto)
}

