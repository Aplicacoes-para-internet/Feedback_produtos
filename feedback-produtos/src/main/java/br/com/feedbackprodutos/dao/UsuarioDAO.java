package br.com.feedbackprodutos.dao;

import br.com.feedbackprodutos.config.ConnectionFactory;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
	public List<Usuario> listar() throws SQLException {
		List<Usuario> usuarios = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.getConnection();
				 PreparedStatement comando = conexao.prepareStatement("SELECT id, nome, email FROM Usuarios ORDER BY nome");
			 ResultSet resultado = comando.executeQuery()) {
			while (resultado.next()) {
				usuarios.add(mapear(resultado));
			}
		}
		return usuarios;
	}

	public Usuario buscarPorId(int id) throws SQLException {
		try (Connection conexao = ConnectionFactory.getConnection();
				 PreparedStatement comando = conexao.prepareStatement("SELECT id, nome, email FROM Usuarios WHERE id = ?")) {
			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? mapear(resultado) : null;
			}
		}
	}

	public void salvar(Usuario usuario) throws SQLException {
		String sql = "INSERT INTO Usuarios (nome, email) VALUES (?, ?)";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setString(1, usuario.getNome());
			comando.setString(2, usuario.getEmail());
			comando.executeUpdate();
		}
	}

	public boolean atualizar(Usuario usuario) throws SQLException {
		String sql = "UPDATE Usuarios SET nome = ?, email = ? WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setString(1, usuario.getNome());
			comando.setString(2, usuario.getEmail());
			comando.setInt(3, usuario.getId());
			return comando.executeUpdate() > 0;
		}
	}

	public boolean excluir(int id) throws SQLException {
		String sql = "DELETE FROM Usuarios WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, id);
			return comando.executeUpdate() > 0;
		}
	}

	public boolean possuiFeedbackVinculado(int idUsuario) throws SQLException {
		String sql = "SELECT 1 FROM Feedback WHERE usuario_id = ? LIMIT 1";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, idUsuario);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next();
			}
		}
	}

	public boolean existeEmail(String email, Integer idIgnorado) throws SQLException {
		String sql = "SELECT id FROM Usuarios WHERE email = ?";
		if (idIgnorado != null) {
			sql += " AND id <> ?";
		}
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

	private Usuario mapear(ResultSet resultado) throws SQLException {
		return new Usuario(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"));
	}
}
