package pharmasave.security;

import pharmasave.jwt.JwtAuthenticationFilter;
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

                        // ==========================
                        // Public APIs
                        // ==========================
                        .requestMatchers("/api/auth/**").permitAll()

                        // Week 3 Product Evaluation
                        .requestMatchers("/api/evaluation/**").permitAll()

                        // ==========================
                        // Week 4 APIs
                        // ==========================

                        // User Interaction Tracking
                        .requestMatchers("/api/interactions/**")
                        .hasAnyRole("ADMIN", "PHARMACIST", "CASHIER", "INVENTORY_MANAGER")

                        // Personalized Recommendation
                        .requestMatchers("/api/recommendations/**")
                        .hasAnyRole("ADMIN", "PHARMACIST", "CASHIER", "INVENTORY_MANAGER")

                        // ==========================
                        // Admin APIs
                        // ==========================
                        .requestMatchers("/api/categories/**")
                        .hasRole("ADMIN")

                        // ==========================
                        // Pharmacist APIs
                        // ==========================
                        .requestMatchers("/api/medicines/**")
                        .hasAnyRole("ADMIN", "PHARMACIST")

                        // ==========================
                        // Cashier APIs
                        // ==========================
                        .requestMatchers("/api/sales/**")
                        .hasAnyRole("ADMIN", "CASHIER")

                        // ==========================
                        // Inventory APIs
                        // ==========================
                        .requestMatchers("/api/purchases/**")
                        .hasAnyRole("ADMIN", "INVENTORY_MANAGER")

                        // ==========================
                        // All Remaining APIs
                        // ==========================
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