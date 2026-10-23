package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de {@link PersonalInfo}, com foco na regra de maioridade (bug 2.2: o código antigo usava {@code > 18} e
 * tratava como menor quem tinha exatamente 18 anos).
 *
 * <p>Todos os cenários usam {@code isAdult(LocalDate today)} com datas fixas: assim o resultado não depende do dia
 * em que o teste roda.
 */
class PersonalInfoTest {

    // Nasceu em 15/06/2000 -> faz 18 anos em 15/06/2018.
    private static final LocalDate BORN = LocalDate.of(2000, 6, 15);

    private PersonalInfo personBornOn(LocalDate dateOfBirth) {
        return new PersonalInfo(new Name("Maria Brown"), new DateOfBirth(dateOfBirth));
    }

    @Test
    @DisplayName("na véspera do 18º aniversário ainda é menor")
    void shouldBeMinorTheDayBeforeTurning18() {
        assertThat(personBornOn(BORN).isAdult(LocalDate.of(2018, 6, 14))).isFalse();
    }

    @Test
    @DisplayName("NO dia do 18º aniversário já é maior (este é o caso que o bug errava)")
    void shouldBeAdultOnThe18thBirthday() {
        assertThat(personBornOn(BORN).isAdult(LocalDate.of(2018, 6, 15))).isTrue();
    }

    @Test
    @DisplayName("depois do 18º aniversário continua sendo maior")
    void shouldBeAdultAfterThe18thBirthday() {
        assertThat(personBornOn(BORN).isAdult(LocalDate.of(2018, 6, 16))).isTrue();
        assertThat(personBornOn(BORN).isAdult(LocalDate.of(2040, 1, 1))).isTrue();
    }

    @Test
    @DisplayName("com 17 anos é menor")
    void shouldBeMinorAt17() {
        assertThat(personBornOn(BORN).isAdult(LocalDate.of(2017, 12, 31))).isFalse();
    }

    @Test
    @DisplayName("quem nasceu em 29/02 completa 18 anos em 01/03 nos anos não bissextos")
    void shouldHandleLeapDayBirthdays() {
        PersonalInfo leapBaby = personBornOn(LocalDate.of(2004, 2, 29));

        // 2022 não é bissexto: em 28/02 ainda faltam dias para os 18 anos completos...
        assertThat(leapBaby.isAdult(LocalDate.of(2022, 2, 28))).isFalse();
        // ...e em 01/03 já completou.
        assertThat(leapBaby.isAdult(LocalDate.of(2022, 3, 1))).isTrue();
    }

    @Test
    @DisplayName("a versão sem parâmetro usa a data de hoje")
    void shouldUseTodayWhenNoDateIsGiven() {
        LocalDate today = LocalDate.now();

        assertThat(personBornOn(today.minusYears(30)).isAdult()).isTrue();
        assertThat(personBornOn(today).isAdult()).isFalse();
    }

    @Test
    @DisplayName("a data de referência é obrigatória")
    void shouldRequireReferenceDate() {
        assertThatThrownBy(() -> personBornOn(BORN).isAdult(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("reference date cannot be null");
    }

    @Test
    @DisplayName("nome e data de nascimento são obrigatórios")
    void shouldRequireNameAndDateOfBirth() {
        assertThatThrownBy(() -> new PersonalInfo(null, new DateOfBirth(BORN)))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is required");
        assertThatThrownBy(() -> new PersonalInfo(new Name("Maria Brown"), null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user date of birth is required");
    }
}
