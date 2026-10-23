package com.devsuperior.dscommerce.shared.presentation;

import com.devsuperior.dscommerce.shared.application.ConflictException;
import com.devsuperior.dscommerce.shared.application.NotFoundException;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

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
 * (JSON malformado, {@code @Valid} falhando no body, parâmetro ausente, método não permitido, validação de
 * parâmetros de método…). Aqui só adicionamos o que é específico da aplicação.
 *
 * <p><b>Mapeamento:</b>
 * <ul>
 *   <li>{@link NotFoundException} → 404</li>
 *   <li>{@link DomainException} (regra de negócio / invariante violada) → 422</li>
 *   <li>{@link ConflictException} (ex.: e-mail já cadastrado) → 409</li>
 *   <li>{@link DataIntegrityViolationException} (o <i>banco</i> recusou: chave única, FK…) → 409</li>
 *   <li>{@link ConstraintViolationException} (validação de métodos fora do alcance do Spring MVC) → 400</li>
 * </ul>
 *
 * <p>As mensagens de {@code DomainException} e {@code ConflictException} são escritas para descrever a regra
 * violada e podem ser expostas ao cliente. Nunca coloque dados sensíveis nelas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleConflict(ConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Rede de segurança para a <b>corrida</b>: duas requisições simultâneas com o mesmo e-mail passam juntas pela
     * verificação "já existe?" (ambas respondem "não") e a segunda é barrada pela restrição UNIQUE do banco.
     * Sem este handler, esse caso viraria HTTP 500.
     *
     * <p>A mensagem do banco ({@code ex.getMessage()}) NÃO é devolvida ao cliente: ela expõe nomes de tabelas,
     * colunas e trechos de SQL. Fica apenas no log.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The request conflicts with existing data.");
    }

    /**
     * <b>BUG corrigido:</b> a resposta devolvia {@code ex.getMessage()} cru, que traz o nome interno do método do
     * controller (ex.: {@code "findById.id: deve ser maior que 0"}) — vazamento de detalhe de implementação.
     * Agora o detalhe lista só o campo e o motivo ({@code "id: deve ser maior que 0"}).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String violations = ex.getConstraintViolations().stream()
                .map(GlobalExceptionHandler::describe)
                .sorted()
                .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed: " + violations);
    }

    /**
     * O caminho da violação vem como {@code metodo.parametro}; percorremos os nós e ficamos com o último nome
     * (o parâmetro), descartando o nome do método.
     */
    private static String describe(ConstraintViolation<?> violation) {
        String field = "";
        for (Path.Node node : violation.getPropertyPath()) {
            field = node.getName();
        }
        return field + ": " + violation.getMessage();
    }
}
