package com.solutis.userservice.dto;

import com.solutis.userservice.entity.User;

import java.util.UUID;

public record ClientOptionResponse(
        UUID id,
        String name
) {

    public static ClientOptionResponse fromEntity(User user) {
        return new ClientOptionResponse(
                user.getId(),
                user.getName()
        );
    }
}