package com.devsuperior.dscommerce.users.presentation.dto;

import java.time.LocalDate;

/**
 * Representação de usuário devolvida pela API.
 *
 * <p>Substitui os antigos {@code UserResponse} (consultas) e {@code CreateUserResponse} (criação), que eram
 * idênticos. O JSON enviado ao cliente <b>não mudou</b>. Note que nunca há campo de senha: o DTO é o filtro final
 * do que sai pela API.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
