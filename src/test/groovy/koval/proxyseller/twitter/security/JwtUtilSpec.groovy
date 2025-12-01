package koval.proxyseller.twitter.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import koval.proxyseller.twitter.exception.AuthenticationException
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.StandardCharsets
import java.security.Key

class JwtUtilSpec extends Specification {

    String secretString = "test-secret-key-for-testing-purposes-only-very-long-key-required-for-hmac-sha256"
    Key secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8))
    long expiration = 600000 // 10 minutes

    @Subject
    JwtUtil jwtUtil

    def setup() {
        jwtUtil = new JwtUtil(secretString)
        // Set expiration via reflection since it's private
        def field = JwtUtil.class.getDeclaredField("expiration")
        field.setAccessible(true)
        field.set(jwtUtil, expiration)
    }

    def "should generate a valid token"() {
        given: "a username"
        def username = "testuser"

        when: "generating a token"
        def token = jwtUtil.generateToken(username)

        then: "token should be generated"
        token != null
        token.length() > 0
    }

    def "should validate a valid token"() {
        given: "a valid token"
        def username = "testuser"
        def token = jwtUtil.generateToken(username)

        when: "validating the token"
        def isValid = jwtUtil.isValidToken(token)

        then: "token should be valid"
        isValid == true
    }

    def "should extract username from token"() {
        given: "a valid token"
        def username = "testuser"
        def token = jwtUtil.generateToken(username)

        when: "extracting username"
        def extractedUsername = jwtUtil.getUsername(token)

        then: "username should match"
        extractedUsername == username
    }

    def "should throw AuthenticationException for invalid token"() {
        given: "an invalid token"
        def invalidToken = "invalid.token.here"

        when: "validating the token"
        jwtUtil.isValidToken(invalidToken)

        then: "should throw AuthenticationException"
        thrown(AuthenticationException)
    }

    def "should throw AuthenticationException for expired token"() {
        given: "an expired token"
        def expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expired
                .signWith(secret)
                .compact()

        when: "validating the token"
        jwtUtil.isValidToken(expiredToken)

        then: "should throw AuthenticationException"
        thrown(AuthenticationException)
    }

    def "should throw AuthenticationException when extracting username from invalid token"() {
        given: "an invalid token"
        def invalidToken = "invalid.token.here"

        when: "extracting username"
        jwtUtil.getUsername(invalidToken)

        then: "should throw AuthenticationException"
        thrown(AuthenticationException)
    }
}

