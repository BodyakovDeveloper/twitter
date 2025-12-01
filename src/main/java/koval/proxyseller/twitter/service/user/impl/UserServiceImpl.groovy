package koval.proxyseller.twitter.service.user.impl

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.PageResponseDto
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
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Slf4j
@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository
    private final UserMapper userMapper
    private final PasswordEncoder passwordEncoder
    private final MetricsService metricsService

    UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, MetricsService metricsService) {
        this.userRepository = userRepository
        this.userMapper = userMapper
        this.passwordEncoder = passwordEncoder
        this.metricsService = metricsService
    }

    @Override
    @CacheEvict(value = "users", key = "'all_'")
    UserDto registerUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        if (userRepository.findByEmailAndIsDeletedFalse(userRegistrationRequestDto.getEmail()).isPresent()) {
            log.warn("UserServiceImpl: User with such email already registered!")
            throw new RegistrationException("User with such email already registered!");
        }
        User user = new User();
        user.setFirstName(userRegistrationRequestDto.getFirstName());
        user.setLastName(userRegistrationRequestDto.getLastName());
        user.setUsername(userRegistrationRequestDto.getUsername());
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setAge(userRegistrationRequestDto.getAge());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));
        user.setCreatedAt(java.time.Instant.now());
        user.setUpdatedAt(java.time.Instant.now());

        log.info("UserServiceImpl: Registering new user: ${user}")
        UserDto savedUser = userMapper.toDto(userRepository.save(user))
        metricsService.incrementUserRegistrations()
        return savedUser
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    UserDto getUserById(String id) {
        log.info("UserServiceImpl: Getting user by id: ${id}")
        return userMapper.toDto(userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found!")))
    }

    @Override
    @Cacheable(value = "users", key = "'username_' + #username")
    UserDto getUserByUsername(String username) {
        log.info("UserServiceImpl: Getting user by username: ${username}")
        return userMapper.toDto(userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found!")))
    }

    @Override
    @Deprecated
    List<UserDto> getAllUsers() {
        log.warn("UserServiceImpl: getAllUsers() without pagination is deprecated. Use getAllUsers(Pageable) instead.")
        Pageable pageable = PageRequest.of(0, 100)
        return getAllUsers(pageable).getContent()
    }

    @Override
    @Cacheable(value = "users", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<UserDto> getAllUsers(Pageable pageable) {
        log.info("UserServiceImpl: Getting all users with pagination: page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<User> userPage = userRepository.findAllByIsDeletedFalse(pageable)
        List<UserDto> content = userPage.content.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList())

        return new PageResponseDto<>(
                content,
                userPage.number,
                userPage.size,
                userPage.totalElements,
                userPage.totalPages,
                userPage.first,
                userPage.last
        )
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    UserDto updateUser(String id, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        User updatedUser = userMapper.toModel(userUpdateRequestDto)
        updatedUser.setId(user.getId())
        updatedUser.setFollowers(user.getFollowers())
        updatedUser.setFollowing(user.getFollowing())
        updatedUser.setCreatedAt(user.getCreatedAt())
        updatedUser.setUpdatedAt(java.time.Instant.now())

        log.info("UserServiceImpl: Updating user with id: ${id}, request: ${userUpdateRequestDto}")
        UserDto updated = userMapper.toDto(userRepository.save(updatedUser))
        return updated
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    void deleteUserById(String id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        user.isDeleted = true

        log.info("UserServiceImpl: Deleting user with id: ${id}")
        userRepository.save(user)
    }

    @Override
    List<UserDto> getFollowers() {
        String currentUserId = SecurityUtil.getCurrentUserId()
        User user = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        log.info("UserServiceImpl: Getting followers for current user")
        Set<String> followersIds = user.getFollowers()
        return userRepository.findAllByIdInAndIsDeletedFalse(followersIds).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList())
    }

    @Override
    List<UserDto> getFollowing() {
        String currentUserId = SecurityUtil.getCurrentUserId()
        User user = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        log.info("UserServiceImpl: Getting following for current user")
        Set<String> followingIds = user.getFollowing()
        return userRepository.findAllByIdInAndIsDeletedFalse(followingIds).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList())
    }

    @Override
    void followUser(String id) {
        String currentUserId = SecurityUtil.getCurrentUserId()
        User currentUser = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        User followingUser = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        if (currentUserId.equals(id)) {
            log.warn("UserServiceImpl: User can't follow himself!")
            throw new InvalidOperationException("User can't follow himself!")
        }

        currentUser.followUser(id)
        followingUser.addFollower(currentUserId)

        log.info("UserServiceImpl: Following user with id: ${id}")
        userRepository.save(currentUser)
        userRepository.save(followingUser)
        metricsService.incrementFollowOperations()
    }

    @Override
    void unfollowUser(String string) {
        String currentUserId = SecurityUtil.getCurrentUserId()
        User currentUser = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        User followingUser = userRepository.findByIdAndIsDeletedFalse(string)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        if (currentUserId.equals(string)) {
            log.warn("UserServiceImpl: User can't unfollow yourself!")
            throw new InvalidOperationException("User can't unfollow yourself!")
        }

        currentUser.unfollowUser(string)
        followingUser.removeFollower(currentUserId)

        log.info("UserServiceImpl: Unfollowing user with id: ${string}")
        userRepository.save(currentUser)
        userRepository.save(followingUser)
        metricsService.incrementUnfollowOperations()
    }
}
