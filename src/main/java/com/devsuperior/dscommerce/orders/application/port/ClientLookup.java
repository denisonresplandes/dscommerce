package com.devsuperior.dscommerce.orders.application.port;

import com.devsuperior.dscommerce.orders.domain.vo.ClientId;

/**
 * Pergunta que o módulo {@code orders} precisa fazer ao resto do sistema: "este cliente existe?".
 *
 * <p><b>Por que isto é um port e não uma chamada direta ao módulo de usuários?</b>
 * <ul>
 *   <li>A regra "só se lista/cria pedido de cliente existente" <b>cruza dois agregados</b>, então não pode estar
 *       dentro de {@code Order}. Ela é orquestrada pela camada de aplicação.</li>
 *   <li>A aplicação de pedidos declara <i>o que precisa saber</i>, em seu próprio vocabulário
 *       ({@link ClientId}), e não depende de como {@code users} responde.</li>
 *   <li>A implementação ({@code UserModuleClientLookup}, em {@code infrastructure.acl}) é o
 *       <b>Anti-Corruption Layer</b>: o único ponto que conhece o contrato público de {@code users}.
 *       Se {@code users} virar um serviço remoto, só essa classe muda.</li>
 * </ul>
 */
public interface ClientLookup {

    boolean exists(ClientId clientId);
}
