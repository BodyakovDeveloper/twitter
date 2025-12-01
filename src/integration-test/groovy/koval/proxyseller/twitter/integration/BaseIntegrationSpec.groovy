package koval.proxyseller.twitter.integration

import koval.proxyseller.twitter.config.TestContainersConfig
import koval.proxyseller.twitter.model.user.User
import koval.proxyseller.twitter.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = [TestContainersConfig])
@TestPropertySource(locations = "classpath:application-test.yml")
abstract class BaseIntegrationSpec extends Specification {

    @Autowired
    UserRepository userRepository

    def setup() {
        // Clear security context before each test
        SecurityContextHolder.clearContext()
    }

    def cleanup() {
        // Clear security context after each test
        SecurityContextHolder.clearContext()
    }

    /**
     * Helper method to set up authenticated user for tests
     */
    protected void setupAuthenticatedUser(String username) {
        def user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow { new RuntimeException("User not found: ${username}") }
        
        def authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        )
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    /**
     * Helper method to create a test user and authenticate
     * Note: Use UserService.registerUser() for proper password hashing
     */
    protected User createAndAuthenticateUser(String username, String email) {
        // This method is kept for compatibility but should use UserService for real scenarios
        def user = new User()
        user.firstName = "Test"
        user.lastName = "User"
        user.username = username
        user.email = email
        user.password = "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5" // BCrypt hash placeholder
        user.age = 25
        user.role = koval.proxyseller.twitter.model.enumeration.Role.ROLE_USER
        def savedUser = userRepository.save(user)
        
        setupAuthenticatedUser(username)
        return savedUser
    }
}

