package br.com.feedbackprodutos.dao;

import br.com.feedbackprodutos.config.ConnectionFactory;
import br.com.feedbackprodutos.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
	public List<Produto> listar() throws SQLException {
		String sql = "SELECT id, nome, descricao, preco FROM Produtos ORDER BY nome";
		List<Produto> produtos = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql);
			 ResultSet resultado = comando.executeQuery()) {
			while (resultado.next()) {
				produtos.add(mapear(resultado));
			}
		}
		return produtos;
	}

	public Produto buscarPorId(int id) throws SQLException {
		String sql = "SELECT id, nome, descricao, preco FROM Produtos WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? mapear(resultado) : null;
			}
		}
	}

	public void salvar(Produto produto) throws SQLException {
		String sql = "INSERT INTO Produtos (nome, descricao, preco) VALUES (?, ?, ?)";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setString(1, produto.getNome());
			comando.setString(2, produto.getDescricao());
			comando.setDouble(3, produto.getPreco());
			comando.executeUpdate();
		}
	}

	public boolean atualizar(Produto produto) throws SQLException {
		String sql = "UPDATE Produtos SET nome = ?, descricao = ?, preco = ? WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setString(1, produto.getNome());
			comando.setString(2, produto.getDescricao());
			comando.setDouble(3, produto.getPreco());
			comando.setInt(4, produto.getId());
			return comando.executeUpdate() > 0;
		}
	}

	public boolean excluir(int id) throws SQLException {
		String sql = "DELETE FROM Produtos WHERE id = ?";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, id);
			return comando.executeUpdate() > 0;
		}
	}

	public boolean possuiFeedbackVinculado(int idProduto) throws SQLException {
		String sql = "SELECT 1 FROM Feedback WHERE produto_id = ? LIMIT 1";
		try (Connection conexao = ConnectionFactory.getConnection();
			 PreparedStatement comando = conexao.prepareStatement(sql)) {
			comando.setInt(1, idProduto);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next();
			}
		}
	}

	private Produto mapear(ResultSet resultado) throws SQLException {
		return new Produto(resultado.getInt("id"), resultado.getString("nome"),
				resultado.getString("descricao"), resultado.getDouble("preco"));
	}
}
