package koval.proxyseller.twitter.security.filter

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

import java.nio.charset.StandardCharsets

@Slf4j
@Component
@Order(-2147483647) // HIGHEST_PRECEDENCE + 1
class RequestLoggingFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATHS = [
            "/actuator/health",
            "/actuator/info"
    ]

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (shouldLog(request)) {
            long startTime = System.currentTimeMillis()
            String requestId = request.getHeader("X-Request-ID") ?: UUID.randomUUID().toString()

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request)
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response)

            try {
                filterChain.doFilter(wrappedRequest, wrappedResponse)
            } finally {
                long duration = System.currentTimeMillis() - startTime
                logRequest(wrappedRequest, wrappedResponse, requestId, duration)
                wrappedResponse.copyBodyToResponse()
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }

    private boolean shouldLog(HttpServletRequest request) {
        String path = request.getRequestURI()
        return !EXCLUDED_PATHS.any { path.startsWith(it) }
    }

    private void logRequest(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String requestId,
            long duration) {

        String method = request.getMethod()
        String uri = request.getRequestURI()
        String queryString = request.getQueryString()
        String fullUri = queryString ? "${uri}?${queryString}" : uri
        int status = response.getStatus()
        String clientIp = getClientIp(request)
        String userAgent = request.getHeader("User-Agent") ?: "Unknown"

        log.info("HTTP Request - ID: ${requestId} | Method: ${method} | URI: ${fullUri} | " +
                "Status: ${status} | Duration: ${duration}ms | IP: ${clientIp} | User-Agent: ${userAgent}")

        if (log.isDebugEnabled()) {
            String requestBody = getRequestBody(request)
            String responseBody = getResponseBody(response)

            if (requestBody) {
                log.debug("Request Body [${requestId}]: ${requestBody}")
            }
            if (responseBody) {
                log.debug("Response Body [${requestId}]: ${responseBody}")
            }
        }

        // Log warnings for slow requests
        if (duration > 1000) {
            log.warn("Slow request detected - ID: ${requestId} | URI: ${fullUri} | Duration: ${duration}ms")
        }

        // Log errors for failed requests
        if (status >= 400) {
            log.error("Failed request - ID: ${requestId} | URI: ${fullUri} | Status: ${status}")
        }
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

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray()
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8)
        }
        return null
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray()
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8)
        }
        return null
    }
}

