package koval.proxyseller.twitter.controller.user

import com.fasterxml.jackson.databind.ObjectMapper
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserUpdateRequestDto
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserControllerSpec extends Specification {

    UserService userService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    UserController controller = new UserController(userService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should get user by id"() {
        given: "a user id"
        def userId = "user123"
        def userDto = new UserDto(id: userId, username: "johndoe", email: "john@example.com")

        when: "getting user by id"
        def result = mockMvc.perform(get("/api/v1/users/{id}", userId))

        then: "user should be returned"
        1 * userService.getUserById(userId) >> userDto
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value(userId))
    }

    def "should get user by username"() {
        given: "a username"
        def username = "johndoe"
        def userDto = new UserDto(id: "user123", username: username, email: "john@example.com")

        when: "getting user by username"
        def result = mockMvc.perform(get("/api/v1/users/username")
                .param("username", username))

        then: "user should be returned"
        1 * userService.getUserByUsername(username) >> userDto
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.username').value(username))
    }

    def "should get all users"() {
        given: "multiple users"
        def users = [
                new UserDto(id: "1", username: "user1"),
                new UserDto(id: "2", username: "user2")
        ]

        when: "getting all users"
        def result = mockMvc.perform(get("/api/v1/users"))

        then: "users should be returned"
        1 * userService.getAllUsers() >> users
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$.length()').value(2))
    }

    def "should update user"() {
        given: "a user id and update request"
        def userId = "user123"
        def updateRequest = new UserUpdateRequestDto(
                firstName: "Updated",
                lastName: "Name",
                username: "newusername",
                email: "new@example.com",
                age: 30
        )
        def updatedUser = new UserDto(id: userId, username: "newusername", email: "new@example.com")

        when: "updating user"
        def result = mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))

        then: "user should be updated"
        1 * userService.updateUser(userId, _) >> updatedUser
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.username').value("newusername"))
    }

    def "should delete user"() {
        given: "a user id"
        def userId = "user123"

        when: "deleting user"
        def result = mockMvc.perform(delete("/api/v1/users/{id}", userId))

        then: "user should be deleted"
        1 * userService.deleteUserById(userId)
        result.andExpect(status().isNoContent())
    }

    def "should get followers"() {
        given: "current user followers"
        def followers = [
                new UserDto(id: "1", username: "follower1"),
                new UserDto(id: "2", username: "follower2")
        ]

        when: "getting followers"
        def result = mockMvc.perform(get("/api/v1/users/followers"))

        then: "followers should be returned"
        1 * userService.getFollowers() >> followers
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$.length()').value(2))
    }

    def "should get following"() {
        given: "current user following"
        def following = [
                new UserDto(id: "1", username: "following1"),
                new UserDto(id: "2", username: "following2")
        ]

        when: "getting following"
        def result = mockMvc.perform(get("/api/v1/users/following"))

        then: "following should be returned"
        1 * userService.getFollowing() >> following
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
    }

    def "should follow user"() {
        given: "a user id to follow"
        def userId = "user123"

        when: "following user"
        def result = mockMvc.perform(post("/api/v1/users/follow/{id}", userId))

        then: "user should be followed"
        1 * userService.followUser(userId)
        result.andExpect(status().isNoContent())
    }

    def "should unfollow user"() {
        given: "a user id to unfollow"
        def userId = "user123"

        when: "unfollowing user"
        def result = mockMvc.perform(post("/api/v1/users/unfollow/{id}", userId))

        then: "user should be unfollowed"
        1 * userService.unfollowUser(userId)
        result.andExpect(status().isNoContent())
    }
}

