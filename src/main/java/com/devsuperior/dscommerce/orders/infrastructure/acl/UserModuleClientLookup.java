package com.devsuperior.dscommerce.orders.infrastructure.acl;

import com.devsuperior.dscommerce.orders.application.port.ClientLookup;
import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.users.api.UserModuleApi;
import org.springframework.stereotype.Component;

/**
 * <b>Anti-Corruption Layer (ACL)</b> entre {@code orders} e {@code users}.
 *
 * <p>Implementa o port {@link ClientLookup} (vocabulário de pedidos: "cliente") traduzindo para o contrato
 * público de {@code users} (vocabulário de usuários: "id de usuário"). É a <b>única classe</b> do módulo
 * {@code orders} autorizada a importar {@code users} — e só o pacote {@code users.api}. O teste de arquitetura
 * (ArchUnit) vai garantir isso no build.
 *
 * <p><b>Por que "corrupção"?</b> Sem essa camada, o vocabulário e as exceções de um módulo vazam para o outro, e o
 * modelo de pedidos passa a ser deformado pelas decisões de usuários. O ACL é a "alfândega" que traduz na
 * fronteira. Hoje a tradução é trivial ({@code ClientId} → {@code Long}); o valor está em ter o lugar certo para
 * quando deixar de ser — por exemplo, se {@code users} virar um serviço remoto.
 */
@Component
class UserModuleClientLookup implements ClientLookup {

    private final UserModuleApi userModuleApi;

    UserModuleClientLookup(UserModuleApi userModuleApi) {
        this.userModuleApi = userModuleApi;
    }

    @Override
    public boolean exists(ClientId clientId) {
        return userModuleApi.existsById(clientId.value());
    }
}
