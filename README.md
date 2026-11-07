<div align="center">

# Feedback de Produtos

**Sistema web para cadastro, gerenciamento e avaliação de produtos**

Projeto acadêmico desenvolvido para a disciplina de **Aplicações para Internet**.

`Java 17` · `JSP` · `Jakarta Servlet` · `MySQL` · `Maven` · `Docker`

</div>

---

## Sobre o projeto

O **Feedback de Produtos** é uma aplicação web criada para organizar produtos, usuários e avaliações em um único sistema. A proposta é colocar em prática conceitos estudados na disciplina, como desenvolvimento web com Java, arquitetura MVC, persistência em banco de dados relacional e criação de uma API REST.

Além das páginas acessadas pelo navegador, o projeto disponibiliza endpoints para consultar e manipular dados em formato JSON.

## Funcionalidades

| Módulo | Funcionalidades |
| --- | --- |
| **Produtos** | Cadastrar, listar, consultar, editar e excluir produtos |
| **Usuários** | Cadastrar, listar, consultar, editar e excluir usuários |
| **Avaliações** | Registrar, listar, consultar, editar e excluir feedbacks associados a produtos e usuários |
| **API REST** | Operações GET, POST, PUT e DELETE para os três módulos |

> **Observação:** as ações de edição e exclusão pela interface JSP fazem parte da atualização recente do projeto. Antes da entrega, valide essas operações no ambiente local ou Docker.

## Tecnologias utilizadas

| Tecnologia | Utilização |
| --- | --- |
| Java 17 | Linguagem principal |
| Jakarta Servlet 6.0 | Recebimento e processamento de requisições HTTP |
| JSP, HTML e CSS | Interface web |
| Maven | Gerenciamento de dependências e geração do arquivo WAR |
| JDBC e MySQL Connector/J | Comunicação com o banco de dados |
| MySQL | Armazenamento de produtos, usuários e avaliações |
| Jackson | Conversão de objetos Java para JSON e vice-versa |
| Apache Tomcat 10.1 | Servidor de aplicação |
| Docker e Docker Compose | Execução da aplicação e do banco em containers |

## Organização do sistema

O projeto segue uma organização em camadas baseada no padrão **MVC (Model–View–Controller)**:

```text
Navegador / Cliente HTTP
          |
          v
Controller (Servlet)
          |
          v
Service (validações e regras de negócio)
          |
          v
DAO (JDBC e consultas SQL)
          |
          v
       MySQL
```

- **Model:** representa os dados de produtos, usuários e feedbacks.
- **View:** reúne as páginas JSP e os estilos CSS.
- **Controller:** recebe as requisições e direciona as operações.
- **Service:** concentra as validações e regras de negócio.
- **DAO:** realiza o acesso ao banco de dados com JDBC.

A API REST utiliza as mesmas regras de negócio e persistência, mas retorna respostas em JSON.

## Como executar com Docker 🐳

Esta é a forma recomendada para testar o projeto, pois permite iniciar a aplicação e o MySQL sem configurar manualmente o Tomcat.

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução.
- Docker Compose disponível (já incluído nas versões atuais do Docker Desktop).
- Portas utilizadas pelos serviços livres, especialmente a **8081** para a aplicação.

### 1. Obter o projeto

```bash
git clone https://github.com/Aplicacoes-para-internet/Feedback_produtos.git
cd Feedback_produtos
```

Se você já baixou o projeto, basta abrir um terminal na pasta que contém `docker-compose.yml`.

### 2. Conferir a configuração

Antes de iniciar, verifique as variáveis de ambiente e as configurações do banco definidas em `docker-compose.yml`.

Caso o arquivo exija um `.env`, crie-o localmente com os valores apropriados. **Não publique senhas, `.env` ou `db.properties` no GitHub.**

O script `schema.sql` contém a estrutura oficial do banco `feedback_db`. Verifique no `docker-compose.yml` se ele está configurado para inicializar o MySQL automaticamente. Caso não esteja, execute o script no banco antes de utilizar o sistema.

### 3. Iniciar os containers

Na raiz do projeto:

```bash
docker compose up --build -d
```

O Docker irá construir a imagem da aplicação e iniciar os serviços configurados. Na primeira execução, esse processo pode levar alguns minutos.

### 4. Acessar a aplicação

Após os containers estarem prontos:

| Recurso | Endereço |
| --- | --- |
| **Interface web** | http://localhost:8081/feedback-produtos/ |
| **API de produtos** | http://localhost:8081/feedback-produtos/api/produtos |
| **API de usuários** | http://localhost:8081/feedback-produtos/api/usuarios |
| **API de avaliações** | http://localhost:8081/feedback-produtos/api/feedbacks |

> A porta **8081** corresponde à configuração Docker utilizada neste projeto. Se o mapeamento de portas for alterado no Compose, ajuste os endereços acima.

### 5. Verificar o funcionamento

Confira o estado dos serviços:

```bash
docker compose ps
```

Caso a aplicação não abra, acompanhe os logs:

```bash
docker compose logs -f
```

Para encerrar a visualização dos logs, pressione `Ctrl + C`. Isso não desliga os containers.

### 6. Parar e reiniciar

Para interromper os serviços:

```bash
docker compose down
```

Para iniciar novamente:

```bash
docker compose up -d
```

**Atenção:** não utilize `docker compose down -v` sem necessidade, pois a opção `-v` remove volumes gerenciados pelo Compose e pode apagar os dados persistidos do MySQL.

## Como executar sem Docker

Esta alternativa exige **JDK 17, Maven, MySQL e Tomcat 10.1** instalados.

1. Execute o arquivo `schema.sql` no MySQL para criar o banco e as tabelas.
2. Configure a conexão usando as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.
3. Na raiz do repositório, compile o projeto:

```bash
mvn clean package
```

4. Copie o arquivo gerado `target/feedback-produtos.war` para a pasta `webapps/` do Tomcat.
5. Inicie o Tomcat e abra:

**http://localhost:8080/feedback-produtos/**

Exemplo de configuração das variáveis no **PowerShell**:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/feedback_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
$env:DB_USER = "SEU_USUARIO_MYSQL"
$env:DB_PASSWORD = "SUA_SENHA_LOCAL"
```

Esses valores são apenas exemplos. Não inclua credenciais reais no repositório.

## Banco de dados

O banco utilizado pela aplicação é o **`feedback_db`**, com três entidades principais:

| Tabela | Finalidade |
| --- | --- |
| `Produtos` | Armazena os produtos cadastrados |
| `Usuarios` | Armazena os usuários |
| `Feedback` | Registra avaliações e relaciona produtos e usuários |

Cada avaliação está vinculada a um produto e a um usuário. A nota deve estar entre **1 e 5**.

O arquivo **`schema.sql`** é a referência para a estrutura atual do banco. Scripts antigos não devem substituí-lo.

**Integridade dos dados:** dependendo dos relacionamentos existentes, a exclusão de um produto ou usuário com avaliações vinculadas pode ser bloqueada pelo MySQL. Nesse caso, a aplicação deve informar o motivo ao usuário.

## API REST

As rotas abaixo utilizam o prefixo:

```text
http://localhost:8081/feedback-produtos/api
```

Se estiver executando diretamente no Tomcat local, substitua a porta `8081` por `8080`.

| Método | Produtos | Usuários | Avaliações |
| --- | --- | --- | --- |
| GET (listar) | `/produtos` | `/usuarios` | `/feedbacks` |
| GET (por ID) | `/produtos/{id}` | `/usuarios/{id}` | `/feedbacks/{id}` |
| POST (cadastrar) | `/produtos` | `/usuarios` | `/feedbacks` |
| PUT (atualizar) | `/produtos/{id}` | `/usuarios/{id}` | `/feedbacks/{id}` |
| DELETE (excluir) | `/produtos/{id}` | `/usuarios/{id}` | `/feedbacks/{id}` |

### Exemplos de JSON

**Cadastrar produto**

```json
{
  "nome": "Notebook",
  "descricao": "Notebook para estudos",
  "preco": 3499.90
}
```

**Cadastrar usuário**

```json
{
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
```

**Cadastrar avaliação**

```json
{
  "produto": { "id": 1 },
  "usuario": { "id": 1 },
  "nota": 5,
  "comentario": "Produto muito bom para estudar."
}
```

Para requisições `POST` e `PUT`, utilize o cabeçalho `Content-Type: application/json`.

### Respostas da API

| Código HTTP | Significado |
| --- | --- |
| `200 OK` | Consulta ou atualização realizada |
| `201 Created` | Registro criado |
| `204 No Content` | Exclusão realizada |
| `400 Bad Request` | Dados ou requisição inválidos |
| `404 Not Found` | Registro não encontrado |
| `409 Conflict` | Conflito de integridade no banco |
| `500 Internal Server Error` | Erro interno |

As mensagens de erro da API são retornadas em JSON, no formato `{"erro":"mensagem"}`.

## Como demonstrar o projeto

Uma sequência simples para apresentar o sistema durante a avaliação:

1. Iniciar a aplicação usando `docker compose up --build -d`.
2. Abrir a interface web e mostrar a navegação entre produtos, usuários e avaliações.
3. Cadastrar um produto e um usuário.
4. Criar uma avaliação vinculada aos dois registros.
5. Demonstrar a edição e a exclusão de uma avaliação.
6. Demonstrar a edição de um produto e de um usuário.
7. Consultar os registros por meio da API REST.
8. Explicar a organização MVC e a persistência dos dados no MySQL.

## Considerações finais

O desenvolvimento do **Feedback de Produtos** permitiu aplicar conceitos de programação Java para web em um sistema completo, integrando interface JSP, regras de negócio, acesso ao banco de dados e endpoints REST.

O uso do Docker também facilita a reprodução do ambiente de execução, o que ajuda nos testes e na apresentação do projeto.

---

<div align="center">

**Feedback de Produtos** · Projeto acadêmico de Aplicações para Internet

[Repositório no GitHub](https://github.com/Aplicacoes-para-internet/Feedback_produtos)

</div>
