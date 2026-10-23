package com.devsuperior.dscommerce.users.application.port;

import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port de <b>leitura</b> de usuários, definido pela camada de aplicação.
 *
 * <p><b>Por que está em {@code application} e não em {@code domain}?</b> O domínio só descreve o que é preciso para
 * proteger as regras do agregado (isso é o {@code UserRepository}). Consultas são uma necessidade dos
 * <i>casos de uso</i> — "mostre-me os dados para a tela" — e devolvem {@link UserView}, um modelo de leitura que
 * não é conceito de domínio.
 *
 * <p>A implementação ({@code JpaUserQueryRepositoryAdapter}) fica na infraestrutura e projeta as colunas direto
 * na view, sem criar o agregado. Os parâmetros continuam sendo value objects ({@link UserId}, {@link Email}) para
 * que a normalização (ex.: e-mail em minúsculas) aconteça num lugar só.
 */
public interface UserQueryRepository {

    Optional<UserView> findById(UserId id);

    Optional<UserView> findByEmail(Email email);

    List<UserView> findAll();

    boolean existsById(UserId id);
}
