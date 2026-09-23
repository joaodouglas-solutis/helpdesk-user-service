package com.solutis.userservice.dto;

import com.solutis.userservice.entity.Role;
import com.solutis.userservice.entity.User;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String name,
        Role role
) {

    public static UserSummaryResponse fromEntity(
            User user
    ) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getRole()
        );
    }
}