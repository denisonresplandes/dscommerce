package com.devsuperior.dscommerce.shared.presentation;

import com.devsuperior.dscommerce.shared.application.NotFoundException;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tradução central de exceções em respostas HTTP.
 *
 * <p><b>Problema que resolve:</b> antes, {@code UserNotFoundException}, {@code DomainException} e violações de
 * validação não tinham tratamento e viravam <b>HTTP 500</b> — o cliente da API não conseguia distinguir "o recurso
 * não existe" de "o servidor quebrou".
 *
 * <p><b>Como funciona:</b> {@code @RestControllerAdvice} intercepta exceções lançadas por qualquer controller.
 * O corpo segue o padrão <a href="https://www.rfc-editor.org/rfc/rfc9457">RFC 9457 (Problem Details)</a>,
 * representado pelo {@link ProblemDetail} do Spring.
 *
 * <p>Estender {@link ResponseEntityExceptionHandler} já cobre, no mesmo formato, os erros "de infraestrutura web"
 * (JSON malformado, {@code @Valid} falhando no body, parâmetro ausente, método não permitido…).
 * Aqui só adicionamos o que é específico da aplicação.
 *
 * <p><b>Mapeamento:</b>
 * <ul>
 *   <li>{@link NotFoundException} → 404</li>
 *   <li>{@link DomainException} (regra de negócio / invariante violada) → 422</li>
 *   <li>{@link ConstraintViolationException} (validação de {@code @PathVariable}/{@code @RequestParam}) → 400</li>
 * </ul>
 *
 * <p>As mensagens de {@code DomainException} são escritas para descrever a regra violada e podem ser expostas ao
 * cliente. Nunca coloque dados sensíveis nelas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
