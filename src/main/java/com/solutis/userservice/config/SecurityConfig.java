package com.solutis.userservice.config;

import com.solutis.userservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // Autenticação
                        .requestMatchers(
                                "/auth/**",
                                "/users/register"
                        )
                        .permitAll()

                        // Lista de clientes para técnicos e administradores
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/clients"
                        )
                        .hasAnyRole(
                                "TECHNICIAN",
                                "ADMIN"
                        )

                        // Resumo de usuário
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/*/summary"
                        )
                        .hasAnyRole(
                                "CLIENT",
                                "TECHNICIAN",
                                "ADMIN"
                        )

                        // Compatibilidade com chamadas internas do Ticket Service
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/**"
                        )
                        .permitAll()

                        // Qualquer usuário autenticado pode
                        // alterar a própria senha.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/me/password"
                        )
                        .hasAnyRole(
                                "CLIENT",
                                "TECHNICIAN",
                                "ADMIN"
                        )

                        // Criação administrativa de usuários
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users"
                        )
                        .hasRole("ADMIN")

                        // Atualização administrativa
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        // Inativação administrativa
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}