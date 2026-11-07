Feedback de Produtos
Projeto acadêmico | Disciplina: Aplicações para Internet

O Feedback de Produtos é uma aplicação web desenvolvida em Java para organizar produtos, usuários e as avaliações feitas sobre esses produtos. A proposta é reunir, em um único sistema, o cadastro das informações e a consulta dos feedbacks, permitindo acompanhar a opinião dos usuários de forma simples.

O projeto foi construído como atividade prática da disciplina de Aplicações para Internet, com o objetivo de aplicar conceitos de desenvolvimento web, arquitetura MVC, persistência de dados e API REST. Além da interface web com JSP, o sistema disponibiliza endpoints que permitem acessar e manipular os dados em formato JSON.
1. Objetivo do projeto
Desenvolver uma aplicação web conectada a um banco de dados relacional, com operações de cadastro, consulta, atualização e exclusão de produtos, usuários e avaliações.

Na prática, o projeto permite exercitar a separação de responsabilidades entre as camadas da aplicação, a comunicação HTTP e a integração entre Java e MySQL.
2. Tecnologias utilizadas
Tecnologia	Utilização no projeto
Java 17	Linguagem principal da aplicação
Jakarta Servlet 6.0	Recebimento e tratamento das requisições HTTP
JSP	Construção das páginas da interface web
Maven	Gerenciamento de dependências e geração do arquivo WAR
Apache Tomcat 10.1.59	Servidor de aplicação
MySQL 8.0	Armazenamento dos dados
JDBC / MySQL Connector/J	Comunicação entre Java e MySQL
Jackson	Conversão entre objetos Java e JSON
Docker e Docker Compose	Execução da aplicação e do banco em containers


3. Funcionalidades
O sistema trabalha com três entidades principais:
- Produtos: cadastro, listagem, consulta por ID, edição e exclusão.
- Usuários: cadastro, listagem, consulta por ID, edição e exclusão.
- Avaliações (feedbacks): registro de uma nota de 1 a 5 e de um comentário, associados a um produto e a um usuário; consulta, edição e exclusão.
As operações CRUD estão disponíveis na API REST. A interface JSP oferece a navegação e o gerenciamento dos registros. Os botões de edição e exclusão pela interface dependem da versão atualizada dessas páginas estar presente no projeto.

Regras importantes: um e-mail não pode ser repetido; a nota deve estar entre 1 e 5; e uma avaliação deve estar vinculada a um produto e a um usuário existentes. Como o banco utiliza chaves estrangeiras, a exclusão de produtos ou usuários com avaliações associadas pode ser impedida até que essas avaliações sejam removidas.
4. Organização e arquitetura
A aplicação segue uma organização baseada em MVC, com separação das responsabilidades:
- Model: representa as entidades Produto, Usuario e Feedback.
- Controller: recebe as requisições e direciona o processamento.
- Service: concentra validações e regras de negócio.
- DAO: realiza o acesso ao MySQL por meio de JDBC e consultas parametrizadas.
- View (JSP): apresenta os dados e os formulários ao usuário.
Essa separação facilita a manutenção do código e evita concentrar regras de negócio diretamente nas páginas.
Estrutura principal
Feedback_produtos/
├── feedback-produtos/
│   └── src/main/
│       ├── java/br/com/feedbackprodutos/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── exception/
│       │   ├── model/
│       │   └── service/
│       ├── resources/
│       └── webapp/
│           ├── css/
│           ├── produto/
│           ├── usuario/
│           ├── feedback/
│           ├── WEB-INF/
│           └── index.jsp
├── Dockerfile
├── docker-compose.yml
├── schema.sql
├── pom.xml
└── README.md
O pom.xml está na raiz do repositório e aponta para os arquivos Java e JSP dentro da pasta feedback-produtos/.
5. Como executar com Docker (recomendado)
Esta é a forma mais prática de executar o projeto para avaliação, pois o Docker Compose prepara dois serviços: o MySQL e a aplicação Java no Tomcat. Não é necessário instalar o MySQL, o Maven ou o Tomcat separadamente na máquina que vai executar os containers.
Pré-requisitos
- Docker Desktop instalado e em execução (no Windows).
- Docker Compose disponível, normalmente incluído no Docker Desktop.
- Acesso à internet na primeira execução para baixar as imagens e dependências.
Passo 1: obter o projeto
Clone o repositório e entre na pasta principal:
git clone https://github.com/Aplicacoes-para-internet/Feedback_produtos.git
cd Feedback_produtos
Se você já baixou o projeto em ZIP, basta extrair os arquivos e abrir um terminal na pasta que contém docker-compose.yml.
Passo 2: configurar a senha do banco (recomendado)
O arquivo docker-compose.yml aceita as variáveis DB_PASSWORD e MYSQL_ROOT_PASSWORD. Para definir senhas próprias, crie um arquivo chamado .env na raiz do projeto:
DB_PASSWORD=defina_uma_senha_local
MYSQL_ROOT_PASSWORD=defina_outra_senha_local
Esses valores são apenas exemplos: escolha suas próprias senhas. Não envie o arquivo .env ao GitHub. Se o arquivo não existir, o Compose utiliza senhas padrão de desenvolvimento definidas na configuração; elas não são adequadas para ambientes públicos ou de produção.
Passo 3: construir e iniciar os containers
Na raiz do projeto, execute:
docker compose up --build -d
Esse comando constrói a aplicação Java com Maven, gera o arquivo WAR, prepara o Tomcat e inicia o MySQL. Na primeira execução, o banco é inicializado com o script schema.sql.

Aguarde alguns instantes para que o MySQL fique saudável e a aplicação termine de iniciar.
Passo 4: acessar o sistema
Recurso	Endereço
Página inicial (JSP)	http://localhost:8081/feedback-produtos/
API de produtos	http://localhost:8081/feedback-produtos/api/produtos
API de usuários	http://localhost:8081/feedback-produtos/api/usuarios
API de avaliações	http://localhost:8081/feedback-produtos/api/feedbacks


Por que a porta 8081? O Tomcat utiliza a porta 8080 dentro do container, mas o docker-compose.yml publica essa porta como 8081 no computador. Isso permite manter um Tomcat local na porta 8080, caso necessário.
Passo 5: verificar se está funcionando
Confira o estado dos serviços:
docker compose ps
Para acompanhar as mensagens da aplicação:
docker compose logs -f web
Para verificar as mensagens do banco:
docker compose logs -f db
Use Ctrl+C para sair da visualização dos logs sem desligar os containers.
Como parar ou reiniciar
Para parar e remover os containers, preservando o volume do banco:
docker compose down
Para iniciar novamente:
docker compose up -d
Os dados do MySQL ficam armazenados no volume Docker db_data. Não utilize docker compose down -v se quiser preservar os registros, pois a opção -v remove também os volumes do projeto.
Observação: o schema.sql é aplicado automaticamente pelo MySQL apenas na inicialização de um diretório de dados vazio. Se o volume já existir, alterações posteriores nesse script não são executadas automaticamente.

6. Como executar sem Docker (Windows)
Esta alternativa é útil para quem deseja executar o projeto diretamente no computador.
Pré-requisitos
- JDK 17;
- Maven;
- MySQL em execução;
- Apache Tomcat 10.1.x.
Preparar o banco
Abra o arquivo schema.sql no MySQL Workbench (ou outro cliente MySQL) e execute o script. Ele cria o banco feedback_db e as tabelas Produtos, Usuarios e Feedback, caso ainda não existam.
Configurar a conexão
No PowerShell, defina as variáveis de ambiente com os dados do seu MySQL:
$env:DB_URL = "jdbc:mysql://localhost:3306/feedback_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
$env:DB_USER = "SEU_USUARIO_MYSQL"
$senha = Read-Host "Senha do MySQL" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $senha).Password
As variáveis devem estar disponíveis para o processo que inicia o Tomcat. Evite colocar credenciais reais no código ou em arquivos versionados.
Compilar e publicar no Tomcat
Na raiz do projeto:
mvn clean package
O Maven gera o arquivo target/feedback-produtos.war. Copie esse arquivo para a pasta webapps do Tomcat e inicie o servidor:
$tomcatHome = "C:\apache-tomcat-10.1.59" # ajuste para a pasta instalada
Copy-Item .\target\feedback-produtos.war "$tomcatHome\webapps\"
& "$tomcatHome\bin\startup.bat"
Acesse http://localhost:8080/feedback-produtos/.

Para encerrar:
& "$tomcatHome\bin\shutdown.bat"
7. API REST
A API disponibiliza as operações básicas de CRUD para cada entidade.
Método	Produtos	Usuários	Avaliações
GET	/api/produtos	/api/usuarios	/api/feedbacks
GET por ID	/api/produtos/{id}	/api/usuarios/{id}	/api/feedbacks/{id}
POST	/api/produtos	/api/usuarios	/api/feedbacks
PUT por ID	/api/produtos/{id}	/api/usuarios/{id}	/api/feedbacks/{id}
DELETE por ID	/api/produtos/{id}	/api/usuarios/{id}	/api/feedbacks/{id}


Os caminhos acima devem ser acrescentados à URL-base:
- Docker: http://localhost:8081/feedback-produtos
- Tomcat local: http://localhost:8080/feedback-produtos
Para operações POST e PUT, envie os dados em JSON com o cabeçalho Content-Type: application/json.
Exemplos de requisições
Cadastrar um produto:
POST /feedback-produtos/api/produtos
Content-Type: application/json

{
  "nome": "Notebook",
  "descricao": "Notebook para estudos",
  "preco": 3499.90
}
Cadastrar um usuário:
POST /feedback-produtos/api/usuarios
Content-Type: application/json

{
  "nome": "Maria Silva",
  "email": "maria@example.com"
}
Cadastrar uma avaliação:
POST /feedback-produtos/api/feedbacks
Content-Type: application/json

{
  "produto": { "id": 1 },
  "usuario": { "id": 1 },
  "nota": 5,
  "comentario": "Produto atendeu às expectativas."
}
No exemplo da avaliação, os IDs 1 precisam corresponder a registros que realmente existam no banco.
Respostas da API
A API utiliza códigos HTTP para indicar o resultado das operações, como 200 para consultas e atualizações, 201 para cadastros e 204 para exclusões concluídas. Também pode retornar 400 para dados inválidos, 404 para registros não encontrados, 409 para conflitos de integridade e 500 para falhas internas.

As respostas de erro seguem o formato JSON:
{
  "erro": "Mensagem explicando o problema"
}
8. Banco de dados
O script oficial de criação do banco é o schema.sql, localizado na raiz do projeto. Ele define:
Tabela	Principais informações
Produtos	ID, nome, descrição e preço
Usuarios	ID, nome e e-mail único
Feedback	ID, produto, usuário, nota, comentário e data de envio


A tabela Feedback possui chaves estrangeiras que relacionam cada avaliação a um produto e a um usuário. A nota é limitada ao intervalo de 1 a 5.

O script utiliza CREATE ... IF NOT EXISTS, de modo que não apaga registros existentes durante sua execução normal. O arquivo antigo de banco, caso esteja presente em arquivos-antigos/, não deve substituir o schema.sql atual.
9. Sugestão de roteiro para avaliação do projeto
Para demonstrar o funcionamento ao professor:
1. Inicie os containers com docker compose up --build -d.
2. Abra a página inicial no navegador.
3. Cadastre um produto e um usuário.
4. Registre uma avaliação vinculada a eles.
5. Consulte os dados pela interface e pelas rotas REST.
6. Demonstre a edição e a exclusão, se a versão da interface instalada já contiver essas ações.
7. Mostre a organização das camadas controller, service, dao e model.
8. Finalize com docker compose down, preservando os dados.
Esse roteiro apresenta tanto a experiência do usuário quanto os principais conceitos técnicos trabalhados na disciplina.
10. Observações e limitações
- O projeto é acadêmico, voltado à aprendizagem, e não deve ser tratado como um sistema pronto para produção.
- Não há uma suíte de testes automatizados configurada no projeto analisado. A compilação e os testes manuais devem ser realizados antes da entrega.
- Os dados persistem no volume do MySQL quando o projeto é executado com Docker.
- Arquivos de credenciais locais, como .env e db.properties, não devem ser publicados no repositório.
- A exclusão de um produto ou usuário pode ser bloqueada pelo banco quando existem avaliações vinculadas.
11. Repositório
GitHub: https://github.com/Aplicacoes-para-internet/Feedback_produtos
Projeto desenvolvido para a disciplina de Aplicações para Internet, com foco na prática de Java web, APIs REST, arquitetura MVC, banco de dados relacional e execução com Docker.