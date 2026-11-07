# Execucao com Docker

Esta configuracao e uma alternativa isolada ao Tomcat e ao MySQL locais. O Docker Compose cria um Tomcat 10.1.59 com a aplicacao e um MySQL 8.0, conectados por uma rede interna. O MySQL nao publica nenhuma porta no Windows; a aplicacao usa o host `db` na rede Docker.

O acesso local pelo Tomcat do Windows permanece em `http://localhost:8080/feedback-produtos/`. A versao em Docker fica em `http://localhost:8081/feedback-produtos/`.

## Requisitos

- Docker Desktop instalado e iniciado, usando containers Linux.
- Docker Compose v2 (`docker compose`).

As credenciais padrao (`docker_root_dev` e `docker_feedback_dev`) sao apenas para desenvolvimento local e nao devem ser usadas em producao. Para personaliza-las, defina `MYSQL_ROOT_PASSWORD` e `DB_PASSWORD` no ambiente antes de iniciar o Compose ou use um arquivo `.env` local, que nao deve ser versionado.

## Construir e iniciar

Abra o PowerShell na pasta que contem `docker-compose.yml` e `schema.sql`. O Docker Desktop deve estar em execucao.

```powershell
docker compose up --build -d
docker compose ps
```

O build Maven usa Java 17 e os caminhos configurados no `pom.xml`, gerando `feedback-produtos.war`. A imagem final usa Tomcat 10.1.59, implantando o WAR com o nome `feedback-produtos.war` para manter o contexto `/feedback-produtos`.

Na primeira inicializacao de um volume MySQL vazio, o MySQL cria `feedback_db` e executa `schema.sql` uma vez. O volume `db_data` persiste os dados entre reinicializacoes. O servico web aguarda o healthcheck do banco, que verifica a existencia da tabela `Produtos`.

Abra:

- Aplicacao: <http://localhost:8081/feedback-produtos/>
- API de produtos: <http://localhost:8081/feedback-produtos/api/produtos>
- API de usuarios: <http://localhost:8081/feedback-produtos/api/usuarios>
- API de feedbacks: <http://localhost:8081/feedback-produtos/api/feedbacks>

## Consultar logs e estado do MySQL

```powershell
docker compose logs -f
docker compose ps
```

No resultado de `docker compose ps`, confira se `db` esta `healthy` e `web` esta em execucao. Para consultar as tabelas inicializadas:

```powershell
docker compose exec db mysql -ufeedback_user -pdocker_feedback_dev -Dfeedback_db -e "SHOW TABLES;"
```

Se definiu um valor personalizado para `DB_PASSWORD`, substitua a senha de exemplo no comando acima. A senha passada com `-p` pode ficar visivel no historico do terminal; para evitar isso, conecte-se ao MySQL interativamente ou use o cliente de sua preferencia.

Para fazer uma consulta HTTP da API pelo PowerShell:

```powershell
$baseUrl = "http://localhost:8081/feedback-produtos/api"
Invoke-RestMethod -Method Get -Uri "$baseUrl/produtos"
Invoke-RestMethod -Method Get -Uri "$baseUrl/usuarios"
Invoke-RestMethod -Method Get -Uri "$baseUrl/feedbacks"
```

Esses comandos sao consultas; nao criam nem removem registros.

## Parar, reiniciar e atualizar

Para parar e remover os containers e a rede, mantendo o volume nomeado:

```powershell
docker compose down
```

Para reiniciar somente a aplicacao:

```powershell
docker compose restart web
```

Depois de alterar o codigo ou o Dockerfile, reconstrua e inicie a aplicacao:

```powershell
docker compose up --build -d
```

**Nao execute `docker compose down -v` sem querer apagar os dados persistidos.** A opcao `-v` remove os volumes do Compose e pode apagar os dados do banco Docker. O comando `docker compose down` (sem `-v`) preserva esses dados.

## Problemas comuns

- **Docker nao conecta ao daemon:** inicie o Docker Desktop e aguarde ficar pronto; depois verifique com `docker info` e tente novamente.
- **Porta 8081 ocupada:** encerre o processo que ocupa essa porta ou altere somente a porta do host no mapeamento `127.0.0.1:8081:8080`. A porta interna do Tomcat continua 8080.
- **MySQL ainda nao esta saudavel:** consulte `docker compose logs db`. O primeiro inicio pode levar mais tempo enquanto o MySQL inicializa o banco e executa o script. O servico web so inicia depois do healthcheck.
- **Tabelas ausentes em um volume ja existente:** o MySQL executa scripts de `/docker-entrypoint-initdb.d` somente quando inicializa um diretorio de dados vazio. Alterar `schema.sql` nao reaplica o script automaticamente a um banco persistido. Confira o banco e aplique apenas as alteracoes necessarias sem apagar o volume.
- **Senha alterada, mas a conexao falha em um volume existente:** as variaveis de inicializacao do MySQL nao atualizam automaticamente usuarios ou senhas ja persistidos. Alinhe a credencial da aplicacao com o usuario existente ou atualize-o conscientemente no MySQL; nao remova o volume como atalho.
- **Aplicacao nao conecta ao banco:** confirme que `DB_URL` usa `db:3306` (nome do servico na rede Docker), nao `localhost`, e verifique `docker compose ps` e os logs de ambos os servicos.
