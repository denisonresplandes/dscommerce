package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes do agregado {@link User}: criação, reconstituição e alterações que preservam as invariantes.
 */
class UserTest {

    private static final String ARGON_HASH =
            "$argon2id$v=19$m=16384,t=2,p=1$e2/Vof4sdyYV9DadKYgE2Q$OwD7aYMKPCfevm7fgxmIjc0pDcb0dRHoBb1pHx9YU28";

    private PersonalInfo personalInfo() {
        return new PersonalInfo(new Name("Maria Brown"), new DateOfBirth(LocalDate.of(2001, 7, 25)));
    }

    private ContactsInfo contactsInfo() {
        return new ContactsInfo(new Email("maria@gmail.com"), new Phone("98888888888"));
    }

    private Credentials credentials() {
        return new Credentials(new PasswordHash(ARGON_HASH));
    }

    private User newUser() {
        return User.create(personalInfo(), contactsInfo(), credentials());
    }

    @Test
    @DisplayName("usuário novo ainda não tem id e expõe seus dados")
    void shouldCreateUserWithoutId() {
        User user = newUser();

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(new Name("Maria Brown"));
        assertThat(user.getEmail()).isEqualTo(new Email("maria@gmail.com"));
        assertThat(user.getPhone()).isEqualTo(new Phone("98888888888"));
        assertThat(user.getDateOfBirth().value()).isEqualTo(LocalDate.of(2001, 7, 25));
    }

    @Test
    @DisplayName("todas as partes do usuário são obrigatórias")
    void shouldRequireAllParts() {
        assertThatThrownBy(() -> User.create(null, contactsInfo(), credentials()))
                .isInstanceOf(DomainException.class).hasMessage("user personal info cannot be null");
        assertThatThrownBy(() -> User.create(personalInfo(), null, credentials()))
                .isInstanceOf(DomainException.class).hasMessage("user contact info cannot be null");
        assertThatThrownBy(() -> User.create(personalInfo(), contactsInfo(), null))
                .isInstanceOf(DomainException.class).hasMessage("user credentials cannot be null");
    }

    @Test
    @DisplayName("restore exige id")
    void shouldRequireIdWhenRestoring() {
        assertThatThrownBy(() -> User.restore(null, personalInfo(), contactsInfo(), credentials()))
                .isInstanceOf(DomainException.class)
                .hasMessage("User ID cannot be null.");

        User restored = User.restore(new UserId(5L), personalInfo(), contactsInfo(), credentials());
        assertThat(restored.getId()).isEqualTo(new UserId(5L));
    }

    @Test
    @DisplayName("changeEmail, changePhone e changePassword substituem só a parte correspondente")
    void shouldChangeIndividualParts() {
        User user = newUser();

        user.changeEmail(new Email("novo@gmail.com"));
        user.changePhone(new Phone("11999998888"));
        user.changePassword(new PasswordHash("$argon2id$outro"));

        assertThat(user.getEmail().value()).isEqualTo("novo@gmail.com");
        assertThat(user.getPhone().value()).isEqualTo("11999998888");
        assertThat(user.getPasswordHash().value()).isEqualTo("$argon2id$outro");
        // o que não foi alterado permanece
        assertThat(user.getName()).isEqualTo(new Name("Maria Brown"));
    }

    @Test
    @DisplayName("não é possível trocar o e-mail por um valor nulo")
    void shouldNotChangeEmailToNull() {
        User user = newUser();

        assertThatThrownBy(() -> user.changeEmail(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user email is required");
        assertThat(user.getEmail().value()).isEqualTo("maria@gmail.com");
    }

    @Test
    @DisplayName("igualdade é por identidade")
    void shouldCompareByIdentity() {
        User a = User.restore(new UserId(1L), personalInfo(), contactsInfo(), credentials());
        User sameId = User.restore(new UserId(1L), personalInfo(), contactsInfo(), credentials());
        User otherId = User.restore(new UserId(2L), personalInfo(), contactsInfo(), credentials());

        assertThat(a).isEqualTo(sameId).isNotEqualTo(otherId);
        assertThat(newUser()).isNotEqualTo(newUser());
    }
}
