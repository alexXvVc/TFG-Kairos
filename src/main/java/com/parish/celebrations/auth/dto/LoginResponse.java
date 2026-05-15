package com.parish.celebrations.auth.dto;

public record LoginResponse(String token, long expiresInMs, String role) {}
