package com.solutis.userservice.service;

import com.solutis.userservice.dto.LoginRequest;
import com.solutis.userservice.dto.LoginResponse;
import com.solutis.userservice.entity.User;
import com.solutis.userservice.exception.UserNotFoundException;
import com.solutis.userservice.repository.UserRepository;
import com.solutis.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Email ou senha inválidos"
                        )
                );

        if (!user.isActive()) {
            throw new BadCredentialsException(
                    "Usuário inativo"
            );
        }

        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(
                        request.password(),
                        user.getPasswordHash()
                )) {

            throw new BadCredentialsException(
                    "Email ou senha inválidos"
            );
        }

        String token = jwtService.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(token, "Bearer");
    }
}
