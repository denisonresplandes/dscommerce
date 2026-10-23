package com.devsuperior.dscommerce.users;

import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.Name;
import com.devsuperior.dscommerce.users.infrastructure.persistence.UserEntity;
import com.devsuperior.dscommerce.users.presentation.dto.CreateUserRequest;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <b>Guarda de consistência</b> dos limites de tamanho (bug 2.7).
 *
 * <p>O mesmo limite — por exemplo, "nome tem no máximo 100 caracteres" — precisa existir em três lugares, cada um em
 * uma camada diferente que não pode importar a outra:
 * <ol>
 *   <li>o <b>Request</b> ({@code @Size(max)}) — filtro da borda web, responde 400;</li>
 *   <li>o <b>value object</b> ({@code Name.MAX_LENGTH}) — regra de domínio, responde 422;</li>
 *   <li>a <b>coluna do banco</b> ({@code @Column(length)}) — o limite físico real.</li>
 * </ol>
 * Se alguém aumentar a coluna e esquecer os outros (ou o contrário), o sistema voltaria a aceitar texto que o banco
 * recusa. Este teste lê as três anotações por <i>reflexão</i> e falha se os números divergirem — é um teste que
 * "vigia" o código, em vez de executá-lo.
 */
class UserFieldLimitsTest {

    private static int requestMax(String field) throws NoSuchFieldException {
        // Em um record, as anotações do componente são copiadas para o campo privado correspondente.
        return CreateUserRequest.class.getDeclaredField(field).getAnnotation(Size.class).max();
    }

    private static int columnLength(String field) throws NoSuchFieldException {
        return UserEntity.class.getDeclaredField(field).getAnnotation(Column.class).length();
    }

    @Test
    @DisplayName("nome: Request, value object e coluna usam o mesmo limite")
    void nameLimitsAreConsistent() throws Exception {
        assertThat(requestMax("name"))
                .isEqualTo(Name.MAX_LENGTH)
                .isEqualTo(columnLength("name"));
    }

    @Test
    @DisplayName("e-mail: Request, value object e coluna usam o mesmo limite")
    void emailLimitsAreConsistent() throws Exception {
        assertThat(requestMax("email"))
                .isEqualTo(Email.MAX_LENGTH)
                .isEqualTo(columnLength("email"));
    }

    @Test
    @DisplayName("telefone: o limite do Request cabe na coluna")
    void phoneLimitFitsColumn() throws Exception {
        // O Phone não tem constante própria (ele guarda sempre 11 dígitos); o que importa é o texto bruto aceito
        // pelo Request, que pode vir formatado, caber na coluna.
        assertThat(requestMax("phone")).isEqualTo(columnLength("phone"));
    }
}
