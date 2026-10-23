package com.devsuperior.dscommerce.users.application.view;

import java.time.LocalDate;

/**
 * <i>Read model</i> de usuário: o que os casos de uso de consulta devolvem.
 *
 * <p><b>Por que não devolver o {@code User} (agregado)?</b>
 * <ul>
 *   <li>O agregado carrega regras e a senha (hash) — dados e comportamento que uma consulta não precisa.</li>
 *   <li>Montá-lo exige reconstruir e <b>revalidar todos os value objects</b> a cada linha lida. Numa listagem com
 *       milhares de usuários isso é custo puro: os dados já foram validados quando entraram.</li>
 * </ul>
 *
 * <p>Esta view é preenchida <b>direto pelo banco</b> (expressão {@code select new ...} no JPQL), sem passar pelo
 * domínio. É a parte "Query" do CQRS leve: escrita usa o agregado, leitura usa views.
 *
 * <p>Também não é o DTO da API: a camada {@code presentation} converte esta view em {@code UserResponse}.
 * Assim o contrato HTTP pode evoluir sem afetar a aplicação, e vice-versa.
 */
public record UserView(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
