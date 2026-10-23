package com.devsuperior.dscommerce.users.application.command;

import java.time.LocalDate;

/**
 * Entrada do caso de uso "criar usuário".
 *
 * <p>É o contrato entre a camada web e a aplicação: a {@code presentation} converte o JSON recebido
 * ({@code CreateUserRequest}) neste comando, e o service nunca vê nada de HTTP. Como são tipos simples (String,
 * LocalDate), a validação "de verdade" acontece ao criar os value objects dentro do service/domínio.
 */
public record CreateUserCommand(
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String password
) {
}
