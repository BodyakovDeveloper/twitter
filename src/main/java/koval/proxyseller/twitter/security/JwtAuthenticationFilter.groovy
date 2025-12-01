package koval.proxyseller.twitter.security

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import koval.proxyseller.twitter.exception.AuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Slf4j
@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil
    private final UserDetailsService userDetailsService

    private static final String AUTHORIZATION_HEADER = "Authorization"
    private static final String BEARER_PREFIX = "Bearer "
    private static final Integer TOKEN_START_INDEX = 7

    JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil
        this.userDetailsService = userDetailsService
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getAttribute("X-Request-ID")?.toString() ?: "Unknown"
        String token = getToken(request)

        if (token) {
            try {
                if (jwtUtil.isValidToken(token)) {
                    String username = jwtUtil.getUsername(token)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username)
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    )
                    SecurityContextHolder.getContext().setAuthentication(authentication)

                    if (log.isDebugEnabled()) {
                        log.debug("JWT Authentication successful | RequestID: ${requestId} | Username: ${username}")
                    }
                } else {
                    log.warn("JWT Authentication failed - Invalid token | RequestID: ${requestId} | IP: ${getClientIp(request)}")
                }
            } catch (AuthenticationException e) {
                log.error("JWT Authentication error | RequestID: ${requestId} | Error: ${e.message}", e)
                SecurityContextHolder.clearContext()
            } catch (Exception e) {
                log.error("Unexpected error during JWT authentication | RequestID: ${requestId} | Error: ${e.message}", e)
                SecurityContextHolder.clearContext()
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("No JWT token found in request | RequestID: ${requestId} | URI: ${request.getRequestURI()}")
            }
        }

        filterChain.doFilter(request, response)
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(TOKEN_START_INDEX)
        }
        return null
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For")
        if (xForwardedFor) {
            return xForwardedFor.split(",")[0].trim()
        }
        String xRealIp = request.getHeader("X-Real-IP")
        if (xRealIp) {
            return xRealIp
        }
        return request.getRemoteAddr()
    }
}
