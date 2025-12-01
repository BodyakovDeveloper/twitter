package koval.proxyseller.twitter.security.interceptor

import groovy.util.logging.Slf4j
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Slf4j
@Component
class LoggingInterceptor implements HandlerInterceptor {

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getAttribute("X-Request-ID")?.toString() ?: UUID.randomUUID().toString()
        request.setAttribute("startTime", System.currentTimeMillis())
        request.setAttribute("X-Request-ID", requestId)

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        String username = authentication?.authenticated ? authentication.name : "Anonymous"

        if (log.isDebugEnabled()) {
            log.debug("Interceptor - Request started | RequestID: ${requestId} | " +
                    "Method: ${request.getMethod()} | URI: ${request.getRequestURI()} | " +
                    "User: ${username}")
        }

        return true
    }

    @Override
    void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        Long startTime = request.getAttribute("startTime") as Long
        if (startTime) {
            long duration = System.currentTimeMillis() - startTime
            String requestId = request.getAttribute("X-Request-ID")?.toString() ?: "Unknown"
            String method = request.getMethod()
            String uri = request.getRequestURI()
            int status = response.getStatus()

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
            String username = authentication?.authenticated ? authentication.name : "Anonymous"

            if (ex) {
                log.error("Interceptor - Request failed | RequestID: ${requestId} | " +
                        "Method: ${method} | URI: ${uri} | Status: ${status} | " +
                        "User: ${username} | Duration: ${duration}ms | Error: ${ex.message}", ex)
            } else if (log.isDebugEnabled()) {
                log.debug("Interceptor - Request completed | RequestID: ${requestId} | " +
                        "Method: ${method} | URI: ${uri} | Status: ${status} | " +
                        "User: ${username} | Duration: ${duration}ms")
            }
        }
    }
}

