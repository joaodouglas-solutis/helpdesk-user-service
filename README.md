# Helpdesk User Service

Microsserviço responsável pela identidade e gerenciamento de usuários do sistema Helpdesk.

## Arquitetura

O serviço é estruturado em uma arquitetura orientada a camadas:

```text
com.solutis.userservice
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service
```

A entrada da aplicação é realizada por `UserServiceApplication`.

A camada `controller` concentra os endpoints HTTP, enquanto `service` concentra as operações de negócio. A persistência é isolada em `repository`, e os objetos utilizados na comunicação HTTP são definidos em `dto`.

A segurança permanece separada em `config` e `security`, permitindo que autenticação e autorização não fiquem misturadas com as regras de gerenciamento de usuários.

## Entidade de usuário

A entidade principal é `User`.

Ela utiliza:

* UUID como identificador;
* nome;
* e-mail;
* hash da senha;
* papel (`Role`);
* indicador de atividade;
* data de criação.

O e-mail possui restrição de unicidade no banco através de `@UniqueConstraint` e também da configuração da coluna.

A entidade utiliza `@PrePersist` para registrar automaticamente `createdAt`. O estado `active` permite inativação lógica do usuário sem remoção física do registro.

## DTOs

A comunicação externa é separada da entidade JPA através de DTOs.

Entre os contratos existentes estão:

```text
ChangePasswordRequest
ClientOptionResponse
CreateUserRequest
LoginRequest
LoginResponse
RegisterUserRequest
UserResponse
UserSummaryResponse
```

Essa separação evita expor diretamente a entidade persistida como contrato da API.

## UserService

`UserService` concentra as operações de:

* criação administrativa;
* registro público;
* consulta individual;
* consulta resumida;
* listagem;
* listagem de clientes ativos;
* atualização administrativa;
* alteração da própria senha;
* inativação.

As operações de leitura utilizam `@Transactional(readOnly = true)`, enquanto operações de alteração utilizam transações normais.

A criação e o registro verificam previamente a existência do e-mail e armazenam somente a senha processada pelo `PasswordEncoder`.

O registro público força o papel:

```java
.role(Role.CLIENT)
```

enquanto a criação administrativa recebe o papel através do request.

## Alteração da própria senha

A alteração de senha utiliza um endpoint específico:

```text
PUT /users/me/password
```

O `UserController` não recebe um ID arbitrário. O identificador é obtido diretamente do `Authentication`:

```java
UUID authenticatedUserId =
        UUID.fromString(authentication.getName());
```

O serviço então:

1. localiza o usuário autenticado;
2. verifica se está ativo;
3. valida a senha atual através do `PasswordEncoder`;
4. impede que a nova senha seja igual à anterior;
5. gera o novo hash;
6. persiste a alteração.

Essa operação está separada da atualização administrativa do usuário.

## Segurança

`SecurityConfig` utiliza uma política stateless e um filtro JWT próprio.

A aplicação utiliza:

```java
SessionCreationPolicy.STATELESS
```

e registra:

```java
.addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

A autorização é feita por endpoint e método HTTP.

Existem regras específicas para:

* autenticação;
* registro;
* consulta de clientes;
* resumo de usuário;
* alteração da própria senha;
* criação administrativa;
* atualização administrativa;
* inativação administrativa.

## JWT

`JwtAuthenticationFilter` intercepta cada requisição e procura o header:

```text
Authorization: Bearer <token>
```

O token é validado pelo `JwtService`.

A partir dos claims são extraídos:

```text
sub  → identificador do usuário
role → papel do usuário
```

O papel é convertido para uma `SimpleGrantedAuthority` com o prefixo:

```text
ROLE_
```

e a autenticação é registrada no `SecurityContextHolder`.

## Inativação

A exclusão de usuários é implementada como inativação:

```java
user.setActive(false);
```

O registro permanece no banco e as operações posteriores podem considerar seu estado através de `active`.

Isso preserva o registro histórico do usuário em vez de remover fisicamente a informação.

## Exceções

O projeto possui uma camada específica para tratamento de erros:

```text
exception/
├── ApiError
├── EmailAlreadyExistsException
├── GlobalExceptionHandler
├── InvalidPasswordException
└── UserNotFoundException
```

As exceções de domínio ficam separadas das classes HTTP, permitindo que os controllers permaneçam focados no fluxo das requisições.

## Containerização

O repositório possui `Dockerfile` e Maven Wrapper, mantendo a aplicação preparada para execução sem depender da instalação global do Maven.
