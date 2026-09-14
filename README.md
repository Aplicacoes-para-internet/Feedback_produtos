# Feedback de Produtos

Este é um projeto acadêmico desenvolvido para permitir o cadastro de produtos e usuários, além do envio e visualização de feedbacks sobre os produtos.

O projeto foi desenvolvido utilizando Java 17, JSP, Servlets, MVC, DAO, Service, Maven, Tomcat e MySQL.

## 🚀 Como executar o projeto

A forma mais simples de executar o projeto é utilizando o Docker.

### Pré-requisitos

Antes de começar, é necessário ter:

- Docker Desktop instalado
- Git instalado

Não é necessário instalar Java, Maven, Tomcat ou MySQL separadamente, pois o projeto utiliza containers Docker para executar esses serviços.

## 📥 Baixando o projeto

Abra o terminal e execute:

```bash
git clone https://github.com/Aplicacoes-para-internet/Feedback_produtos.git
```

Entre na pasta do projeto:

```bash
cd Feedback_produtos
```

## 🐳 Executando com Docker

Com o Docker Desktop aberto, execute:

```bash
docker compose up --build
```

Na primeira execução, o Docker irá preparar automaticamente:

- MySQL
- Banco de dados
- Tabelas do sistema
- Java
- Maven
- Tomcat
- Aplicação web

Depois que os containers forem iniciados, acesse no navegador:

```text
http://localhost:8080
```

## 🗄️ Banco de dados

O projeto utiliza MySQL.

O banco de dados é criado automaticamente pelo Docker a partir do arquivo:

```text
schema.sql
```

As configurações utilizadas pelo projeto são:

```text
Banco: feedback_db
Usuário: feedback_user
Senha: feedback_password
MySQL: porta 3306 dentro do container
```

Para evitar conflitos com instalações locais do MySQL, a porta utilizada pelo computador é:

```text
3307
```

A aplicação Java continua acessando o MySQL internamente pela porta 3306.

## 📂 Estrutura do projeto

```text
Feedback_produtos/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── schema.sql
├── README.md
│
└── feedback-produtos/
    └── src/
        └── main/
            ├── java/
            │   └── br/com/feedbackprodutos/
            │       ├── config/
            │       ├── controller/
            │       ├── dao/
            │       ├── model/
            │       ├── service/
            │       └── util/
            │
            ├── resources/
            │   ├── db.properties
            │   └── db.properties.example
            │
            └── webapp/
                ├── WEB-INF/
                ├── css/
                ├── feedback/
                ├── produto/
                ├── usuario/
                └── index.jsp
```

## 🧩 Tecnologias utilizadas

- Java 17
- Maven
- JSP
- Servlets
- Tomcat 10.1
- MySQL 8
- Docker
- MVC
- DAO
- Service

## 💬 Funcionalidades

O sistema permite:

- Cadastrar produtos
- Listar produtos
- Cadastrar usuários
- Listar usuários
- Enviar feedbacks
- Listar feedbacks
- Armazenar os dados no MySQL

## 🛑 Para parar o projeto

Para encerrar os containers, pressione:

```text
Ctrl + C
```

Ou, em outro terminal dentro da pasta do projeto:

```bash
docker compose down
```

## 🔄 Para executar novamente

Depois que o projeto já tiver sido criado, normalmente basta executar:

```bash
docker compose up
```

Não é necessário utilizar `--build` todas as vezes.

Use:

```bash
docker compose up --build
```

quando houver alterações que precisem reconstruir a imagem da aplicação.

## ⚠️ Recriando o banco de dados

Caso seja necessário apagar o banco e criá-lo novamente utilizando o `schema.sql`, execute:

```bash
docker compose down -v
```

Depois:

```bash
docker compose up --build
```

**Atenção:** o comando `docker compose down -v` remove o volume do banco e, consequentemente, os dados armazenados nele.

## 👨‍💻 Projeto acadêmico

Projeto desenvolvido para fins acadêmicos na disciplina de Aplicações para Internet.

O objetivo é aplicar na prática conceitos de desenvolvimento web, organização em camadas, banco de dados e utilização de containers.
