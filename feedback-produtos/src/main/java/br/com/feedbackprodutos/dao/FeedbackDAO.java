package br.com.feedbackprodutos.dao;

import br.com.feedbackprodutos.config.ConnectionFactory;
import br.com.feedbackprodutos.model.Feedback;
import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
	public List<Feedback> listar() throws SQLException {
		String sql = "SELECT f.id, f.nota, f.comentario, f.data_envio, "
				+ "p.id produto_id, p.nome produto_nome, p.descricao produto_descricao, p.preco produto_preco, "
				+ "u.id usuario_id, u.nome usuario_nome, u.email usuario_email "
				+ "FROM Feedback f JOIN Produtos p ON p.id = f.produto_id JOIN Usuarios u ON u.id = f.usuario_id "
				+ "ORDER BY f.data_envio DESC";
		List<Feedback> feedbacks = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql);
			 ResultSet resultado = comando.executeQuery()) {
			while (resultado.next()) {
				feedbacks.add(mapear(resultado));
			}
		}
		return feedbacks;
	}

	public void salvar(Feedback feedback) throws SQLException {
		String sql = "INSERT INTO Feedback (produto_id, usuario_id, nota, comentario) VALUES (?, ?, ?, ?)";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, feedback.getProduto().getId());
			comando.setInt(2, feedback.getUsuario().getId());
			comando.setInt(3, feedback.getNota());
			comando.setString(4, feedback.getComentario());
			comando.executeUpdate();
		}
	}

	public Feedback buscarPorId(int id) throws SQLException {
		String sql = "SELECT f.id, f.nota, f.comentario, f.data_envio, "
				+ "p.id produto_id, p.nome produto_nome, p.descricao produto_descricao, p.preco produto_preco, "
				+ "u.id usuario_id, u.nome usuario_nome, u.email usuario_email "
				+ "FROM Feedback f JOIN Produtos p ON p.id = f.produto_id JOIN Usuarios u ON u.id = f.usuario_id "
				+ "WHERE f.id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? mapear(resultado) : null;
			}
		}
	}

	public boolean atualizar(Feedback feedback) throws SQLException {
		String sql = "UPDATE Feedback SET produto_id = ?, usuario_id = ?, nota = ?, comentario = ? WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, feedback.getProduto().getId());
			comando.setInt(2, feedback.getUsuario().getId());
			comando.setInt(3, feedback.getNota());
			comando.setString(4, feedback.getComentario());
			comando.setInt(5, feedback.getId());
			return comando.executeUpdate() > 0;
		}
	}

	public boolean excluir(int id) throws SQLException {
		String sql = "DELETE FROM Feedback WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, id);
			return comando.executeUpdate() > 0;
		}
	}

	private Feedback mapear(ResultSet resultado) throws SQLException {
		Produto produto = new Produto(resultado.getInt("produto_id"), resultado.getString("produto_nome"),
				resultado.getString("produto_descricao"), resultado.getDouble("produto_preco"));
		Usuario usuario = new Usuario(resultado.getInt("usuario_id"), resultado.getString("usuario_nome"),
				resultado.getString("usuario_email"));
		Timestamp data = resultado.getTimestamp("data_envio");
		return new Feedback(resultado.getInt("id"), produto, usuario, resultado.getInt("nota"),
				resultado.getString("comentario"), data == null ? null : data.toLocalDateTime());
	}
}
