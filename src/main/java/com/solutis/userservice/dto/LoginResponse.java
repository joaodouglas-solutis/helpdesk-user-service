package com.solutis.userservice.dto;

public record LoginResponse(
        String token,
        String tokenType
) {
}
