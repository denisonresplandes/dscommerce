package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordHashTest {

    private static final String ARGON_HASH =
            "$argon2id$v=19$m=16384,t=2,p=1$e2/Vof4sdyYV9DadKYgE2Q$OwD7aYMKPCfevm7fgxmIjc0pDcb0dRHoBb1pHx9YU28";

    @Test
    @DisplayName("aceita um hash Argon2")
    void shouldAcceptArgon2Hash() {
        assertThat(new PasswordHash(ARGON_HASH).value()).isEqualTo(ARGON_HASH);
    }

    @Test
    @DisplayName("rejeita senha em texto puro (esqueceu de gerar o hash)")
    void shouldRejectPlainTextPassword() {
        assertThatThrownBy(() -> new PasswordHash("secret123"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not a valid hash");
    }

    @Test
    @DisplayName("rejeita vazio e nulo")
    void shouldRejectBlankAndNull() {
        assertThatThrownBy(() -> new PasswordHash("  "))
                .isInstanceOf(DomainException.class)
                .hasMessage("user password hash cannot be blank.");
        assertThatThrownBy(() -> new PasswordHash(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user password hash is required");
    }

    @Test
    @DisplayName("toString nunca vaza o hash (logs e mensagens de erro)")
    void shouldMaskHashInToString() {
        PasswordHash hash = new PasswordHash(ARGON_HASH);

        assertThat(hash.toString()).isEqualTo("**********").doesNotContain("argon2");
    }
}
