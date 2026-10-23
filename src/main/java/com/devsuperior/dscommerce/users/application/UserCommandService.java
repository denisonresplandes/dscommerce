package com.devsuperior.dscommerce.users.application;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.application.command.CreateUserCommand;
import com.devsuperior.dscommerce.users.application.exception.EmailAlreadyInUseException;
import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.application.view.UserViewMapper;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Application Service</b> do lado de escrita de usuários (substitui o antigo {@code CreateUserHandler}).
 *
 * <p><b>O que um Application Service faz e NÃO faz</b>
 * <ul>
 *   <li><b>Faz:</b> orquestra o caso de uso — converte o comando em objetos de domínio, chama o agregado, usa o
 *       repositório e define a fronteira da transação.</li>
 *   <li><b>Não faz:</b> regras de negócio. Elas ficam no domínio ({@link User}, value objects). Se este service
 *       começar a ter {@code if} sobre regras do usuário, a regra deveria estar no agregado ("domínio anêmico"
 *       é o antipadrão a evitar).</li>
 * </ul>
 *
 * <p><b>Transação:</b> {@code @Transactional} na classe significa que cada método roda em uma transação de
 * escrita. Hoje há um único repositório, mas o ponto certo para transacionar é o caso de uso, pois no futuro ele
 * pode salvar mais de um agregado e tudo deve confirmar (ou falhar) junto.
 *
 * <p><b>Diferença para a versão anterior (Vertical Slice):</b> havia uma classe por caso de uso
 * ({@code CreateUserHandler}). Agora os casos de uso de escrita de usuário vivem neste service, um método cada.
 * Se ele crescer demais, divida por subcaso (ex.: {@code UserProfileService}).
 */
@Service
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserPasswordHasher passwordHasher;

    public UserCommandService(UserRepository userRepository, UserPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserView create(CreateUserCommand command) {
        DomainException.requireNonNull(command, "create user command cannot be null");

        // 1) Valida e normaliza os dados. Cada "new" abaixo aplica as regras do value object: se algo for
        //    inválido, uma DomainException é lançada aqui e o GlobalExceptionHandler a transforma em HTTP 422.
        //    Nenhum dado inválido avança para as etapas seguintes (que envolvem banco e CPU).
        PersonalInfo personalInfo = new PersonalInfo(
                new Name(command.name()),
                new DateOfBirth(command.dateOfBirth())
        );
        ContactsInfo contactsInfo = new ContactsInfo(
                new Email(command.email()),
                new Phone(command.phone())
        );

        // 2) Garante que o e-mail está livre. A ORDEM IMPORTA: esta checagem é barata (um SELECT) e vem ANTES de
        //    gerar o hash, que é propositalmente caro (Argon2 gasta ~16 MB e bastante CPU por chamada). Assim, um
        //    e-mail repetido é recusado sem gastar nada disso — o que também evita que alguém use cadastros
        //    repetidos para sobrecarregar o servidor.
        //    Esta verificação é uma checagem rápida, não a garantia final: duas requisições simultâneas podem
        //    passar juntas por aqui. A garantia final é a restrição UNIQUE do banco (veja UserEntity).
        if (userRepository.existsByEmail(contactsInfo.email())) {
            throw new EmailAlreadyInUseException(contactsInfo.email().value());
        }

        // 3) Só agora gera o hash, pela porta do domínio; o agregado nunca recebe a senha em texto puro.
        Credentials credentials = new Credentials(
                passwordHasher.toHash(command.password())
        );

        User savedUser = userRepository.save(User.create(personalInfo, contactsInfo, credentials));

        // Devolve uma view, não o agregado: as camadas externas não devem manipular objetos de domínio.
        return UserViewMapper.toView(savedUser);
    }
}
