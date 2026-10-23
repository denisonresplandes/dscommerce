package com.devsuperior.dscommerce.users.application;

import com.devsuperior.dscommerce.users.api.UserModuleApi;
import com.devsuperior.dscommerce.users.application.port.UserQueryRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação interna do contrato público {@link UserModuleApi}.
 *
 * <p>Traduz os tipos simples do contrato (que é o que o resto do sistema conhece) para os tipos internos do
 * módulo ({@link UserId}) e delega ao port de leitura.
 */
@Service
@Transactional(readOnly = true)
public class UserModuleFacade implements UserModuleApi {

    private final UserQueryRepository userQueryRepository;

    public UserModuleFacade(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    @Override
    public boolean existsById(Long userId) {
        return userQueryRepository.existsById(new UserId(userId));
    }
}
