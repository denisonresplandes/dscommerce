package com.devsuperior.dscommerce.shared.application;

/**
 * Base de todas as exceções do tipo "recurso não encontrado".
 *
 * <p><b>Por que existe?</b> A camada de apresentação precisa transformar erros de negócio em HTTP, mas não deve
 * conhecer cada exceção de cada módulo ({@code UserNotFoundException}, {@code OrderNotFoundException}, …).
 * Com uma base comum, o {@code GlobalExceptionHandler} trata <i>uma</i> classe e devolve 404 para todas.
 *
 * <p><b>Por que na camada {@code application}?</b> "Não encontrado" é o resultado de um caso de uso (consultar algo
 * que não existe), não uma regra de negócio violada. Violações de regra continuam sendo {@code DomainException}.
 */
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
