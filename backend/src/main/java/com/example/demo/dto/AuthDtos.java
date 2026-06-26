package com.example.demo.dto;

import com.example.demo.entity.User;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(String fullName, String username, String email, String password, String phoneNumber) {
    }

    public record LoginRequest(String usernameOrEmail, String password) {
    }

    public record AuthResponse(String token, Long userId, String username, String fullName, User.Role role, long expiresAtEpochSeconds) {
    }
}
