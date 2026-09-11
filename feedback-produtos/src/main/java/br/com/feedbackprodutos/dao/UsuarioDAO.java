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

	private Usuario mapear(ResultSet resultado) throws SQLException {
		return new Usuario(resultado.getInt("id"), resultado.getString("nome"), resultado.getString("email"));
	}
}
