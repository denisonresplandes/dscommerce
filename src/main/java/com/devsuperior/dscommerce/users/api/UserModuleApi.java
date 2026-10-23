package com.devsuperior.dscommerce.users.api;

/**
 * <b>API pública do módulo {@code users}</b> — a única porta de entrada para outros módulos (padrão
 * <b>Facade</b>).
 *
 * <p><b>Problema que resolve:</b> o módulo {@code orders} precisa saber se um usuário existe, mas importar o
 * repositório, o agregado ou os value objects de {@code users} acopla os dois módulos (mudar um quebra o outro).
 * Com este contrato:
 * <ul>
 *   <li>só tipos simples atravessam a fronteira ({@code Long}, {@code boolean}) — nada de {@code UserId};</li>
 *   <li>tudo que não está em {@code users.api} é interno e pode mudar livremente;</li>
 *   <li>um teste de arquitetura (ArchUnit) proíbe outros módulos de importarem qualquer outro pacote de
 *       {@code users}.</li>
 * </ul>
 *
 * <p>Se um dia {@code users} virar um serviço separado, só a implementação desta interface muda (de chamada de
 * método para chamada HTTP); quem a consome nem percebe.
 */
public interface UserModuleApi {

    boolean existsById(Long userId);
}
