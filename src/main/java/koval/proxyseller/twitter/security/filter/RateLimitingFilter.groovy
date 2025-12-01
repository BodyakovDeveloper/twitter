package koval.proxyseller.twitter.security.filter

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import koval.proxyseller.twitter.exception.AuthenticationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
@Component
@Order(-2147483644) // HIGHEST_PRECEDENCE + 4
class RateLimitingFilter extends OncePerRequestFilter {

    @Value('${rate-limiting.max-requests-per-minute:100}')
    private int maxRequestsPerMinute

    @Value('${rate-limiting.max-requests-per-hour:1000}')
    private int maxRequestsPerHour

    private static final long MINUTE_WINDOW = 60 * 1000 // 1 minute
    private static final long HOUR_WINDOW = 60 * 60 * 1000 // 1 hour

    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>()

    @Value('${rate-limiting.excluded-paths:/actuator/health,/actuator/info}')
    private String excludedPathsString

    private List<String> getExcludedPaths() {
        return Arrays.asList(excludedPathsString.split(","))
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (shouldApplyRateLimit(request)) {
            String clientId = getClientId(request)
            RequestCounter counter = requestCounters.computeIfAbsent(clientId, { new RequestCounter() })

            // Check minute limit
            if (counter.getMinuteCount() >= maxRequestsPerMinute) {
                log.warn("Rate limit exceeded (per minute) - Client: ${clientId} | IP: ${getClientIp(request)}")
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS)
                response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute))
                response.setHeader("X-RateLimit-Remaining", "0")
                response.setHeader("Retry-After", "60")
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}")
                return
            }

            // Check hour limit
            if (counter.getHourCount() >= maxRequestsPerHour) {
                log.warn("Rate limit exceeded (per hour) - Client: ${clientId} | IP: ${getClientIp(request)}")
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS)
                response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerHour))
                response.setHeader("X-RateLimit-Remaining", "0")
                response.setHeader("Retry-After", "3600")
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}")
                return
            }

            // Increment counters
            counter.increment()

            // Set rate limit headers
            response.setHeader("X-RateLimit-Limit-Minute", String.valueOf(maxRequestsPerMinute))
            response.setHeader("X-RateLimit-Remaining-Minute", String.valueOf(maxRequestsPerMinute - counter.getMinuteCount()))
            response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(maxRequestsPerHour))
            response.setHeader("X-RateLimit-Remaining-Hour", String.valueOf(maxRequestsPerHour - counter.getHourCount()))
        }

        filterChain.doFilter(request, response)
    }

    private boolean shouldApplyRateLimit(HttpServletRequest request) {
        String path = request.getRequestURI()
        return !getExcludedPaths().any { path.startsWith(it.trim()) }
    }

    private String getClientId(HttpServletRequest request) {
        // Try to get authenticated user first
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
        if (auth?.authenticated && auth.name) {
            return "user:${auth.name}"
        }
        // Fallback to IP address
        return "ip:${getClientIp(request)}"
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

    private static class RequestCounter {
        private final AtomicInteger minuteCount = new AtomicInteger(0)
        private final AtomicInteger hourCount = new AtomicInteger(0)
        private long minuteWindowStart = System.currentTimeMillis()
        private long hourWindowStart = System.currentTimeMillis()

        void increment() {
            long now = System.currentTimeMillis()

            // Reset minute counter if window expired
            if (now - minuteWindowStart > MINUTE_WINDOW) {
                minuteCount.set(0)
                minuteWindowStart = now
            }

            // Reset hour counter if window expired
            if (now - hourWindowStart > HOUR_WINDOW) {
                hourCount.set(0)
                hourWindowStart = now
            }

            minuteCount.incrementAndGet()
            hourCount.incrementAndGet()
        }

        int getMinuteCount() {
            long now = System.currentTimeMillis()
            if (now - minuteWindowStart > MINUTE_WINDOW) {
                minuteCount.set(0)
                minuteWindowStart = now
            }
            return minuteCount.get()
        }

        int getHourCount() {
            long now = System.currentTimeMillis()
            if (now - hourWindowStart > HOUR_WINDOW) {
                hourCount.set(0)
                hourWindowStart = now
            }
            return hourCount.get()
        }
    }
}
