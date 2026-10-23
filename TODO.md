# TODO — dscommerce

Estado em **2026-10-22**, branch `refactor/layered-architecture`.
Contexto e decisões: [`docs/adr/0001-layered-architecture.md`](docs/adr/0001-layered-architecture.md).
Estado anterior (Vertical Slice) preservado na tag `v1-vertical-slice`.

Legenda: 🔴 alta (bug ou risco real) · 🟠 média · 🟢 baixa (polimento) · `[x]` concluído

> **Convenção:** a migração para camadas (seção 1) **preserva o comportamento**. Bugs e melhorias (seções 2 a 6)
> entram em commits separados (`fix:`, `perf:`, `feat:`) para que o histórico mostre o que é refatoração e o que muda
> comportamento. Os itens abaixo estão escritos para a estrutura **em camadas** (`presentation`, `application`,
> `domain`, `infrastructure`).

---

## 1. Migração para arquitetura em camadas

### Etapa 1 — Fundação ✅
- [x] ADR 0001 (`docs/adr/`)
- [x] Dependências de teste + ArchUnit no `pom.xml` (antes os testes nem compilavam)
- [x] Value objects de `users` lançam `DomainException` (antes: NPE e `IllegalArgumentException` misturados)
- [x] `NotFoundException` + `GlobalExceptionHandler` (ProblemDetail): 404 / 422 / 400 no lugar de 500
- [x] `SecurityConfig` movido para `shared/infrastructure/config`
- [x] `GlobalExceptionHandlerTest` fixando o contrato HTTP de erros

### Etapa 2 — Módulo `users` ✅
- [x] **E2.1** Renomear `UserWriteRepository` → `UserRepository` (port do agregado no domínio); manter só `save` e
  `findById` (remover `delete`/`deleteById`, que nenhum caso de uso usa). Adapter e Spring Data renomeados junto.
- [x] **E2.2** Criar a camada `application`: `UserView` (read model), `UserQueryRepository` (port de leitura),
  `UserQueryService`, `UserCommandService`, `command/CreateUserCommand`, `exception/UserNotFoundException`.
  Leitura por `select new UserView(...)`, **sem** montar o agregado nem revalidar VOs.
- [x] **E2.3** Criar `presentation`: um `UserController` (substitui 4 controllers), `dto/` (`CreateUserRequest`,
  `UserResponse` único no lugar de `UserResponse` + `CreateUserResponse` idênticos) e `UserWebMapper`.
- [x] **E2.4** Remover os pacotes `commands/`, `queries/` e as classes obsoletas (`*Handler`, `UserSummary`,
  `UserSummaryProjection`, `UserReadRepository`, `UserResponseMapper`, `CreateUserMapper`).
- [x] **E2.5** Expor `users.api.UserModuleApi` (Facade) com `existsById(Long)`, para uso do módulo `orders`.
- [x] **E2.6** Agrupar a infraestrutura de hash: `Argon2UserPasswordHasher` e o bean `PasswordEncoder` no mesmo
  pacote `infrastructure/security`.

### Etapa 3 — Módulo `orders` ✅
- [x] **E3.1** Criar `orders.domain.vo.ClientId` e usá-lo em `Order`, `OrderEntity`, mappers (fim da dependência de
  `users.domain.vo.UserId`). Renome `userId → clientId` feito em domínio, entidade e view; o campo **público** da API segue `userId`
  (mudar quebraria clientes; ver item 5.4).
- [x] **E3.2** Port `ClientLookup` (application) + `orders.infrastructure.acl.UserModuleClientLookup`, que chama
  `users.api.UserModuleApi` (Anti-Corruption Layer). `ClientNotFoundException` própria de `orders`.
- [x] **E3.3** Mesma estrutura de `users`: `OrderRepository` (`save`, `findById`), `OrderView`, `OrderQueryRepository`,
  `OrderQueryService`, `OrderController` (mapeia `/orders`, `/orders/{id}` e `/users/{userId}/orders`),
  `OrderWebMapper`.
- [x] **E3.4** Corrigir o typo do pacote `infraestructure` → `infrastructure` (junto da movimentação).
- [x] **E3.5** Repositórios Spring Data de leitura devem estender `Repository<>`, não `JpaRepository<>`
  (hoje expõem `save/delete` no lado de leitura).

### Etapa 4 — Guardas
- [ ] **E4.1** `ArchitectureTest` (ArchUnit): `domain` sem Spring/JPA; `presentation` não acessa `domain` nem
  `infrastructure`; `application` não acessa `infrastructure`/`presentation`; `orders` só enxerga `users.api`;
  sem ciclos entre módulos.
- [ ] **E4.2** Testes unitários de domínio: `Order` (todas as transições válidas/inválidas e idempotência),
  `OrderStatus`, VOs de `users`.
- [x] **E4.3** Testes de API cobrindo todos os endpoints existentes (`UserApiTest`, `OrderApiTest`,
  `GlobalExceptionHandlerTest`).
- [ ] **E4.4** Abrir PR (`refactor/layered-architecture` → `main`), merge **sem squash**, tag `v2-layered-architecture`.

---

## 2. Bugs 🔴

- [ ] **2.1** 🔴 **`PasswordHash` aceita prefixo bcrypt inexistente.** `startsWith("$b$")`, mas o bcrypt real começa com
  `$2a$`/`$2b$`/`$2y$`. O projeto só usa Argon2: aceitar somente `$argon2id$` (ou corrigir os prefixos bcrypt).
  Teste: hash bcrypt real passa; `"$b$qualquer"` é rejeitado.
- [ ] **2.2** 🔴 **`PersonalInfo.isAdult()` usa `> 18`.** Quem tem exatamente 18 anos é tratado como menor.
  Trocar por `>= AGE_OF_MAJORITY`. Teste no dia do 18º aniversário. Usar `Clock` (item 4.4) para o teste ser
  determinístico.
- [ ] **2.3** 🔴 **E-mail duplicado não é tratado.** `UserEntity.email` não tem `unique`, e `UserCommandService` não
  verifica. Dois usuários com o mesmo e-mail fazem `findByEmail` lançar `IncorrectResultSizeDataAccessException` (500).
  Ações: índice único em `tb_user.email`; `UserRepository.existsByEmail(Email)` checado **antes** de gerar o hash;
  `DataIntegrityViolationException` → 409 no `GlobalExceptionHandler` (cobre a corrida entre duas requisições);
  `EmailAlreadyInUseException` em `users.application.exception`.
- [ ] **2.4** 🟠 **`GET /users/email` aceita qualquer string** e falha só no VO (hoje 422, mas para entrada
  claramente malformada o correto é 400). Adicionar `@Email` no `@RequestParam`.
- [ ] **2.5** 🟠 **`Name` não normaliza**: não faz `trim()` (`Email` e `Phone` fazem), então `"  A"` passa na regra de
  tamanho. Rejeitar dígitos é frágil (barra "R2 D2", aceita "$%&"). Definir a regra e normalizar.
- [ ] **2.6** 🟠 **Request e VO divergem**: `CreateUserRequest.phone` exige `^\d{11}$`, enquanto `Phone` aceita e
  normaliza qualquer formato com 11 dígitos. Escolher um contrato (aceitar `(11) 98888-8888` e normalizar, ou exigir só
  dígitos) e documentar.
- [ ] **2.7** 🟠 **Faltam tamanhos máximos no Request**: colunas têm `length = 100/100/20`, mas `name` e `email` não têm
  `@Size(max = …)`. Estouro vira erro de banco (500). Usar constantes compartilhadas entre Request e entidade.
- [ ] **2.8** 🟠 **Política de senha**: `@Size(min = 6, max = 20)` é fraca e restritiva. Mínimo 8 e teto alto
  (ex.: 128) para permitir passphrases. Com Argon2 não há motivo para limite baixo.
- [ ] **2.9** 🟢 `ConstraintViolationException` expõe, na mensagem, o nome do método (ex.: `findById.id: …`).
  Remover `@Validated` dos controllers e usar a validação de métodos nativa do Spring (devolve
  `HandlerMethodValidationException`, já tratada pela `ResponseEntityExceptionHandler`), ou formatar a mensagem.

---

## 3. Desempenho

- [ ] **3.1** 🔴 **Listagens sem paginação.** `GET /users` e `GET /orders` carregam a tabela inteira. Adicionar
  `Pageable` com teto de `size` e *whitelist* de ordenação; para `orders`, avaliar paginação por cursor (keyset).
  Devolver envelope próprio, não `PageImpl`.
- [ ] **3.2** 🟠 **Índices.** `tb_order.user_id` (usado por `findAllByClientId`) e `tb_user.email` não têm índice.
  Declarar em `@Table(indexes = …)`. A FK `fk_order_user` está criada à mão no `import.sql`; declarar no mapeamento
  ou na migration (item 4.1).
- [ ] **3.3** 🟠 **`GET /users/{id}/orders` faz 2 queries** (`existsById` + `select`). Medir e decidir: (a) lista vazia
  sem checar, (b) uma query única. Se o requisito é 404, manter (b) com o `ClientLookup`.
- [ ] **3.4** 🟠 **Argon2 no thread da requisição, sem limite.** Cada `POST /users` usa ~16 MB e CPU
  (`m=16384,t=2,p=1`). Ações: checar duplicidade antes do hash (2.3); limitar concorrência (semáforo/bulkhead) ou
  *rate limit*; revisar os parâmetros contra o mínimo OWASP atual (m ≥ 19 MiB, t = 2, p = 1) com benchmark.
- [ ] **3.5** 🟠 **`save()` de agregado existente faz `SELECT` + `UPDATE`** (mapper cria entidade *detached* com id →
  `merge`). Ao implementar comandos de `orders`, carregar a entidade gerenciada e aplicar as mudanças.
- [ ] **3.6** 🟢 `IDENTITY` impede *batch insert*. Só vale trocar por `SEQUENCE` + `hibernate.jdbc.batch_size` se
  surgir carga em lote.
- [ ] **3.7** 🟢 Habilitar `spring.threads.virtual.enabled=true` (Java 21); manter limite de concorrência do Argon2
  (CPU-bound).
- [ ] **3.8** 🟢 Trocar `spring.jpa.show-sql=true` por `logging.level.org.hibernate.SQL=DEBUG` (só em dev); usar
  `hibernate.generate_statistics` em dev para detectar N+1 quando surgirem itens de pedido.
- [ ] **3.9** 🟢 Cache HTTP (`ETag`/`Cache-Control`) em `GET /users/{id}` e `/orders/{id}`, só após medir.

---

## 4. Persistência e dados

- [ ] **4.1** 🟠 **Migrations (Flyway/Liquibase)** no lugar do schema gerado pelo Hibernate + `import.sql`:
  `V1__schema.sql` e `V2__seed_dev.sql` (apenas dev).
- [ ] **4.2** 🟠 **Banco real fora do dev**: perfil `prod` com PostgreSQL e pool Hikari dimensionado; Testcontainers
  nos testes de integração.
- [ ] **4.3** 🟠 **Controle otimista**: `@Version` em `UserEntity`/`OrderEntity` e propagar para `User`/`Order`
  (essencial nas transições de status do pedido).
- [ ] **4.4** 🟢 **`Clock` injetável**: `Order.create`, `DateOfBirth` e `PersonalInfo.isAdult` usam `now()` global
  (testes não determinísticos, sem fuso explícito). Passar `Clock`/instante pelo service; avaliar `Instant` para
  `createdAt`.
- [ ] **4.5** 🟢 `AggregateRoot` está vazio: usar para eventos de domínio (`registerEvent`/`pullEvents`) quando
  houver efeito entre agregados, ou remover.
- [ ] **4.6** 🟢 `User.changeEmail/changePhone/changePassword` não têm caso de uso: criar os comandos de atualização ou
  remover até haver uso. Remover também o comentário `// demais metodos..`.

---

## 5. Funcionalidades pendentes

- [ ] **5.1** 🟠 **Write side de `orders`**: `OrderCommandService` com `create`, `pay`, `ship`, `deliver`, `cancel`
  (o domínio já tem as regras de transição, mas nada as usa) e endpoints correspondentes.
  `create` valida o cliente via `ClientLookup`.
- [ ] **5.2** 🟠 **Itens do pedido e produtos** (hoje `Order` só tem cliente, status e data).
- [ ] **5.3** 🟢 Update e delete de usuário.
- [ ] **5.4** 🟢 **API v2**: renomear o campo público `userId` → `clientId` nas respostas de pedido (hoje o
  `OrderWebMapper` faz a ponte entre o nome interno e o público).

---

## 6. Segurança e configuração

- [ ] **6.1** 🔴 **`spring.profiles.active=test` fixo em `application.properties`.** O perfil de teste (H2, console
  H2, `show-sql`) é o padrão em qualquer ambiente. Remover e ativar por `SPRING_PROFILES_ACTIVE`; renomear
  `application-test.properties` para `application-dev.properties` (evita confusão com `src/test`).
- [ ] **6.2** 🔴 **Todos os endpoints públicos** (`anyRequest().permitAll()`) e `GET /users` devolve nome, e-mail,
  telefone e data de nascimento de todos. Antes de qualquer deploy: autenticação (JWT/OAuth2 Resource Server) e
  autorização (ADMIN lista tudo; usuário vê só o próprio perfil e pedidos); `SessionCreationPolicy.STATELESS`.
- [ ] **6.3** 🟠 **Console H2 e `frameOptions.disable()` globais**: restringir ao perfil dev. O
  `spring-boot-h2console` está em escopo compile no `pom.xml` → `runtime`/`optional` ou perfil dev.
- [ ] **6.4** 🟠 **`GET /users/email?email=…` coloca PII na query string** (vai para logs de proxy). Preferir
  `GET /users?email=` ou `POST /users/search`.
- [ ] **6.5** 🟢 CORS explícito, versionamento (`/api/v1/…`), headers de segurança, *rate limiting* nas escritas.

---

## 7. Qualidade e build

- [ ] **7.1** 🟠 **Observabilidade**: `spring-boot-starter-actuator`, logs estruturados com *correlation id*, métrica
  do tempo do Argon2.
- [ ] **7.2** 🟠 **CI** (GitHub Actions): `./mvnw verify` em cada push/PR (inclui ArchUnit), análise estática
  (Checkstyle/Spotless, SpotBugs).
- [ ] **7.3** 🟢 **Documentação da API** (springdoc-openapi) e um `README.md` real explicando a arquitetura e como
  rodar.
- [ ] **7.4** 🟢 **Limpar o `pom.xml`**: `<name/>`, `<description/>`, `<licenses>`, `<developers>`, `<scm>` vazios;
  versão do BouncyCastle (`1.84`) em property; Dependabot/Renovate.

---

## Ordem sugerida

1. Concluir a migração: etapas 2, 3 e 4 (seção 1), PR e merge.
2. Bugs: 2.1, 2.2, 2.3 (+ 2.4 a 2.8) em commits `fix:` separados.
3. Segurança mínima: 6.1 e 6.2 antes de qualquer deploy.
4. Escala: 3.1 (paginação), 3.2 (índices), 4.1 (migrations).
5. Funcionalidades: seção 5.
6. Polimento: seção 7 e itens 🟢.
