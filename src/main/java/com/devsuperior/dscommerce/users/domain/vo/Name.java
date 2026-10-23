package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.util.regex.Pattern;

/**
 * Nome de uma pessoa.
 *
 * <p><b>Normalização</b> (igual ao que {@link Email} e {@link Phone} já faziam): remove espaços nas pontas e
 * reduz sequências de espaços a um só. Assim {@code "  Maria   Brown "} e {@code "Maria Brown"} são o mesmo valor.
 * Antes, o nome era guardado exatamente como chegava.
 *
 * <p><b>Regras</b>
 * <ul>
 *   <li>de {@value #MIN_LENGTH} a {@value #MAX_LENGTH} caracteres (o máximo acompanha a coluna
 *       {@code tb_user.name}, {@code length = 100}; passar disso viraria erro de banco);</li>
 *   <li>sem dígitos;</li>
 *   <li>só letras (de qualquer idioma, com acentos), espaço, apóstrofo, hífen e ponto, começando por letra —
 *       o que cobre "Maria D'Ávila", "João-Pedro" e "Jr.", mas recusa "$%&amp;" ou "_".</li>
 * </ul>
 *
 * <p><b>BUG corrigido:</b> a única regra era "não pode ter número", então "R2D2" era barrado mas "$%&amp;@#" passava.
 */
public record Name(String value) {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 100;

    // \p{L} = qualquer letra Unicode; \p{M} = marcas combinadas (acentos escritos "separados" da letra).
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^\\p{L}[\\p{L}\\p{M} .'-]*$");

    public Name {
        DomainException.requireNonNull(value, "user name is required");

        // Normalização: trim() tira as pontas; replaceAll junta espaços repetidos (inclui tab e quebra de linha).
        value = value.trim().replaceAll("\\s+", " ");

        if (value.isEmpty()) {
            throw new DomainException("user name is required");
        }
        if (value.length() < MIN_LENGTH) {
            throw new DomainException("user name is too short.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new DomainException("user name is too long.");
        }
        // A checagem de dígitos vem antes da genérica só para manter a mensagem específica que já existia.
        if (value.matches(".*\\d.*")) {
            throw new DomainException("user name cannot contain numbers.");
        }
        if (!ALLOWED_CHARACTERS.matcher(value).matches()) {
            throw new DomainException(
                    "user name may contain only letters, spaces, apostrophes, hyphens and periods.");
        }
    }
}
