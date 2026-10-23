# ADR 0001 — Migrar de Vertical Slice para Arquitetura em Camadas (monólito modular)

- **Status:** Aceito
- **Data:** 2026-10-22
- **Estado anterior preservado na tag:** `v1-vertical-slice`

## 1. Contexto

A versão 1 do projeto organizava o código em **módulos por feature** (`users`, `orders`) e, dentro de cada módulo,
em **slices por caso de uso** (`commands/create`, `queries/findbyid`, …), cada slice com seu controller, handler,
query/command e DTOs.

Isso funcionou bem para começar, mas a revisão da arquitetura mostrou que o resultado era um híbrido:

| Observação | Consequência |
|---|---|
| Domínio, infraestrutura e mappers eram **compartilhados por módulo**, não por slice | Os slices não eram independentes: mudar uma consulta mexia em port + adapter + Spring Data + mapper + projeção compartilhados |
| `orders` importava `users.domain.UserReadRepository`, `users.domain.vo.UserId` e `UserNotFoundException` | Acoplamento entre módulos: o domínio de pedidos não compilava sem o domínio de usuários |
| Leituras passavam por `Projection → Summary (VOs) → Response` | Value objects reconstruídos e revalidados a cada linha lida; três mapeamentos para devolver quatro campos |
| Um controller e um handler por endpoint | Muitas classes pequenas e estrutura de pastas inconsistente (`dto/` só em um slice) |

Além disso, o objetivo de aprendizado do projeto é praticar **DDD tático** (agregados, domínio rico, value objects,
separação entre modelo de domínio e de persistência) numa estrutura que a maior parte do mercado reconhece de
imediato: a arquitetura em camadas.

## 2. Decisão

Adotar **arquitetura em camadas dentro de cada módulo**, mantendo os conceitos de DDD que já existiam.

### 2.1 Estrutura

```
com.devsuperior.dscommerce
├── shared/                        # núcleo compartilhado (Shared Kernel) — o mínimo possível
│   ├── domain/                    #   AggregateRoot, DomainException
│   ├── application/               #   NotFoundException
│   ├── presentation/              #   GlobalExceptionHandler (RFC 9457 ProblemDetail)
│   └── infrastructure/config/     #   SecurityConfig
├── users/                         # módulo
│   ├── api/                       #   contrato PÚBLICO para outros módulos (Facade)
│   ├── presentation/              #   controllers, dto (Request/Response), mappers web
│   ├── application/               #   services (casos de uso), commands, views, ports de leitura, exceções
│   ├── domain/                    #   agregado, value objects, port do repositório, ports de domínio
│   └── infrastructure/            #   persistência (entidade JPA + mapper + adapters), segurança (Argon2)
└── orders/                        # módulo (mesma estrutura)
    └── infrastructure/acl/        #   Anti-Corruption Layer: único ponto que conhece `users.api`
```

### 2.2 Regra de dependência

```
presentation ──▶ application ──▶ domain ◀── infrastructure
```

- `domain` não conhece Spring, JPA, web nem nenhuma outra camada.
- `application` orquestra casos de uso e define *ports* (interfaces) de que precisa; só conhece `domain`.
  Por pragmatismo, os services usam as anotações do Spring `@Service` e `@Transactional` (são metadados de
  composição e de transação). Continua proibido importar JPA, web (`jakarta.persistence`, `org.springframework.web`)
  ou qualquer classe de `infrastructure`/`presentation`.
- `infrastructure` **implementa** os ports (inversão de dependência) — é a única camada que conhece JPA/Argon2.
- `presentation` só chama `application`; nunca `domain` nem `infrastructure`.

### 2.3 Regras entre módulos

1. Um módulo só pode usar de outro o pacote `api` (contrato público). Nada de `domain`, `application` interno ou
   `infrastructure` do vizinho.
2. **Agregados se referenciam por identidade, nunca por objeto.** `Order` guarda um `ClientId`, não um `User`.
3. `orders` define o **seu próprio** `ClientId`. No contexto de pedidos, o usuário é um *cliente*; o modelo de
   `orders` não deve mudar porque `users` mudou (Bounded Contexts independentes).
4. Validar que "o cliente existe" é uma regra que cruza dois agregados, então **não fica no domínio**. Fica na camada
   de aplicação de `orders`, que depende de um port (`ClientLookup`) cuja implementação (`UserModuleClientLookup`,
   na infraestrutura) traduz a chamada para `users.api` — um *Anti-Corruption Layer*.
5. Leituras que juntam dados de dois agregados são feitas no lado de leitura (query com DTO próprio), sem carregar
   dois agregados.

### 2.4 O que é mantido do DDD

| Conceito | Onde |
|---|---|
| Aggregate Root + domínio rico | `User`, `Order` (regras e transições de estado dentro do agregado, não nos services) |
| Value Objects imutáveis (records) | `Email`, `Phone`, `Name`, `OrderId`, `ClientId`, … |
| Factory methods | `User.create` / `User.restore`, `Order.create` / `Order.restore` |
| Modelo de domínio ≠ modelo de persistência | `User`/`Order` (domínio) × `UserEntity`/`OrderEntity` (JPA), ligados por *mappers* |
| Repository como port do domínio | `UserRepository`, `OrderRepository` (interfaces no domínio, adapters na infraestrutura) |

### 2.5 CQRS leve (mantido)

A separação entre escrita e leitura permanece, mas agora de forma mais simples:

- **Escrita:** `UserCommandService` carrega/cria o **agregado** via `UserRepository`.
- **Leitura:** `UserQueryService` usa `UserQueryRepository`, que devolve *views* (`UserView`) direto do banco,
  **sem montar o agregado**. É mais rápido e remove a revalidação de VOs a cada linha lida.

### 2.6 Padrões de projeto usados (e por quê)

| Padrão | Uso | Motivo |
|---|---|---|
| Repository | `UserRepository`, `OrderRepository` | O domínio fala em termos de coleção de agregados, sem saber de JPA |
| Adapter (Ports & Adapters) | `JpaUserRepositoryAdapter` etc. | Implementa o port do domínio usando Spring Data |
| Mapper | `UserMapper`, `OrderMapper`, `*WebMapper` | Isola modelos: domínio ↔ JPA ↔ DTO web |
| Service Layer | `*CommandService`, `*QueryService` | Um ponto de orquestração e de transação por caso de uso |
| Facade | `users.api.UserModuleApi` | Único contrato que outros módulos enxergam |
| Anti-Corruption Layer | `orders.infrastructure.acl.UserModuleClientLookup` | Traduz o vocabulário de `users` para o de `orders` |
| Factory Method | `User.create`, `Order.create` | Criação válida, nomeando a intenção |
| Value Object | `Email`, `Phone`, … | Validação e normalização encapsuladas no tipo |
| Strategy (via enum) | `OrderStatus.canTransitionTo` | Máquina de estados do pedido sem `if/else` espalhado |

## 3. Consequências

### Positivas
- Estrutura familiar e previsível: quem conhece camadas navega sem aprender uma convenção própria.
- Menos classes: um controller e um service por agregado, no lugar de um par controller/handler por endpoint.
- Acoplamento entre módulos explícito e verificável: restrito a `users.api`, imposto por **ArchUnit** no build.
- Leitura mais rápida (sem agregado nem revalidação) e mais simples (projeta direto para a view).
- Erros de negócio passam a ter tratamento HTTP uniforme (`404`, `422`, `400`) via `GlobalExceptionHandler`.

### Negativas / custos assumidos
- **Perde-se a independência por caso de uso.** Services podem crescer; a mitigação é separar
  `CommandService`/`QueryService` e dividir por subcaso quando passar de ~6–8 métodos.
- **Mais mapeamentos** do que num CRUD direto (domínio ↔ entidade ↔ DTO). É o preço da separação de modelos e foi
  aceito conscientemente.
- **Duplicação intencional** de `ClientId` (em `orders`) e `UserId` (em `users`): troca de reuso por autonomia.
- A FK `tb_order.user_id → tb_user.id` continua no banco (mesmo banco no monólito). Se um módulo for extraído no
  futuro, essa FK deverá ser removida e a consistência passa a ser eventual.

## 4. Alternativas consideradas

1. **Manter Vertical Slice e completar o isolamento** (query-repository por slice, leitura direta em DTO).
   Seria coerente, mas foi descartado: o objetivo aqui é exercitar camadas + DDD tático em uma estrutura mais comum.
2. **Compartilhar `UserId` num shared kernel.** Menos trabalho, porém mantém `orders` acoplado ao tipo de `users`.
3. **Domain events + consistência eventual entre módulos já agora.** Adiado: ainda não há efeito colateral entre
   agregados que justifique. `AggregateRoot` está pronto para receber eventos quando houver.

## 5. Plano de migração (commits na branch `refactor/layered-architecture`)

| Etapa | Conteúdo |
|---|---|
| 1. Fundação | ADR, dependências de teste, validação de VOs padronizada em `DomainException`, `NotFoundException` + `GlobalExceptionHandler`, `SecurityConfig` em `shared` |
| 2. Módulo `users` | Pacotes em camadas, `UserRepository`, services, views, `UserController`, `UserModuleApi` |
| 3. Módulo `orders` | Idem, com `ClientId`, `ClientLookup` e ACL para `users.api` |
| 4. Guardas | Testes de arquitetura (ArchUnit), testes de domínio e de API |

## 6. Como validar

- `./mvnw test` — inclui os testes de arquitetura que quebram o build se uma regra da seção 2 for violada.
- Estado anterior para comparação: `git diff v1-vertical-slice..refactor/layered-architecture`.
