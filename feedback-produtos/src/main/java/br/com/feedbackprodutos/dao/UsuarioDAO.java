package br.com.feedbackprodutos.dao;

import br.com.feedbackprodutos.config.ConnectionFactory;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email FROM Usuarios ORDER BY nome";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                usuarios.add(mapear(resultado));
            }
        }
        return usuarios;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email FROM Usuarios WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, email FROM Usuarios WHERE email = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public boolean existeEmail(String email, Integer idIgnorado) throws SQLException {
        String sql = idIgnorado == null
                ? "SELECT 1 FROM Usuarios WHERE email = ? LIMIT 1"
                : "SELECT 1 FROM Usuarios WHERE email = ? AND id <> ? LIMIT 1";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            if (idIgnorado != null) {
                comando.setInt(2, idIgnorado);
            }
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public int salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuarios (nome, email) VALUES (?, ?)";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.executeUpdate();
            try (ResultSet keys = comando.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("O MySQL não retornou o ID do usuário criado.");
                }
                int id = keys.getInt(1);
                usuario.setId(id);
                return id;
            }
        }
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuarios SET nome = ?, email = ? WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.setInt(3, usuario.getId());
            comando.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Usuarios WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        return new Usuario(
                resultado.getInt("id"),
                resultado.getString("nome"),
                resultado.getString("email")
        );
    }
}
