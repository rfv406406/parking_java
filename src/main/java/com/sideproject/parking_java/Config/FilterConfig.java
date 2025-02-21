package com.sideproject.parking_java.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * Configuration class to register CustomFilter as a Spring Bean.
 */
@Configuration
public class FilterConfig {

    /**
     * Registers CustomFilter with the Spring context.
     * 
     * @return FilterRegistrationBean for CustomFilter
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistrationBean(JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        // Set the filter instance
        registrationBean.setFilter(jwtAuthenticationFilter);
        
        // Define URL patterns to which the filter should be applied
        registrationBean.addUrlPatterns("/api/member/auth");
        registrationBean.addUrlPatterns("/api/member/status");

        return registrationBean;
    }
}
