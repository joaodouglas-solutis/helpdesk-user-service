package com.solutis.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "A senha atual é obrigatória")
        String currentPassword,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(
                min = 8,
                message = "A nova senha deve possuir pelo menos 8 caracteres"
        )
        String newPassword

) {
}