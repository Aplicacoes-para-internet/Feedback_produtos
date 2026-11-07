package br.com.feedbackprodutos.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionFactory {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/feedback_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final Properties PROPERTIES = loadProperties();

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("DB_URL", "db.url", DEFAULT_URL);
        String user = setting("DB_USER", "db.user", "");
        String password = setting("DB_PASSWORD", "db.password", "");

        if (user.isBlank()) {
            throw new SQLException("Configure DB_USER ou db.user para conectar ao MySQL.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Driver MySQL não encontrado no classpath.", exception);
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String environmentVariable, String property, String defaultValue) {
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(property, defaultValue);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        String externalConfig = System.getProperty("feedback.db.config");

        if (externalConfig != null && !externalConfig.isBlank()) {
            try (InputStream input = Files.newInputStream(Path.of(externalConfig))) {
                properties.load(input);
            } catch (IOException exception) {
                throw new ExceptionInInitializerError(
                        "Não foi possível carregar o arquivo externo de configuração do banco: "
                                + exception.getMessage());
            }
            return properties;
        }

        try (InputStream input = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    "Não foi possível carregar db.properties: " + exception.getMessage());
        }
        return properties;
    }
}
