package com.devsuperior.dscommerce.users.presentation.dto;

import com.devsuperior.dscommerce.users.application.command.CreateUserCommand;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Corpo JSON do {@code POST /users}.
 *
 * <p>As anotações validam a <b>forma</b> da entrada logo na borda (campo ausente, formato óbvio errado) e
 * respondem 400 sem tocar na aplicação. As regras de <b>negócio</b> (ex.: idade, telefone brasileiro) continuam nos
 * value objects do domínio — a validação web é um filtro barato, não a fonte da verdade.
 */
public record CreateUserRequest(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^\\d{11}$",
                message = "O campo deve conter 11 dígitos"
        )
        String phone,

        @NotNull
        @PastOrPresent
        LocalDate dateOfBirth,

        @NotBlank
        @Size(min = 6, max = 20)
        String password
) {

    /**
     * Converte o DTO web no comando da aplicação. Mantido aqui (e não em um mapper) por decisão anterior do
     * projeto: o request conhece o comando, mas a aplicação nunca conhece o request.
     */
    public CreateUserCommand toCommand() {
        return new CreateUserCommand(name, email, phone, dateOfBirth, password);
    }
}
