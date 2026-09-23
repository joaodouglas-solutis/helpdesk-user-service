package com.solutis.userservice.controller;

import com.solutis.userservice.dto.ChangePasswordRequest;
import com.solutis.userservice.dto.ClientOptionResponse;
import com.solutis.userservice.dto.CreateUserRequest;
import com.solutis.userservice.dto.RegisterUserRequest;
import com.solutis.userservice.dto.UserResponse;
import com.solutis.userservice.dto.UserSummaryResponse;
import com.solutis.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request
    ) {

        UserResponse response =
                userService.create(request);

        URI location =
                URI.create(
                        "/users/" + response.id()
                );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {

        UserResponse response =
                userService.register(request);

        URI location =
                URI.create(
                        "/users/" + response.id()
                );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeOwnPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {

        UUID authenticatedUserId =
                UUID.fromString(
                        authentication.getName()
                );

        userService.changeOwnPassword(
                authenticatedUserId,
                request
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientOptionResponse>>
    findActiveClients() {

        return ResponseEntity.ok(
                userService.findActiveClients()
        );
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<UserSummaryResponse>
    findSummaryById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                userService.findSummaryById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>>
    findAll() {

        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                userService.findById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request
    ) {

        return ResponseEntity.ok(
                userService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id
    ) {

        userService.deactivate(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}