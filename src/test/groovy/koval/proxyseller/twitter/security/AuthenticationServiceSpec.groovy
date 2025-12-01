package koval.proxyseller.twitter.security

import koval.proxyseller.twitter.dto.user.UserLoginRequestDto
import koval.proxyseller.twitter.dto.user.UserLoginResponseDto
import koval.proxyseller.twitter.monitoring.MetricsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import spock.lang.Specification
import spock.lang.Subject

class AuthenticationServiceSpec extends Specification {

    JwtUtil jwtUtil = Mock()
    AuthenticationManager authenticationManager = Mock()
    MetricsService metricsService = Mock()

    @Subject
    AuthenticationService authenticationService = new AuthenticationService(jwtUtil, authenticationManager, metricsService)

    def "should authenticate user successfully"() {
        given: "valid login credentials"
        def request = new UserLoginRequestDto(username: "testuser", password: "password123")
        def authentication = Mock(Authentication)
        def token = "jwt.token.here"

        when: "authenticating"
        def result = authenticationService.authenticate(request)

        then: "should return JWT token"
        1 * authenticationManager.authenticate(_) >> authentication
        1 * authentication.getName() >> "testuser"
        1 * jwtUtil.generateToken("testuser") >> token
        1 * metricsService.incrementUserLogins()
        1 * metricsService.recordAuthenticationTime(_, _)
        result != null
        result.token() == token
    }

    def "should throw BadCredentialsException for invalid credentials"() {
        given: "invalid login credentials"
        def request = new UserLoginRequestDto(username: "testuser", password: "wrongpassword")

        when: "authenticating"
        authenticationService.authenticate(request)

        then: "should throw BadCredentialsException"
        1 * authenticationManager.authenticate(_) >> { throw new BadCredentialsException("Bad credentials") }
        1 * metricsService.recordAuthenticationTime(_, _)
        thrown(BadCredentialsException)
    }
}

