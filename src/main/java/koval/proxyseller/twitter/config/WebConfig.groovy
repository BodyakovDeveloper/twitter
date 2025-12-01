package koval.proxyseller.twitter.config

import koval.proxyseller.twitter.security.interceptor.LoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor

    WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor
    }

    @Override
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/**")
    }
}

