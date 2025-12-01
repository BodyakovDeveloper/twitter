package koval.proxyseller.twitter.security.filter

import groovy.util.logging.Slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID"
    private static final String MDC_REQUEST_ID_KEY = "requestId"
    private static final String MDC_USER_KEY = "username"

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER)
        if (!requestId) {
            requestId = UUID.randomUUID().toString()
        }

        response.setHeader(REQUEST_ID_HEADER, requestId)
        request.setAttribute(REQUEST_ID_HEADER, requestId)

        try {
            MDC.put(MDC_REQUEST_ID_KEY, requestId)
            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}

