package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    /**
     * Regressão do bug 2.1: o VO aceitava qualquer texto começando com "$b$" (prefixo que não existe em nenhum
     * formato de hash real). Os dois exemplos abaixo precisam ser recusados:
     * <ul>
     *   <li>"$b$..." — o falso prefixo que o código antigo aceitava;</li>
     *   <li>um hash BCrypt de verdade ("$2a$...") — formato legítimo, mas que este sistema não gera nem sabe
     *       verificar (só Argon2), então aceitá-lo criaria um usuário que nunca conseguiria fazer login.</li>
     * </ul>
     */
    @ParameterizedTest(name = "rejeita \"{0}\"")
    @ValueSource(strings = {
            "$b$qualquer-texto-que-parece-hash",
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
    })
    @DisplayName("rejeita prefixos que não são Argon2 (inclui o falso '$b$' e o BCrypt real)")
    void shouldRejectNonArgon2Prefixes(String notArgon2) {
        assertThatThrownBy(() -> new PasswordHash(notArgon2))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not a valid hash");
    }

    @ParameterizedTest(name = "aceita \"{0}\"")
    @ValueSource(strings = {"$argon2id$v=19$m=16384,t=2,p=1$abc$def", "$argon2i$v=19$abc", "$argon2d$v=19$abc"})
    @DisplayName("aceita as três variantes do Argon2 (id, i e d)")
    void shouldAcceptAllArgon2Variants(String hash) {
        assertThat(new PasswordHash(hash).value()).isEqualTo(hash);
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
