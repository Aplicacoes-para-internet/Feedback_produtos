# Feedback de Produtos

Aplicação acadêmica de Aplicações para Internet que permite cadastrar produtos e usuários, registrar feedbacks e consultar esses dados por uma API REST. O projeto mantém as páginas JSP existentes e usa MySQL para persistência.

## Tecnologias

- Java 17

- Maven e empacotamento WAR

- JDBC com MySQL Connector/J

- MySQL

- Jakarta Servlet 6.0

- Jackson (JSON e suporte a `LocalDateTime`)

- Apache Tomcat 10.1.59

## Arquitetura

O projeto segue MVC com camadas:

- **Controller** (`controller`): Servlets que recebem requisições HTTP, interpretam JSON, selecionam a operação e definem o status e a resposta.

- **Service** (`service`): aplica validações e regras de negócio antes de chamar a persistência.

- **DAO** (`dao`): executa consultas SQL parametrizadas via JDBC e converte os resultados em Models.

- **Model** (`model`): representa `Produto`, `Usuario` e `Feedback`, usados nas camadas e serializados em JSON.

As rotas JSP existentes continuam disponíveis separadamente das rotas `/api/...`.

## Estrutura do projeto

```text

.

├── feedback-produtos/

│   └── src/main/

│       ├── java/br/com/feedbackprodutos/

│       │   ├── config/       # conexão JDBC e teste de conexão

│       │   ├── controller/   # Servlets MVC e API

│       │   ├── dao/          # acesso JDBC a Produtos, Usuarios e Feedback

│       │   ├── exception/    # exceção de registro não encontrado

│       │   ├── model/        # Produto, Usuario e Feedback

│       │   └── service/      # regras de negócio e validações

│       ├── resources/        # db.properties.example; db.properties local é ignorado

│       └── webapp/

│           ├── WEB-INF/web.xml

│           ├── css/

│           ├── feedback/

│           ├── produto/

│           ├── usuario/

│           └── index.jsp

├── arquivos-antigos/

│   └── BANCO DE DADOS.sql    # script legado; não usar para instalar o banco atual

├── schema.sql                # script oficial do banco feedback_db

├── pom.xml

├── Dockerfile                # configuração opcional

├── docker-compose.yml        # configuração opcional

└── .gitignore

```

O `pom.xml` fica na raiz e configura os diretórios Java, resources e webapp dentro de `feedback-produtos/`. Não há atualmente uma suíte de testes automatizados no projeto.

## Banco de dados MySQL

O script oficial é [`schema.sql`](./schema.sql). No MySQL Workbench, abra esse arquivo e execute o script conectado ao servidor MySQL. Ele cria o banco `feedback_db` (se ainda não existir) e as tabelas `Produtos`, `Usuarios` e `Feedback`, incluindo chaves estrangeiras e a restrição de nota de 1 a 5.

O script usa `CREATE ... IF NOT EXISTS`; não apaga dados nem substitui tabelas existentes. Se já houver tabelas com estrutura diferente, compare-as com o script antes de qualquer alteração. O arquivo legado está em `arquivos-antigos/` e usa outro banco (`feedback_produtos`), por isso não deve ser usado.

Configure a conexão por variáveis de ambiente. `DB_URL` é opcional e, se omitida, usa o endereço local padrão da aplicação; configure `DB_USER` e `DB_PASSWORD` para sua instalação. Não coloque credenciais reais em arquivos versionados.
Executar com Docker 🐳
O Docker é uma alternativa à execução local no Windows. Ele inicia o MySQL 8 e o Tomcat com a aplicação Java em containers, sem exigir a instalação manual desses serviços. Os dados do banco do Docker ficam separados do MySQL instalado no computador.
Pré-requisitos
- Docker Desktop instalado e em execução, com suporte ao Docker Compose.
- Portas 8081 disponível no computador.
- Terminal aberto na raiz do projeto, onde está o arquivo docker-compose.yml.
1. Baixar o projeto (se ainda não estiver no computador)
git clone https://github.com/Aplicacoes-para-internet/Feedback_produtos.git
cd Feedback_produtos
2. Construir e iniciar os containers
docker compose up --build -d
Na primeira execução, o Docker baixa as imagens necessárias, compila a aplicação e cria o banco feedback_db. O arquivo schema.sql é aplicado na inicialização de um volume de banco novo. Aguarde o MySQL ficar saudável e o Tomcat iniciar.
3. Verificar se os serviços estão ativos
docker compose ps
O esperado é que o serviço db apareça como healthy e o serviço web como Up, com a porta 8081 do computador encaminhada para a porta 8080 do container.
4. Acessar a aplicação
- Interface JSP: http://localhost:8081/feedback-produtos/
- API REST (produtos): http://localhost:8081/feedback-produtos/api/produtos
- API REST (usuários): http://localhost:8081/feedback-produtos/api/usuarios
- API REST (feedbacks): http://localhost:8081/feedback-produtos/api/feedbacks
A porta 8081 é usada pelo Docker para não conflitar com o Tomcat local, que pode continuar usando a porta 8080. Para testar o CRUD documentado abaixo usando Docker, troque a variável PowerShell para:
$baseUrl = "http://localhost:8081/feedback-produtos/api"
5. Consultar os logs, se necessário
docker compose logs -f web
Para ver os logs do banco:
docker compose logs -f db
Use Ctrl+C para sair da visualização dos logs sem desligar os containers.
6. Parar e iniciar novamente
Para parar os containers sem apagar os dados do banco:
docker compose down
Para iniciar novamente:
docker compose up -d
Atenção: não use docker compose down -v para parar normalmente, pois a opção -v remove os volumes do projeto e pode apagar os dados persistidos no MySQL do Docker. Também não é necessário executar o schema.sql manualmente no MySQL local para usar a versão Docker.
## Compilar e executar no Windows

Abra o PowerShell na raiz do repositório, com JDK 17, Maven e MySQL instalados. Defina as variáveis no mesmo terminal usado para iniciar o Tomcat:

```powershell

Env: Db URL = "jdbc:mysql://localhost:3306/feedback_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"

Env: Db User = "SEU_USUARIO_MYSQL"

$senha = Read-Host "Senha do MySQL" -AsSecureString

Env: Db Password = [System.Net.NetworkCredential]::new("", $senha).Password

```

Compile e empacote:

```powershell

mvn clean package

```

O arquivo gerado é `target/feedback-produtos.war`. Para implantá-lo no Tomcat 10.1.59:

```powershell

$tomcatHome = "C:\apache-tomcat-10.1.59"

Copy-Item .\target\feedback-produtos.war "$tomcatHome\webapps\"

& "$tomcatHome\bin\startup.bat"

```

Após o Tomcat implantar o WAR, a aplicação fica disponível em:

- Interface JSP: `http://localhost:8080/feedback-produtos/`

- API REST: `http://localhost:8080/feedback-produtos/api`

Para encerrar o Tomcat:

```powershell

& "$tomcatHome\bin\shutdown.bat"

```

As variáveis definidas no PowerShell não são permanentes: defina-as novamente em cada terminal usado para iniciar o Tomcat. A configuração alternativa por arquivo externo `db.properties` também é suportada pela aplicação; mantenha esse arquivo privado e fora do Git.

## API REST

Use a URL-base `http://localhost:8080/feedback-produtos/api` (ajuste o contexto se o nome do WAR mudar) e envie `Content-Type: application/json` em requisições POST e PUT.

### Produtos

| Método | Endpoint | Comportamento e sucesso |

|---|---|---|

| GET | `/api/produtos` | Lista produtos (`200`, array JSON). |

| GET | `/api/produtos/{id}` | Consulta um produto (`200`, objeto JSON). |

| POST | `/api/produtos` | Cria produto (`201`, objeto com ID gerado). |

| PUT | `/api/produtos/{id}` | Atualiza produto (`200`, objeto atualizado). |

| DELETE | `/api/produtos/{id}` | Exclui produto (`204`, sem corpo). |

Corpo para POST e PUT:

```json

{

  "nome": "Notebook",

  "descricao": "Notebook para trabalho",

  "preco": 3499.90

}

```

Resposta de exemplo:

```json

{

  "id": 1,

  "nome": "Notebook",

  "descricao": "Notebook para trabalho",

  "preco": 3499.9

}

```

Nome não pode estar vazio e preço deve ser finito e não negativo. Descrição pode ser nula.

### Usuários

| Método | Endpoint | Comportamento e sucesso |

|---|---|---|

| GET | `/api/usuarios` | Lista usuários (`200`, array JSON). |

| GET | `/api/usuarios/{id}` | Consulta um usuário (`200`, objeto JSON). |

| POST | `/api/usuarios` | Cria usuário (`201`, objeto com ID gerado). |

| PUT | `/api/usuarios/{id}` | Atualiza usuário (`200`, objeto atualizado). |

| DELETE | `/api/usuarios/{id}` | Exclui usuário (`204`, sem corpo). |

Corpo para POST e PUT:

```json

{

  "nome": "Maria Silva",

  "email": "maria@example.com"

}

```

Resposta de exemplo:

```json

{

  "id": 1,

  "nome": "Maria Silva",

  "email": "maria@example.com"

}

```

Nome e e-mail são obrigatórios. O serviço valida o formato do e-mail e impede duplicatas; o banco também aplica unicidade.

### Feedbacks

| Método | Endpoint | Comportamento e sucesso |

|---|---|---|

| GET | `/api/feedbacks` | Lista feedbacks (`200`, array JSON). |

| GET | `/api/feedbacks/{id}` | Consulta um feedback (`200`, objeto JSON). |

| POST | `/api/feedbacks` | Cria feedback (`201`, objeto criado). |

| PUT | `/api/feedbacks/{id}` | Atualiza feedback (`200`, objeto atualizado). |

| DELETE | `/api/feedbacks/{id}` | Exclui feedback (`204`, sem corpo). |

Em POST e PUT, informe os IDs do produto e do usuário:

```json

{

  "produto": { "id": 1 },

  "usuario": { "id": 1 },

  "nota": 5,

  "comentario": "Produto excelente, atendeu às expectativas."

}

```

A resposta contém os objetos completos relacionados e `dataEnvio`:

```json

{

  "id": 1,

  "produto": {

    "id": 1,

    "nome": "Notebook",

    "descricao": "Notebook para trabalho",

    "preco": 3499.9

  },

  "usuario": {

    "id": 1,

    "nome": "Maria Silva",

    "email": "maria@example.com"

  },

  "nota": 5,

  "comentario": "Produto excelente, atendeu às expectativas.",

  "dataEnvio": "2026-11-06T23:23:32"

}

```

Produto e usuário devem existir; nota deve estar entre 1 e 5; comentário não pode estar vazio.

### Respostas de erro

As respostas de erro da API usam JSON no formato `{"erro":"mensagem"}`:

| Status | Uso |

|---|---|

| `400 Bad Request` | ID inválido, JSON/Content-Type inválido ou dados que não passam pela validação. |

| `404 Not Found` | Registro solicitado não existe ou a rota de criação não corresponde à coleção. |

| `409 Conflict` | Conflito de integridade reportado pelo MySQL, por exemplo, exclusão impedida por feedback relacionado ou violação de unicidade no banco. E-mail duplicado detectado previamente pelo Service retorna `400`. |

| `500 Internal Server Error` | Falha interna ou de acesso ao banco; a resposta não expõe detalhes internos. |

## Teste manual do CRUD

Com o MySQL e o Tomcat em execução, defina a URL-base no PowerShell:

```powershell

$baseUrl = "http://localhost:8080/feedback-produtos/api"

```

Execute os passos em ordem. Eles criam dados de demonstração e removem os registros criados; não use IDs existentes. Se a exclusão de um produto ou usuário relacionado for tentada antes de excluir o feedback, a chave estrangeira pode impedir a operação.

```powershell

# CREATE: produto

$produto = Invoke-RestMethod -Method Post -Uri "$baseUrl/produtos" `

  -ContentType "application/json" `

  -Body '{"nome":"Notebook de teste","descricao":"CRUD manual","preco":100.00}'

# READ: lista e consulta por ID

Invoke-RestMethod -Method Get -Uri "$baseUrl/produtos"

Invoke-RestMethod -Method Get -Uri "$baseUrl/produtos/$($produto.id)"

# UPDATE: alterar o produto criado

Invoke-RestMethod -Method Put -Uri "$baseUrl/produtos/$($produto.id)" `

  -ContentType "application/json" `

  -Body '{"nome":"Notebook atualizado","descricao":"CRUD manual","preco":120.00}'

# CREATE e READ: usuário

$usuario = Invoke-RestMethod -Method Post -Uri "$baseUrl/usuarios" `

  -ContentType "application/json" `

  -Body '{"nome":"Usuario de teste","email":"usuario-crud@example.com"}'

Invoke-RestMethod -Method Get -Uri "$baseUrl/usuarios"

Invoke-RestMethod -Method Get -Uri "$baseUrl/usuarios/$($usuario.id)"

# UPDATE: usuário

Invoke-RestMethod -Method Put -Uri "$baseUrl/usuarios/$($usuario.id)" `

  -ContentType "application/json" `

  -Body '{"nome":"Usuario atualizado","email":"usuario-crud@example.com"}'

# CREATE e READ: feedback relacionado aos registros criados acima

$feedbackBody = @{

  produto = @{ id = $produto.id }

  usuario = @{ id = $usuario.id }

  nota = 5

  comentario = "Feedback criado no teste manual."

} | ConvertTo-Json -Depth 4

$feedback = Invoke-RestMethod -Method Post -Uri "$baseUrl/feedbacks" `

  -ContentType "application/json" -Body $feedbackBody

Invoke-RestMethod -Method Get -Uri "$baseUrl/feedbacks"

Invoke-RestMethod -Method Get -Uri "$baseUrl/feedbacks/$($feedback.id)"

# UPDATE: feedback

$feedbackBody = @{

  produto = @{ id = $produto.id }

  usuario = @{ id = $usuario.id }

  nota = 4

  comentario = "Feedback atualizado no teste manual."

} | ConvertTo-Json -Depth 4

Invoke-RestMethod -Method Put -Uri "$baseUrl/feedbacks/$($feedback.id)" `

  -ContentType "application/json" -Body $feedbackBody

# DELETE: exclua primeiro o feedback para respeitar as chaves estrangeiras

Invoke-RestMethod -Method Delete -Uri "$baseUrl/feedbacks/$($feedback.id)"

Invoke-RestMethod -Method Delete -Uri "$baseUrl/produtos/$($produto.id)"

Invoke-RestMethod -Method Delete -Uri "$baseUrl/usuarios/$($usuario.id)"

```

Uma resposta `204` não tem corpo. Esse roteiro documenta como testar; ele não substitui a execução real das requisições contra a aplicação e o banco.

## Publicar no GitHub

Antes de publicar, revise `git status` e confirme que nenhum arquivo local de credenciais (`db.properties`, `.env` ou equivalente), dado pessoal ou artefato de build será enviado. O `.gitignore` exclui as configurações locais e as pastas `target/`. Nunca publique senhas no README, no código ou no histórico do Git.

Após criar um repositório vazio no GitHub, configure o endereço remoto e publique a branch desejada:

```powershell

git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git

git add .

git status

git commit -m "Documenta e prepara o projeto Feedback de Produtos"

git push -u origin main

```

Se `origin` já existir, confira-o com `git remote -v` em vez de adicioná-lo novamente. Revise cuidadosamente o que será incluído antes de confirmar o commit e o push.