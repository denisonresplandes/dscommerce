package com.devsuperior.dscommerce.users.application;

import com.devsuperior.dscommerce.users.application.exception.UserNotFoundException;
import com.devsuperior.dscommerce.users.application.port.UserQueryRepository;
import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service do lado de <b>leitura</b> de usuários (substitui {@code FindUserByIdHandler},
 * {@code FindUserByEmailHandler} e {@code FindAllUsersHandler}).
 *
 * <p>Separado do {@link UserCommandService} de propósito (CQRS leve): os dois lados mudam por motivos diferentes
 * e têm necessidades diferentes. Este aqui:
 * <ul>
 *   <li>é {@code readOnly = true}: o Hibernate pula o <i>dirty checking</i> e o banco pode otimizar a transação;</li>
 *   <li>nunca monta o agregado: devolve {@link UserView} direto do {@link UserQueryRepository}.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    public UserQueryService(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    public UserView findById(Long id) {
        // O value object valida o id (positivo) antes de ir ao banco.
        UserId userId = new UserId(id);
        return userQueryRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.value()));
    }

    public UserView findByEmail(String email) {
        // O value object normaliza (trim + minúsculas); por isso a busca é insensível a caixa.
        Email normalized = new Email(email);
        return userQueryRepository.findByEmail(normalized)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public List<UserView> findAll() {
        return userQueryRepository.findAll();
    }
}
