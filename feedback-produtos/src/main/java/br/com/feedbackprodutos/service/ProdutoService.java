package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.ProdutoDAO;
import br.com.feedbackprodutos.model.Produto;
import java.sql.SQLException;
import java.util.List;

public class ProdutoService {
	private final ProdutoDAO dao = new ProdutoDAO();

	public List<Produto> listar() throws SQLException { return dao.listar(); }
	public Produto buscarPorId(int id) throws SQLException { return dao.buscarPorId(id); }

	public void salvar(Produto produto) throws SQLException {
		if (produto == null || produto.getNome() == null || produto.getNome().isBlank()) {
			throw new IllegalArgumentException("Informe o nome do produto.");
		}
		if (produto.getPreco() < 0) {
			throw new IllegalArgumentException("O preço não pode ser negativo.");
		}
		dao.salvar(produto);
	}
}
