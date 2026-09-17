package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.ProdutoDAO;
import br.com.feedbackprodutos.model.Produto;
import java.sql.SQLException;
import java.util.List;

public class ProdutoService {
	private final ProdutoDAO dao = new ProdutoDAO();

	public List<Produto> listar() throws SQLException { return dao.listar(); }
	public Produto buscarPorId(int id) throws SQLException {
		validarId(id);
		return dao.buscarPorId(id);
	}

	public void salvar(Produto produto) throws SQLException {
		validarProduto(produto);
		dao.salvar(produto);
	}

	public void atualizar(Produto produto) throws SQLException {
		validarProduto(produto);
		if (produto.getId() <= 0 || !dao.atualizar(produto)) {
			throw new IllegalArgumentException("Produto não encontrado.");
		}
	}

	public void excluir(int id) throws SQLException {
		validarId(id);
		if (dao.possuiFeedbackVinculado(id)) {
			throw new IllegalArgumentException("Não é possível excluir este produto porque existem feedbacks vinculados a ele.");
		}
		if (!dao.excluir(id)) {
			throw new IllegalArgumentException("Produto não encontrado.");
		}
	}

	private void validarProduto(Produto produto) {
		if (produto == null) {
			throw new IllegalArgumentException("Produto inválido.");
		}
		if (produto.getNome() == null || produto.getNome().isBlank()) {
			throw new IllegalArgumentException("Informe o nome do produto.");
		}
		if (produto.getPreco() < 0) {
			throw new IllegalArgumentException("O preço não pode ser negativo.");
		}
	}

	private void validarId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("Informe um ID de produto válido.");
		}
	}
}
