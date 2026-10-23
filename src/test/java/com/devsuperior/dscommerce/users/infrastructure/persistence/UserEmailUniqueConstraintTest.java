package com.devsuperior.dscommerce.users.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prova que o <b>banco</b> recusa e-mails duplicados, independentemente do que a aplicação verifique antes.
 *
 * <p><b>Por que este teste é necessário?</b> A checagem {@code existsByEmail} do service não basta: com duas
 * requisições simultâneas, ambas podem perguntar "este e-mail existe?", ambas ouvir "não", e ambas tentar gravar.
 * Só a restrição UNIQUE do banco decide essa corrida com segurança. Este teste grava <i>direto</i> pelo repositório
 * Spring Data — pulando de propósito a verificação do service — para exercitar essa última linha de defesa.
 *
 * <p>Fica no mesmo pacote de {@code SpringDataUserRepository} porque ele é package-private (detalhe interno da
 * infraestrutura): um teste no mesmo pacote consegue enxergá-lo, mas o resto da aplicação não.
 */
@SpringBootTest
class UserEmailUniqueConstraintTest {

    @Autowired
    private SpringDataUserRepository repository;

    @Test
    @DisplayName("gravar um segundo usuário com e-mail existente lança DataIntegrityViolationException")
    void shouldRejectDuplicateEmailAtDatabaseLevel() {
        // "maria@gmail.com" já existe: veio do import.sql.
        UserEntity duplicate = new UserEntity(
                null, "Outra Pessoa", "maria@gmail.com", "11999998888",
                LocalDate.of(1990, 1, 1), "$argon2id$hash-qualquer");

        // saveAndFlush força o INSERT imediatamente (sem ele, o Hibernate poderia adiar a gravação).
        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("existsByEmail encontra o e-mail cadastrado e não encontra um desconhecido")
    void shouldFindExistingEmail() {
        org.assertj.core.api.Assertions.assertThat(repository.existsByEmail("maria@gmail.com")).isTrue();
        org.assertj.core.api.Assertions.assertThat(repository.existsByEmail("ninguem@example.com")).isFalse();
    }
}
