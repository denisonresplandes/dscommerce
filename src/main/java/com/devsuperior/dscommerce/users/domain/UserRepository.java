package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.util.Optional;

/**
 * Port de persistência do agregado {@link User} (padrão <b>Repository</b> do DDD).
 *
 * <p><b>Ideia central:</b> para o domínio, um repositório é uma "coleção de agregados". Ele recebe e devolve
 * {@link User} — o objeto de domínio rico — e não faz a menor ideia de que existe JPA, SQL ou banco de dados.
 * Quem sabe disso é o <i>adapter</i> {@code JpaUserRepositoryAdapter}, na camada de infraestrutura
 * (princípio da inversão de dependência: o domínio define o contrato, a infraestrutura obedece).
 *
 * <p><b>Escrita × leitura:</b> este repositório existe para o lado de <b>escrita</b> (casos de uso que mudam estado e
 * precisam das regras do agregado). Consultas que só devolvem dados para a tela usam
 * {@code UserQueryRepository} (camada {@code application}), que não monta o agregado e por isso é bem mais barato.
 *
 * <p><b>Por que só {@code save} e {@code findById}?</b> Um repositório expõe apenas o que os casos de uso
 * realmente usam. {@code delete} e {@code deleteById} existiam sem nenhum chamador, então saíram (YAGNI); voltam
 * quando houver o caso de uso "excluir usuário".
 */
public interface UserRepository {

    /**
     * Persiste o agregado. Para um usuário novo ({@code id == null}) devolve o agregado com o id gerado.
     */
    User save(User user);

    Optional<User> findById(UserId id);

    /**
     * Informa se já existe algum usuário com este e-mail.
     *
     * <p>Recebe o {@link Email} (já normalizado: sem espaços e em minúsculas), e não uma {@code String}, para que
     * "MARIA@gmail.com" e "maria@gmail.com" sejam reconhecidos como o mesmo e-mail.
     */
    boolean existsByEmail(Email email);
}
