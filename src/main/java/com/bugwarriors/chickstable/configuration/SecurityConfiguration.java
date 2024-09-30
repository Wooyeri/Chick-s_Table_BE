package com.bugwarriors.chickstable.configuration;

import com.bugwarriors.chickstable.security.CustomAuthenticationFailureHandler;
import com.bugwarriors.chickstable.security.CustomAuthenticationSuccessHandler;
import com.bugwarriors.chickstable.security.JwtRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	private CustomAuthenticationSuccessHandler successHandler;

	@Autowired
	private CustomAuthenticationFailureHandler failureHandler;

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				(auth) -> auth
						.requestMatchers(
						"/login", "/join")
						.permitAll()

						.anyRequest().authenticated());
		http.formLogin((auth) -> auth.loginProcessingUrl("/login").permitAll()
				.successHandler(successHandler)
				.failureHandler(failureHandler));

		http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(List.of("*"));
			configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
			configuration.setAllowedHeaders(List.of("*"));
			configuration.setExposedHeaders(List.of("token", "id"));
			return configuration;
		}));

		http.csrf(AbstractHttpConfigurer::disable);
		
		http.httpBasic(AbstractHttpConfigurer::disable);

		http.sessionManagement(
				(sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// http.logout((auth) -> auth.logoutUrl("/logout").logoutSuccessUrl("/"));

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}