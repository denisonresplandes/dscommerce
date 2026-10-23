package com.devsuperior.dscommerce.users.application;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.application.command.CreateUserCommand;
import com.devsuperior.dscommerce.users.application.exception.EmailAlreadyInUseException;
import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Teste <b>unitário</b> do {@link UserCommandService}: só a lógica do caso de uso, sem Spring e sem banco.
 *
 * <h3>Como ler este teste (guia de Mockito)</h3>
 * O service depende de duas interfaces: {@link UserRepository} (guarda usuários) e {@link UserPasswordHasher}
 * (gera hashes). Em vez de usar implementações reais — banco de dados e Argon2, lentos —, o Mockito cria
 * <b>dublês</b> ("mocks") dessas interfaces, que nós controlamos:
 * <ul>
 *   <li>{@code @Mock}: cria um objeto falso da interface. Por padrão ele não faz nada e devolve valores vazios
 *       ({@code false}, {@code null}, {@code Optional.empty()}).</li>
 *   <li>{@code when(mock.metodo(args)).thenReturn(valor)}: ensina o mock — "quando este método for chamado com
 *       estes argumentos, responda este valor". É o <b>stub</b>.</li>
 *   <li>{@code when(...).thenAnswer(...)}: como o {@code thenReturn}, mas calcula a resposta a partir dos
 *       argumentos recebidos. Usado para simular o banco, que devolve o usuário com um id gerado.</li>
 *   <li>{@code verify(mock).metodo(args)}: confere que o método FOI chamado, com aqueles argumentos.</li>
 *   <li>{@code verify(mock, never()).metodo(...)}: confere que NÃO foi chamado. É assim que provamos, por exemplo,
 *       que o hash caro não foi gerado.</li>
 *   <li>{@code verifyNoInteractions(mocks)}: confere que ninguém encostou neles.</li>
 *   <li>{@code ArgumentCaptor}: "pega" o objeto que foi passado a um mock, para inspecioná-lo depois.</li>
 * </ul>
 * O {@code MockitoExtension} é "estrito": se um stub é declarado e nunca usado, o teste falha — isso evita stubs
 * esquecidos que dariam falsa segurança.
 */
@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    private static final String RAW_PASSWORD = "secret123";
    private static final PasswordHash HASHED_PASSWORD = new PasswordHash(
            "$argon2id$v=19$m=16384,t=2,p=1$e2/Vof4sdyYV9DadKYgE2Q$OwD7aYMKPCfevm7fgxmIjc0pDcb0dRHoBb1pHx9YU28");

    /** Dublê do repositório: nenhum banco é tocado. */
    @Mock
    private UserRepository userRepository;

    /** Dublê do gerador de hash: nenhum Argon2 (lento) é executado. */
    @Mock
    private UserPasswordHasher passwordHasher;

    /** Captura o {@link User} entregue ao repositório, para conferirmos o que seria gravado. */
    @Captor
    private ArgumentCaptor<User> savedUserCaptor;

    private UserCommandService service;

    @BeforeEach
    void setUp() {
        // O service real, mas ligado aos dublês. Passar as dependências pelo construtor é o que torna isso simples.
        service = new UserCommandService(userRepository, passwordHasher);
    }

    private CreateUserCommand commandWithEmail(String email) {
        return new CreateUserCommand("Maria Brown", email, "98888888888", LocalDate.of(2001, 7, 25), RAW_PASSWORD);
    }

    /**
     * Ensina os mocks a se comportarem como num cadastro bem-sucedido. Usado pelos testes de "caminho feliz".
     */
    private void stubSuccessfulCreation(Email expectedEmail) {
        // "Quando perguntarem se este e-mail já existe, responda: não." (e-mail livre)
        when(userRepository.existsByEmail(expectedEmail)).thenReturn(false);

        // "Quando pedirem o hash desta senha, devolva um hash fixo." (evita rodar o Argon2 de verdade)
        when(passwordHasher.toHash(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);

        // "Quando mandarem salvar um usuário, devolva o MESMO usuário, mas já com id 10." Imita o banco, que gera
        // o id no INSERT. thenAnswer permite montar a resposta a partir do argumento recebido.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User toSave = invocation.getArgument(0);
            return User.restore(
                    new UserId(10L),
                    new PersonalInfo(toSave.getName(), toSave.getDateOfBirth()),
                    new ContactsInfo(toSave.getEmail(), toSave.getPhone()),
                    new Credentials(toSave.getPasswordHash())
            );
        });
    }

    @Test
    @DisplayName("cria o usuário: salva com a senha já em hash e devolve a view com o id gerado")
    void shouldCreateUser() {
        stubSuccessfulCreation(new Email("maria@gmail.com"));

        UserView result = service.create(commandWithEmail("maria@gmail.com"));

        // O que o service DEVOLVEU:
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("Maria Brown");
        assertThat(result.email()).isEqualTo("maria@gmail.com");

        // O que o service ENTREGOU ao repositório (capturado do mock):
        verify(userRepository).save(savedUserCaptor.capture());
        User saved = savedUserCaptor.getValue();
        assertThat(saved.getId()).as("antes do banco, o usuário ainda não tem id").isNull();
        assertThat(saved.getPasswordHash())
                .as("o repositório recebe o HASH, nunca a senha em texto puro")
                .isEqualTo(HASHED_PASSWORD);
    }

    @Test
    @DisplayName("o e-mail é normalizado ANTES de checar duplicidade (' MARIA@Gmail.com ' == 'maria@gmail.com')")
    void shouldNormalizeEmailBeforeCheckingForDuplicates() {
        // O stub só responde "livre" para o e-mail NORMALIZADO. Se o service consultasse o texto cru, o mock não
        // reconheceria o argumento e o teste falharia.
        stubSuccessfulCreation(new Email("maria@gmail.com"));

        service.create(commandWithEmail("  MARIA@Gmail.com "));

        verify(userRepository).existsByEmail(new Email("maria@gmail.com"));
    }

    @Test
    @DisplayName("e-mail já cadastrado → EmailAlreadyInUseException, SEM gerar o hash caro e SEM salvar")
    void shouldRejectDuplicateEmailBeforeHashing() {
        // "Quando perguntarem se este e-mail existe, responda: sim." (já está em uso)
        when(userRepository.existsByEmail(new Email("maria@gmail.com"))).thenReturn(true);

        assertThatThrownBy(() -> service.create(commandWithEmail("maria@gmail.com")))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("A user with e-mail maria@gmail.com already exists.");

        // A prova do ganho: o Argon2 (caro) nem chegou a rodar...
        verify(passwordHasher, never()).toHash(anyString());
        // ...e nada foi gravado.
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("dado inválido → DomainException antes de qualquer acesso ao banco ou ao hash")
    void shouldNotTouchRepositoryOrHasherWhenDataIsInvalid() {
        CreateUserCommand invalidName = new CreateUserCommand(
                "Fulano 2", "fulano@example.com", "98888888888", LocalDate.of(2001, 7, 25), RAW_PASSWORD);

        assertThatThrownBy(() -> service.create(invalidName))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name cannot contain numbers.");

        // Nenhum dos dois dublês foi usado: a validação barrou tudo antes.
        verifyNoInteractions(userRepository, passwordHasher);
    }

    @Test
    @DisplayName("e-mail malformado → DomainException antes de qualquer acesso ao banco ou ao hash")
    void shouldNotTouchRepositoryWhenEmailIsMalformed() {
        assertThatThrownBy(() -> service.create(commandWithEmail("not-an-email")))
                .isInstanceOf(DomainException.class)
                .hasMessage("invalid user email address");

        verifyNoInteractions(userRepository, passwordHasher);
    }

    @Test
    @DisplayName("comando nulo → DomainException")
    void shouldRejectNullCommand() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("create user command cannot be null");
    }
}
