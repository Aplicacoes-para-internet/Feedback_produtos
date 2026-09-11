package br.com.feedbackprodutos.config;

import java.sql.Connection;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection connection = ConnectionFactory.getConnection()) {

            System.out.println("Conexão com o banco realizada com sucesso!");

        } catch (Exception e) {

            System.out.println("Erro ao conectar com o banco.");
            e.printStackTrace();
        }
    }
}