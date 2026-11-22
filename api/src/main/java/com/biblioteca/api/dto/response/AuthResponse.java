package com.biblioteca.api.dto.response;


public record AuthResponse(String accessToken, UserResponse user, String message) {
}