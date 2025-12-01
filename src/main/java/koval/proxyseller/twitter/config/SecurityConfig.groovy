package koval.proxyseller.twitter.config

import jakarta.servlet.http.HttpServletResponse
import koval.proxyseller.twitter.security.JwtAuthenticationFilter
import koval.proxyseller.twitter.security.filter.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
class SecurityConfig {

    private final UserDetailsService userDetailsService
    private final JwtAuthenticationFilter jwtAuthenticationFilter
    private final RequestIdFilter requestIdFilter
    private final RequestLoggingFilter requestLoggingFilter
    private final SecurityAuditFilter securityAuditFilter
    private final PerformanceMonitoringFilter performanceMonitoringFilter
    private final RateLimitingFilter rateLimitingFilter

    @Value('${security.password-encoder.bcrypt-strength:12}')
    private int bcryptStrength

    @Value('${security.cors.allowed-origins:http://localhost:3000,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:8080}')
    private String allowedOrigins

    @Value('${security.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}')
    private String allowedMethods

    @Value('${security.cors.allowed-headers:*}')
    private String allowedHeaders

    @Value('${security.cors.exposed-headers:X-Request-ID,X-Response-Time,X-Memory-Used,X-RateLimit-Limit-Minute,X-RateLimit-Remaining-Minute,X-RateLimit-Limit-Hour,X-RateLimit-Remaining-Hour}')
    private String exposedHeaders

    @Value('${security.cors.allow-credentials:true}')
    private boolean allowCredentials

    @Value('${security.cors.max-age:3600}')
    private long maxAge

    @Value('${security.public-endpoints:/api/v1/auth/**,/api/error,/swagger-ui/**,/swagger-ui.html,/v3/api-docs/**,/actuator/health,/actuator/info,/actuator/prometheus}')
    private String publicEndpoints

    SecurityConfig(
            UserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RequestIdFilter requestIdFilter,
            RequestLoggingFilter requestLoggingFilter,
            SecurityAuditFilter securityAuditFilter,
            PerformanceMonitoringFilter performanceMonitoringFilter,
            RateLimitingFilter rateLimitingFilter) {
        this.userDetailsService = userDetailsService
        this.jwtAuthenticationFilter = jwtAuthenticationFilter
        this.requestIdFilter = requestIdFilter
        this.requestLoggingFilter = requestLoggingFilter
        this.securityAuditFilter = securityAuditFilter
        this.performanceMonitoringFilter = performanceMonitoringFilter
        this.rateLimitingFilter = rateLimitingFilter
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] publicPaths = publicEndpoints.split(",")
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityAuditFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(performanceMonitoringFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            response.setContentType("application/json")
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}")
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                            response.setContentType("application/json")
                            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}")
                        })
                )
                .build()
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration()
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")))
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")))
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")))
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")))
        configuration.setAllowCredentials(allowCredentials)
        configuration.setMaxAge(maxAge)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager()
    }
}
