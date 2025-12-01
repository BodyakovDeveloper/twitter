package koval.proxyseller.twitter.integration

import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Shared

class UserServiceIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    UserService userService

    @Shared
    UserDto createdUser

    def setupSpec() {
        // Testcontainers will be initialized automatically
    }

    def cleanupSpec() {
        // Cleanup if needed
    }

    def "should register a new user"() {
        given: "a user registration request"
        def registrationRequest = new UserRegistrationRequestDto(
                firstName: "John",
                lastName: "Doe",
                username: "johndoe",
                email: "john.doe@example.com",
                password: "SecurePassword123!",
                age: 25
        )

        when: "registering the user"
        createdUser = userService.registerUser(registrationRequest)

        then: "user should be created successfully"
        createdUser != null
        createdUser.id != null
        createdUser.username == "johndoe"
        createdUser.email == "john.doe@example.com"
    }

    def "should find user by id"() {
        given: "a registered user"
        def registrationRequest = new UserRegistrationRequestDto(
                firstName: "Jane",
                lastName: "Smith",
                username: "janesmith",
                email: "jane.smith@example.com",
                password: "SecurePassword123!",
                age: 30
        )
        def user = userService.registerUser(registrationRequest)

        when: "finding user by id"
        def foundUser = userService.getUserById(user.id)

        then: "user should be found"
        foundUser != null
        foundUser.id == user.id
        foundUser.username == "janesmith"
    }

    def "should find user by username"() {
        given: "a registered user"
        def registrationRequest = new UserRegistrationRequestDto(
                firstName: "Bob",
                lastName: "Johnson",
                username: "bobjohnson",
                email: "bob.johnson@example.com",
                password: "SecurePassword123!",
                age: 28
        )
        userService.registerUser(registrationRequest)

        when: "finding user by username"
        def foundUser = userService.getUserByUsername("bobjohnson")

        then: "user should be found"
        foundUser != null
        foundUser.username == "bobjohnson"
    }

    def "should get all users with pagination"() {
        given: "multiple registered users"
        (1..5).each { i ->
            def request = new UserRegistrationRequestDto(
                    firstName: "User$i",
                    lastName: "Test",
                    username: "user$i",
                    email: "user$i@example.com",
                    password: "SecurePassword123!",
                    age: 20 + i
            )
            userService.registerUser(request)
        }

        when: "getting all users with pagination"
        def pageable = org.springframework.data.domain.PageRequest.of(0, 10)
        def result = userService.getAllUsers(pageable)

        then: "users should be returned"
        result != null
        result.content.size() >= 5
        result.totalElements >= 5
    }
}

