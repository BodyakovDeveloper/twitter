package koval.proxyseller.twitter.controller.user

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserLoginRequestDto
import koval.proxyseller.twitter.dto.user.UserLoginResponseDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import koval.proxyseller.twitter.security.AuthenticationService
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class AuthenticationController implements AuthenticationControllerApi {
    private final UserService userService
    private final AuthenticationService authenticationService

    AuthenticationController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService
        this.authenticationService = authenticationService
    }

    @Override
    ResponseEntity<UserDto> registerUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        log.info("AuthenticationController: Registering user")
        UserDto userDto = userService.registerUser(userRegistrationRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto)
    }

    @Override
    ResponseEntity<UserLoginResponseDto> login(UserLoginRequestDto requestDto) {
        log.info("AuthenticationController: User login")
        UserLoginResponseDto response = authenticationService.authenticate(requestDto)
        return ResponseEntity.ok(response)
    }
}
