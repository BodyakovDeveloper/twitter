package koval.proxyseller.twitter.controller.user

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserUpdateRequestDto
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class UserController implements UserControllerApi {
    private final UserService userService

    UserController(UserService userService) {
        this.userService = userService
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<UserDto> getUserById(String id) {
        log.info("UserController: Getting user by id: ${id}")
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<UserDto> getUserByUsername(String username) {
        log.info("UserController: Getting user by username: ${username}")
        return ResponseEntity.ok(userService.getUserByUsername(username))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("UserController: Getting all users")
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<UserDto>> getAllUsersPaginated(int page, int size, String sort) {
        log.info("UserController: Getting all users with pagination: page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(userService.getAllUsers(pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<UserDto> updateUser(String id, UserUpdateRequestDto userUpdateRequestDto) {
        log.info("UserController: Updating user with id: ${id}")
        return ResponseEntity.ok(userService.updateUser(id, userUpdateRequestDto))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> deleteUserById(String id) {
        log.info("UserController: Deleting user by id: ${id}")
        userService.deleteUserById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<UserDto>> getFollowers() {
        log.info("UserController: Getting followers for current user")
        return ResponseEntity.ok(userService.getFollowers())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<UserDto>> getFollowing() {
        log.info("UserController: Getting following for current user")
        return ResponseEntity.ok(userService.getFollowing())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> followUser(String id) {
        log.info("UserController: Following user with id: ${id}")
        userService.followUser(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> unfollowUser(String id) {
        log.info("UserController: Unfollowing user with id: ${id}")
        userService.unfollowUser(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
