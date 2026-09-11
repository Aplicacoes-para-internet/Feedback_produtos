package br.com.feedbackprodutos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionFactory {

    private static final Properties PROPRIEDADES = carregarPropriedades();
    private static final String URL = configurar("DB_URL", "db.url");
    private static final String USUARIO = configurar("DB_USER", "db.user");
    private static final String SENHA = configurar("DB_PASSWORD", "db.password");

    private static String configurar(String variavelAmbiente, String chave) {
        String valor = System.getenv(variavelAmbiente);
        if (valor != null && !valor.isBlank()) {
            return valor;
        }
        return PROPRIEDADES.getProperty(chave, "");
    }

    private static Properties carregarPropriedades() {
        Properties propriedades = new Properties();
        try (InputStream arquivo = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (arquivo != null) {
                propriedades.load(arquivo);
            }
        } catch (IOException exception) {
            throw new ExceptionInInitializerError("Não foi possível carregar db.properties: " + exception.getMessage());
        }
        return propriedades;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Driver MySQL não encontrado no classpath.", exception);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
    public static void main(String[] args) {

    try (Connection connection = getConnection()) {

        System.out.println("Conexão com o banco realizada com sucesso!");

    } catch (Exception e) {

        System.out.println("Erro ao conectar com o banco.");
        e.printStackTrace();
    }
}

}