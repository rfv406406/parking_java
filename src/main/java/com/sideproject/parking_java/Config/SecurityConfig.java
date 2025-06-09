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
public class SecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
            .csrf(
				(AbstractHttpConfigurer::disable)
				)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.GET,"/css/**", "/js/**","/image/**").permitAll()
				.requestMatchers(HttpMethod.GET,"/", "/id", "/carPage", "/cashFlowRecord", "/depositPage", "/parkingLotPage", "/chatroom/**").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/gps/**","/api/parkingLot").permitAll()
				.requestMatchers(HttpMethod.POST,"/api/member","/api/member/login").permitAll()
				.requestMatchers("/parkingLot-websocket/**").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/member/auth","/api/member/status","/api/member/balanceStatus","/api/member/memberDetails","/api/car","/api/parkingLotUsage","/api/transactionRecords","/api/chatroom","/api/chatmessage/**").hasAuthority("user")
				.requestMatchers(HttpMethod.POST,"/api/parkingLot","/api/tappay","/api/car","/api/parkingLotUsage","/api/chatroom","/api/chatmessage").hasAuthority("user")
				.requestMatchers(HttpMethod.PUT,"/api/parkingLot/**","/api/member/memberDetails/**","/api/parkingLotUsage/**").hasAuthority("user")
				.requestMatchers(HttpMethod.DELETE,"/api/parkingLot/**","/api/car/**").hasAuthority("user")
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
			.authenticationManager(authenticationManager());

		return http.build();
	}

    @Bean
    AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsServiceImpl);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(true);

		return providerManager;
	}


    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		// return NoOpPasswordEncoder.getInstance();
	}

}
