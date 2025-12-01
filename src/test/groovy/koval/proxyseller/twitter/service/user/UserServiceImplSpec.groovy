package koval.proxyseller.twitter.service.user


import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import koval.proxyseller.twitter.dto.user.UserUpdateRequestDto
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.exception.InvalidOperationException
import koval.proxyseller.twitter.exception.RegistrationException
import koval.proxyseller.twitter.mapper.user.UserMapper
import koval.proxyseller.twitter.model.user.User
import koval.proxyseller.twitter.monitoring.MetricsService
import koval.proxyseller.twitter.repository.user.UserRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.user.impl.UserServiceImpl
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class UserServiceImplSpec extends Specification {

    UserRepository userRepository = Mock()
    UserMapper userMapper = Mock()
    PasswordEncoder passwordEncoder = Mock()
    MetricsService metricsService = Mock()

    @Subject
    UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, metricsService)

    def "should register a new user successfully"() {
        given: "a valid registration request"
        def request = new UserRegistrationRequestDto(
                firstName: "John",
                lastName: "Doe",
                username: "johndoe",
                email: "john@example.com",
                password: "password123",
                age: 25
        )
        def user = new User()
        user.id = "user123"
        def userDto = new UserDto(id: "user123", username: "johndoe", email: "john@example.com")

        when: "registering the user"
        def result = userService.registerUser(request)

        then: "user should be registered"
        1 * userRepository.findByEmailAndIsDeletedFalse("john@example.com") >> Optional.empty()
        1 * passwordEncoder.encode("password123") >> "encodedPassword"
        1 * userRepository.save(_) >> { User u -> user }
        1 * userMapper.toDto(user) >> userDto
        1 * metricsService.incrementUserRegistrations()
        result != null
        result.id == "user123"
    }

    def "should throw RegistrationException when email already exists"() {
        given: "a registration request with existing email"
        def request = new UserRegistrationRequestDto(
                firstName: "John",
                lastName: "Doe",
                username: "johndoe",
                email: "existing@example.com",
                password: "password123",
                age: 25
        )
        def existingUser = new User()

        when: "registering the user"
        userService.registerUser(request)

        then: "should throw RegistrationException"
        1 * userRepository.findByEmailAndIsDeletedFalse("existing@example.com") >> Optional.of(existingUser)
        thrown(RegistrationException)
    }

    def "should get user by id"() {
        given: "a user id"
        def userId = "user123"
        def user = new User(id: userId, username: "johndoe", email: "john@example.com")
        def userDto = new UserDto(id: userId, username: "johndoe", email: "john@example.com")

        when: "getting user by id"
        def result = userService.getUserById(userId)

        then: "user should be returned"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        1 * userMapper.toDto(user) >> userDto
        result != null
        result.id == userId
    }

    def "should throw EntityNotFoundException when user not found by id"() {
        given: "a non-existent user id"
        def userId = "nonexistent"

        when: "getting user by id"
        userService.getUserById(userId)

        then: "should throw EntityNotFoundException"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get user by username"() {
        given: "a username"
        def username = "johndoe"
        def user = new User(id: "user123", username: username, email: "john@example.com")
        def userDto = new UserDto(id: "user123", username: username, email: "john@example.com")

        when: "getting user by username"
        def result = userService.getUserByUsername(username)

        then: "user should be returned"
        1 * userRepository.findByUsernameAndIsDeletedFalse(username) >> Optional.of(user)
        1 * userMapper.toDto(user) >> userDto
        result != null
        result.username == username
    }

    def "should throw EntityNotFoundException when user not found by username"() {
        given: "a non-existent username"
        def username = "nonexistent"

        when: "getting user by username"
        userService.getUserByUsername(username)

        then: "should throw EntityNotFoundException"
        1 * userRepository.findByUsernameAndIsDeletedFalse(username) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get all users with pagination"() {
        given: "a pageable request"
        def pageable = PageRequest.of(0, 10)
        def users = [new User(id: "1"), new User(id: "2")]
        def userDtos = [new UserDto(id: "1"), new UserDto(id: "2")]
        def page = new PageImpl<>(users, pageable, 2)

        when: "getting all users"
        def result = userService.getAllUsers(pageable)

        then: "paginated users should be returned"
        1 * userRepository.findAllByIsDeletedFalse(pageable) >> page
        2 * userMapper.toDto(_) >> { User u -> userDtos[users.indexOf(u)] }
        result != null
        result.content.size() == 2
        result.totalElements == 2
    }

    def "should update user successfully"() {
        given: "a user id and update request"
        def userId = "user123"
        def existingUser = new User(
                id: userId,
                username: "oldusername",
                email: "old@example.com",
                followers: ["follower1"] as Set,
                following: ["following1"] as Set
        )
        def updateRequest = new UserUpdateRequestDto(
                firstName: "Updated",
                lastName: "Name",
                username: "newusername",
                email: "new@example.com",
                age: 30
        )
        def updatedUser = new User(id: userId)
        def userDto = new UserDto(id: userId, username: "newusername", email: "new@example.com")

        when: "updating the user"
        def result = userService.updateUser(userId, updateRequest)

        then: "user should be updated"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(existingUser)
        1 * userMapper.toModel(updateRequest) >> updatedUser
        1 * userRepository.save(_) >> { User u ->
            assert u.id == userId
            assert u.followers == existingUser.followers
            assert u.following == existingUser.following
            updatedUser
        }
        1 * userMapper.toDto(updatedUser) >> userDto
        result != null
    }

    def "should throw EntityNotFoundException when updating non-existent user"() {
        given: "a non-existent user id"
        def userId = "nonexistent"
        def updateRequest = new UserUpdateRequestDto()

        when: "updating the user"
        userService.updateUser(userId, updateRequest)

        then: "should throw EntityNotFoundException"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should delete user by id"() {
        given: "a user id"
        def userId = "user123"
        def user = new User(id: userId, isDeleted: false)

        when: "deleting the user"
        userService.deleteUserById(userId)

        then: "user should be marked as deleted"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        1 * userRepository.save(_) >> { User u ->
            assert u.isDeleted == true
            u
        }
    }

    def "should throw EntityNotFoundException when deleting non-existent user"() {
        given: "a non-existent user id"
        def userId = "nonexistent"

        when: "deleting the user"
        userService.deleteUserById(userId)

        then: "should throw EntityNotFoundException"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get followers for current user"() {
        given: "current user with followers"
        def currentUserId = "user123"
        def currentUser = new User(id: currentUserId, followers: ["follower1", "follower2"] as Set)
        def followers = [
                new User(id: "follower1", username: "follower1"),
                new User(id: "follower2", username: "follower2")
        ]
        def followerDtos = [
                new UserDto(id: "follower1", username: "follower1"),
                new UserDto(id: "follower2", username: "follower2")
        ]

        when: "getting followers"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        def result = userService.getFollowers()

        then: "followers should be returned"
        1 * userRepository.findByIdAndIsDeletedFalse(currentUserId) >> Optional.of(currentUser)
        1 * userRepository.findAllByIdInAndIsDeletedFalse(_) >> followers
        2 * userMapper.toDto(_) >> { User u -> followerDtos.find { it.id == u.id } }
        result != null
        result.size() == 2
    }

    def "should get following for current user"() {
        given: "current user with following"
        def currentUserId = "user123"
        def currentUser = new User(id: currentUserId, following: ["following1", "following2"] as Set)
        def following = [
                new User(id: "following1", username: "following1"),
                new User(id: "following2", username: "following2")
        ]
        def followingDtos = [
                new UserDto(id: "following1", username: "following1"),
                new UserDto(id: "following2", username: "following2")
        ]

        when: "getting following"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        def result = userService.getFollowing()

        then: "following should be returned"
        1 * userRepository.findByIdAndIsDeletedFalse(currentUserId) >> Optional.of(currentUser)
        1 * userRepository.findAllByIdInAndIsDeletedFalse(_) >> following
        2 * userMapper.toDto(_) >> { User u -> followingDtos.find { it.id == u.id } }
        result != null
        result.size() == 2
    }

    def "should follow user successfully"() {
        given: "current user and user to follow"
        def currentUserId = "user1"
        def followingUserId = "user2"
        def currentUser = new User(id: currentUserId, following: [] as Set)
        def followingUser = new User(id: followingUserId, followers: [] as Set)

        when: "following the user"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        userService.followUser(followingUserId)

        then: "users should be updated"
        1 * userRepository.findByIdAndIsDeletedFalse(currentUserId) >> Optional.of(currentUser)
        1 * userRepository.findByIdAndIsDeletedFalse(followingUserId) >> Optional.of(followingUser)
        1 * userRepository.save(currentUser) >> { User u -> assert u.following.contains(followingUserId); u }
        1 * userRepository.save(followingUser) >> { User u -> assert u.followers.contains(currentUserId); u }
        1 * metricsService.incrementFollowOperations()
    }

    def "should throw InvalidOperationException when user tries to follow himself"() {
        given: "current user trying to follow himself"
        def userId = "user1"
        def user = new User(id: userId)

        when: "following himself"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        userService.followUser(userId)

        then: "should throw InvalidOperationException"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        thrown(InvalidOperationException)
    }

    def "should unfollow user successfully"() {
        given: "current user following another user"
        def currentUserId = "user1"
        def followingUserId = "user2"
        def currentUser = new User(id: currentUserId, following: [followingUserId] as Set)
        def followingUser = new User(id: followingUserId, followers: [currentUserId] as Set)

        when: "unfollowing the user"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        userService.unfollowUser(followingUserId)

        then: "users should be updated"
        1 * userRepository.findByIdAndIsDeletedFalse(currentUserId) >> Optional.of(currentUser)
        1 * userRepository.findByIdAndIsDeletedFalse(followingUserId) >> Optional.of(followingUser)
        1 * userRepository.save(currentUser) >> { User u -> assert !u.following.contains(followingUserId); u }
        1 * userRepository.save(followingUser) >> { User u -> assert !u.followers.contains(currentUserId); u }
        1 * metricsService.incrementUnfollowOperations()
    }

    def "should throw InvalidOperationException when user tries to unfollow himself"() {
        given: "current user trying to unfollow himself"
        def userId = "user1"
        def user = new User(id: userId)

        when: "unfollowing himself"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        userService.unfollowUser(userId)

        then: "should throw InvalidOperationException"
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        1 * userRepository.findByIdAndIsDeletedFalse(userId) >> Optional.of(user)
        thrown(InvalidOperationException)
    }
}

