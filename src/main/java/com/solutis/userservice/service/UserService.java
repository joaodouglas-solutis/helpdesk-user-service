package com.solutis.userservice.service;


import com.solutis.userservice.dto.CreateUserRequest;
import com.solutis.userservice.dto.UserResponse;
import com.solutis.userservice.entity.User;
import com.solutis.userservice.exception.EmailAlreadyExistsException;
import com.solutis.userservice.exception.UserNotFoundException;
import com.solutis.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Email já cadastrado: " + request.email()
            );
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário não encontrado: " + id)
                );

        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional
    public UserResponse update(UUID id, CreateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário não encontrado: " + id)
                );

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {

            throw new EmailAlreadyExistsException(
                    "Email já cadastrado: " + request.email()
            );
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deactivate(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário não encontrado: " + id)
                );

        user.setActive(false);

        userRepository.save(user);
    }
}