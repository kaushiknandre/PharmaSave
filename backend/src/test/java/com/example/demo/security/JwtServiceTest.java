package com.example.demo.security;

import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void signsExtractsAndRejectsTamperedTokens() {
        JwtService jwtService = new JwtService("test-secret-value-with-more-than-32-chars", 30);
        User user = User.builder()
                .id(7L)
                .username("demo")
                .email("demo@pharmasave.local")
                .fullName("Demo Customer")
                .password("hash")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        String token = jwtService.generateToken(user);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.extractUsername(token)).isEqualTo("demo");
        assertThat(jwtService.isValid(token, user)).isTrue();
        assertThat(jwtService.isValid(tampered, user)).isFalse();
    }
}
