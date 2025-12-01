package koval.proxyseller.twitter.security.filter

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Slf4j
@Component
@Order(-2147483646) // HIGHEST_PRECEDENCE + 2
class SecurityAuditFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader("X-Request-ID") ?: UUID.randomUUID().toString()
        request.setAttribute("X-Request-ID", requestId)
        response.setHeader("X-Request-ID", requestId)

        try {
            filterChain.doFilter(request, response)
        } finally {
            auditSecurityEvent(request, response, requestId)
        }
    }

    private void auditSecurityEvent(
            HttpServletRequest request,
            HttpServletResponse response,
            String requestId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        String method = request.getMethod()
        String uri = request.getRequestURI()
        int status = response.getStatus()
        String clientIp = getClientIp(request)

        // Log authentication events
        if (isAuthenticationEndpoint(uri)) {
            if (status == 200 || status == 201) {
                String username = authentication?.name ?: "Unknown"
                log.info("Security Audit - SUCCESSFUL_LOGIN | RequestID: ${requestId} | " +
                        "Username: ${username} | IP: ${clientIp} | URI: ${uri}")
            } else if (status == 401) {
                log.warn("Security Audit - FAILED_LOGIN | RequestID: ${requestId} | " +
                        "IP: ${clientIp} | URI: ${uri} | Reason: Invalid credentials")
            }
        }

        // Log authorization failures
        if (status == 403) {
            String username = authentication?.name ?: "Anonymous"
            log.warn("Security Audit - AUTHORIZATION_FAILED | RequestID: ${requestId} | " +
                    "Username: ${username} | IP: ${clientIp} | URI: ${uri} | Method: ${method}")
        }

        // Log authentication failures
        if (status == 401 && !isAuthenticationEndpoint(uri)) {
            log.warn("Security Audit - AUTHENTICATION_FAILED | RequestID: ${requestId} | " +
                    "IP: ${clientIp} | URI: ${uri} | Method: ${method} | Reason: Missing or invalid token")
        }

        // Log access to sensitive endpoints
        if (isSensitiveEndpoint(uri) && authentication?.authenticated) {
            String username = authentication.name
            log.info("Security Audit - SENSITIVE_ACCESS | RequestID: ${requestId} | " +
                    "Username: ${username} | IP: ${clientIp} | URI: ${uri} | Method: ${method}")
        }
    }

    private boolean isAuthenticationEndpoint(String uri) {
        return uri.startsWith("/api/v1/auth/")
    }

    private boolean isSensitiveEndpoint(String uri) {
        return uri.contains("/admin") ||
                uri.contains("/users") && (uri.contains("/delete") || uri.contains("/update")) ||
                uri.contains("/posts") && (uri.contains("/delete") || uri.contains("/update"))
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

