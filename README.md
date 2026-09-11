# Feedback de Produtos

Aplicação web acadêmica em Java 17 com MVC, DAO, Service, JSP e MySQL.

## Banco de dados

1. Execute `schema.sql` no MySQL.
2. Configure as variáveis de ambiente abaixo antes de iniciar o servidor. Os valores padrão são `localhost:3306/feedback_produtos`, usuário `root` e senha vazia.

- `DB_URL`: URL JDBC completa
- `DB_USER`: usuário do banco
- `DB_PASSWORD`: senha do banco

## Execução

Gere o WAR com:

```bash
mvn clean package
```

Copie `target/feedback-produtos.war` para o Tomcat 10.1+ (Jakarta Servlet 6) e acesse:

`http://localhost:8080/feedback-produtos/`

A página inicial permite cadastrar produtos e usuários e enviar/listar feedbacks.
