package koval.proxyseller.twitter.controller.user

import com.fasterxml.jackson.databind.ObjectMapper
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserLoginRequestDto
import koval.proxyseller.twitter.dto.user.UserLoginResponseDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import koval.proxyseller.twitter.security.AuthenticationService
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AuthenticationControllerSpec extends Specification {

    UserService userService = Mock()
    AuthenticationService authenticationService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    AuthenticationController controller = new AuthenticationController(userService, authenticationService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should register a new user"() {
        given: "a registration request"
        def request = new UserRegistrationRequestDto(
                firstName: "John",
                lastName: "Doe",
                username: "johndoe",
                email: "john@example.com",
                password: "password123",
                age: 25
        )
        def userDto = new UserDto(id: "user123", username: "johndoe", email: "john@example.com")

        when: "registering user"
        def result = mockMvc.perform(post("/api/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "user should be created"
        1 * userService.registerUser(_) >> userDto
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').value("user123"))
                .andExpect(jsonPath('$.username').value("johndoe"))
    }

    def "should login user successfully"() {
        given: "login credentials"
        def request = new UserLoginRequestDto(username: "johndoe", password: "password123")
        def response = new UserLoginResponseDto(token: "jwt.token.here")

        when: "logging in"
        def result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "should return JWT token"
        1 * authenticationService.authenticate(_) >> response
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.token').value("jwt.token.here"))
    }
}

