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

@Slf4j
@Component
@Order(-2147483645) // HIGHEST_PRECEDENCE + 3
class PerformanceMonitoringFilter extends OncePerRequestFilter {

    private static final long SLOW_REQUEST_THRESHOLD = 1000 // 1 second
    private static final long VERY_SLOW_REQUEST_THRESHOLD = 5000 // 5 seconds

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis()
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        try {
            filterChain.doFilter(request, response)
        } finally {
            long duration = System.currentTimeMillis() - startTime
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            long memoryUsed = endMemory - startMemory

            String requestId = request.getAttribute("X-Request-ID")?.toString() ?: "Unknown"
            String uri = request.getRequestURI()
            String method = request.getMethod()

            // Add performance headers
            response.setHeader("X-Response-Time", "${duration}ms")
            response.setHeader("X-Memory-Used", "${memoryUsed / 1024}KB")

            // Log performance metrics
            if (duration > VERY_SLOW_REQUEST_THRESHOLD) {
                log.error("Performance Alert - VERY_SLOW_REQUEST | RequestID: ${requestId} | " +
                        "Method: ${method} | URI: ${uri} | Duration: ${duration}ms | Memory: ${memoryUsed / 1024}KB")
            } else if (duration > SLOW_REQUEST_THRESHOLD) {
                log.warn("Performance Alert - SLOW_REQUEST | RequestID: ${requestId} | " +
                        "Method: ${method} | URI: ${uri} | Duration: ${duration}ms | Memory: ${memoryUsed / 1024}KB")
            } else if (log.isDebugEnabled()) {
                log.debug("Performance Metrics | RequestID: ${requestId} | " +
                        "Method: ${method} | URI: ${uri} | Duration: ${duration}ms | Memory: ${memoryUsed / 1024}KB")
            }
        }
    }
}
