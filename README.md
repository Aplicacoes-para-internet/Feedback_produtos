# Feedback de Produtos

Projeto acadêmico em Java com arquitetura MVC, separação em camadas de Model, View, Controller, Service e DAO, usando MySQL como banco de dados e Docker para execução.

## Tecnologias

- Java 17
- JSP
- Servlets
- MVC
- DAO
- Service
- MySQL
- Maven
- Tomcat
- Docker

O projeto possui uma estrutura em que o código-fonte da aplicação fica dentro da pasta `feedback-produtos/src`, enquanto o `pom.xml`, `Dockerfile` e `docker-compose.yml` ficam na raiz do repositório.

O `Dockerfile` copia `feedback-produtos/src` para a estrutura padrão `src` do projeto Maven durante a etapa de build. O `pom.xml` utiliza essa estrutura padrão para localizar as classes Java, recursos e páginas JSP.

### 🌐 Acessando a aplicação

Depois que os containers forem iniciados, acesse no navegador:

```text
http://localhost:8080/
```

A aplicação é publicada como `ROOT.war` no Tomcat. Por isso, **não é necessário colocar `/feedback-produtos` na URL**.

A página inicial da aplicação está em:

```text
feedback-produtos/src/main/webapp/index.jsp
```

Portanto, a URL correta é:

```text
http://localhost:8080/
```

ou:

```text
http://localhost:8080/index.jsp
```

## Funcionalidades implementadas

O sistema possui CRUD completo e validado em execução para as três entidades principais:

- Feedback
  - criar
  - listar
  - editar
  - excluir
- Usuários
  - cadastrar
  - listar
  - editar
  - excluir
- Produtos
  - cadastrar
  - listar
  - editar
  - excluir

Além disso, o sistema aplica regras de integridade:

- não permite excluir usuário com feedback vinculado
- não permite excluir produto com feedback vinculado
- valida e-mail duplicado para usuários
- valida campos obrigatórios e formatos básicos

## Estrutura do projeto

```text
Feedback_produtos/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── schema.sql
├── README.md
└── feedback-produtos/
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── br/com/feedbackprodutos/
        │   │       ├── config/
        │   │       ├── controller/
        │   │       ├── dao/
        │   │       ├── model/
        │   │       ├── service/
        │   │       └── util/
        │   ├── resources/
        │   │   └── db.properties.example
        │   └── webapp/
        │       ├── WEB-INF/
        │       ├── css/
        │       ├── feedback/
        │       ├── produto/
        │       ├── usuario/
        │       └── index.jsp
```

## Fluxo da arquitetura

- View: páginas JSP em src/main/webapp
- Controller: servlets em controller
- Service: regras de negócio em service
- DAO: acesso ao banco em dao
- Model: entidades em model

## Como executar

### Opção 1: Docker

```bash
docker compose up --build
```

Acesse:

```text
http://localhost:8080
```

### Opção 2: Maven local

```bash
mvn clean package
```

Em seguida, publique o WAR em um container Tomcat local ou em ambiente de execução compatível.

## Banco de dados

O banco é inicializado pelo arquivo schema.sql.

Estrutura principal:

- Produtos
- Usuarios
- Feedback

Relacionamentos:

- Feedback referencia produto_id e usuario_id
- A exclusão de produto ou usuário com feedback vinculado é bloqueada pela camada de serviço

Use `docker compose up --build` quando houver alterações no código ou nos arquivos de configuração que precisem reconstruir a imagem da aplicação.

## Observações de validação

A aplicação foi validada em execução real com Docker. O processo de verificação incluiu:

- build do projeto com docker compose up --build
- inicialização do MySQL e do Tomcat
- criação, edição e exclusão de usuários
- criação, edição e exclusão de produtos
- criação, edição e exclusão de feedback
- verificação da mensagem de erro ao tentar excluir registros ligados a feedback

## Projeto acadêmico

Este projeto foi desenvolvido para fins acadêmicos, com foco em MVC, persistência em banco relacional, organização em camadas e validação de regras de negócio.
