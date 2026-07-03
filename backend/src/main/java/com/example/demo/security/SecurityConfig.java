package com.example.demo.security;

import com.example.demo.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

        // Public APIs
        .requestMatchers("/api/auth/**").permitAll()

        // ADMIN only
        .requestMatchers("/api/categories/**").hasRole("ADMIN")

        // ADMIN + PHARMACIST
        .requestMatchers("/api/medicines/**")
        .hasAnyRole("ADMIN", "PHARMACIST")

        // ADMIN + CASHIER
        .requestMatchers("/api/sales/**")
        .hasAnyRole("ADMIN", "CASHIER")

        // ADMIN + INVENTORY_MANAGER
        .requestMatchers("/api/purchases/**")
        .hasAnyRole("ADMIN", "INVENTORY_MANAGER")

        // Everything else requires login
        .anyRequest().authenticated()
)
                

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}