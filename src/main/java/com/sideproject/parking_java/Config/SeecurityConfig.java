package com.sideproject.parking_java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.sideproject.parking_java.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SeecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
            .csrf((AbstractHttpConfigurer::disable))
			// .httpBasic(withDefaults())
			// .addFilterBefore(new LoggingBasicAuthFilter(), BasicAuthenticationFilter.class)			
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.GET,"/api/gps/**","/api/parkingLot").permitAll()
				.requestMatchers(HttpMethod.POST,"/api/member","/api/member/login").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/member/auth","/api/member/status").hasAuthority("user")
				.requestMatchers(HttpMethod.POST,"/api/parkingLot","/api/tappay").hasAuthority("user")
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
			.authenticationManager(authenticationManager());

		return http.build();
	}

    @Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsServiceImpl);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);

		return providerManager;
	}

	

    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		// return NoOpPasswordEncoder.getInstance();
	}

}
