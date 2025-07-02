package com.hostelhive.hostelhive.Config;

import com.hostelhive.hostelhive.Service.CustomUserDetailsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors().and()
        .csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll() // Allow registration and login
                .requestMatchers("/api/payments/**").hasAnyRole("STUDENT", "MANAGER", "ADMIN") // Payments accessible to all roles
                .requestMatchers("/api/students/**").hasRole("STUDENT") // Student-specific endpoints
                .requestMatchers("/api/manager/**").hasRole("MANAGER") // Manager-specific endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin-specific endpoints
                .requestMatchers("/api/hostels/**", "/api/bookings/**").permitAll() // Keep these open for now
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing (enable in production)
            .httpBasic(); // Use HTTP Basic auth for simplicity
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost","http://127.0.0.1:5500")); // Specify allowed origins
        configuration.setAllowedMethods(List.of("GET", "POST","PUT" ,"DELETE","OPTIONS")); // Include OPTIONS
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Allow cookies if needed
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
