package com.solutis.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(
                max = 120,
                message = "Nome deve possuir no máximo 120 caracteres"
        )
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(
                max = 150,
                message = "Email deve possuir no máximo 150 caracteres"
        )
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(
                min = 8,
                max = 100,
                message = "Senha deve possuir entre 8 e 100 caracteres"
        )
        String password,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmPassword

) {
}