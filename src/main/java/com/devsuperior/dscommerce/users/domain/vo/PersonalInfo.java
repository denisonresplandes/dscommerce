package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.time.LocalDate;
import java.time.Period;

public record PersonalInfo(
        Name name,
        DateOfBirth dateOfBirth
        // demais atributos.
) {
    public static final int AGE_OF_MAJORITY = 18;

    public PersonalInfo {
        DomainException.requireNonNull(name, "user name is required");
        DomainException.requireNonNull(dateOfBirth, "user date of birth is required");
    }

    /**
     * Indica se a pessoa é maior de idade <b>hoje</b>.
     */
    public boolean isAdult() {
        return isAdult(LocalDate.now());
    }

    /**
     * Indica se a pessoa era (ou será) maior de idade na data informada.
     *
     * <p><b>Por que existe esta sobrecarga?</b> {@code LocalDate.now()} depende do relógio da máquina, e um teste
     * não consegue "congelar" o dia de hoje. Recebendo a data como parâmetro, o teste fixa qualquer dia — inclusive
     * o do 18º aniversário, que é exatamente onde estava o bug.
     *
     * <p><b>BUG corrigido:</b> a comparação era {@code > 18}, então quem tinha exatamente 18 anos completos era
     * considerado menor. A maioridade começa <i>no</i> aniversário de 18 anos, logo a comparação correta é
     * {@code >= 18}.
     *
     * <p>{@link Period#between} conta anos <i>completos</i> (respeitando meses e dias), então o resultado é
     * correto mesmo para quem nasceu em 29 de fevereiro.
     */
    public boolean isAdult(LocalDate today) {
        DomainException.requireNonNull(today, "reference date cannot be null");
        return Period.between(dateOfBirth.value(), today).getYears() >= AGE_OF_MAJORITY;
    }
}
