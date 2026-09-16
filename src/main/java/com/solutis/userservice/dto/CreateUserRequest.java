package com.solutis.userservice.dto;

import com.solutis.userservice.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "Email deve possuir no máximo 150 caracteres")
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotNull(message = "Role é obrigatória")
        Role role

) {
}