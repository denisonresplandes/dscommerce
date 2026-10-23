package com.devsuperior.dscommerce.users.presentation.dto;

import com.devsuperior.dscommerce.users.application.command.CreateUserCommand;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Corpo JSON do {@code POST /users}.
 *
 * <p>As anotações validam a <b>forma</b> da entrada logo na borda (campo ausente, tamanho absurdo, formato óbvio
 * errado) e respondem 400 sem tocar na aplicação. As regras de <b>negócio</b> (ex.: nome sem símbolos, telefone
 * brasileiro de 11 dígitos) continuam nos value objects do domínio — a validação web é um filtro barato, não a
 * fonte da verdade.
 *
 * <h3>Limites de tamanho (bug 2.7)</h3>
 * {@code name} e {@code email} aceitam no máximo 100 caracteres e {@code phone} no máximo 20: exatamente o tamanho
 * das colunas de {@code tb_user}. Sem esses limites, um texto longo passava pela validação web e só estourava no
 * banco, devolvendo erro 500. Os números ficam escritos aqui (e não importados do domínio) porque a camada web não
 * pode depender do domínio; o teste {@code UserFieldLimitsTest} garante que os três lugares (Request, value object e
 * coluna) nunca saiam de sincronia.
 */
public record CreateUserRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        /*
         * Bug 2.6: antes este campo exigia "somente 11 dígitos" (@Pattern), enquanto o value object Phone aceita
         * qualquer formatação e a normaliza ("(11) 99999-8888" -> "11999998888"). As duas regras discordavam e a
         * do Request, mais rígida, tornava a normalização do domínio inútil.
         * Decisão: o Request só exige que o campo exista e caiba na coluna; QUEM DECIDE se o número é válido é o
         * domínio (11 dígitos), que também faz a normalização. Telefone inválido agora responde 422 com a
         * mensagem da regra, em vez de um 400 genérico.
         */
        @NotBlank
        @Size(max = 20)
        String phone,

        @NotNull
        @PastOrPresent
        LocalDate dateOfBirth,

        /*
         * Bug 2.8: a regra antiga era min = 6 e max = 20. O mínimo era fraco e o máximo, sem sentido: a senha é
         * transformada em hash (Argon2), então o tamanho do que o usuário digita não afeta o armazenamento, e
         * limitar a 20 caracteres impede frases-senha longas, que são mais seguras. Agora: mínimo 8; máximo 128,
         * apenas para não aceitar entradas gigantes que só serviriam para sobrecarregar o servidor.
         */
        @NotBlank
        @Size(min = 8, max = 128)
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
