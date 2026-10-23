package com.devsuperior.dscommerce.shared.application;

/**
 * Base de todas as exceções do tipo "conflito com o estado atual do sistema" — por exemplo, tentar cadastrar um
 * e-mail que já existe. Vira <b>HTTP 409 (Conflict)</b> no {@code GlobalExceptionHandler}.
 *
 * <p><b>Qual a diferença para os outros erros?</b>
 * <ul>
 *   <li>{@code DomainException} (422): o <i>dado em si</i> viola uma regra ("e-mail inválido") — o mesmo pedido
 *       falharia sempre.</li>
 *   <li>{@link NotFoundException} (404): o recurso pedido não existe.</li>
 *   <li>{@code ConflictException} (409): o dado é válido, mas <i>colide com o que já existe</i> — o mesmo pedido
 *       poderia ter funcionado se o e-mail ainda estivesse livre.</li>
 * </ul>
 *
 * <p>Mesma ideia da {@link NotFoundException}: uma base comum permite ao handler tratar <i>uma</i> classe, sem
 * conhecer as exceções de cada módulo.
 */
public abstract class ConflictException extends RuntimeException {

    protected ConflictException(String message) {
        super(message);
    }
}
