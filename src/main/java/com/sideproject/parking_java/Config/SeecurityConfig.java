package com.sideproject.parking_java.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sideproject.parking_java.Service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SeecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
            .csrf((AbstractHttpConfigurer::disable))
			.httpBasic(withDefaults())
			// .addFilterBefore(new LoggingBasicAuthFilter(), BasicAuthenticationFilter.class)
			.authenticationManager(authenticationManager())
			// 加入自訂的 LoggingBasicAuthFilter
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.POST,"/api/member").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/getLatAndLong/**").permitAll()
				.requestMatchers(HttpMethod.POST,"/api/member/auth").permitAll()
				.anyRequest().authenticated()
			);

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
