package koval.proxyseller.twitter.security

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.user.UserLoginRequestDto
import koval.proxyseller.twitter.dto.user.UserLoginResponseDto
import koval.proxyseller.twitter.monitoring.MetricsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

@Slf4j
@Service
class AuthenticationService {

    private final JwtUtil jwtUtil
    private final AuthenticationManager authenticationManager
    private final MetricsService metricsService

    AuthenticationService(JwtUtil jwtUtil, AuthenticationManager authenticationManager, MetricsService metricsService) {
        this.jwtUtil = jwtUtil
        this.authenticationManager = authenticationManager
        this.metricsService = metricsService
    }

    UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        long startTime = System.currentTimeMillis()
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            )

            String token = jwtUtil.generateToken(authentication.getName())
            metricsService.incrementUserLogins()
            return new UserLoginResponseDto(token)
        } finally {
            long duration = System.currentTimeMillis() - startTime
            metricsService.recordAuthenticationTime(duration, TimeUnit.MILLISECONDS)
        }
    }
}

