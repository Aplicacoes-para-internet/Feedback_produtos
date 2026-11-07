package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.ProdutoDAO;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import br.com.feedbackprodutos.model.Produto;
import java.sql.SQLException;
import java.util.List;

public class ProdutoService {
    private final ProdutoDAO dao = new ProdutoDAO();

    public List<Produto> listar() throws SQLException {
        return dao.listar();
    }

    public Produto buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Produto salvar(Produto produto) throws SQLException {
        validar(produto);
        dao.salvar(produto);
        return produto;
    }

    public void atualizar(Produto produto) throws SQLException {
        if (produto == null || produto.getId() <= 0) {
            throw new IllegalArgumentException("Produto inválido para atualização.");
        }
        if (dao.buscarPorId(produto.getId()) == null) {
            throw new RegistroNaoEncontradoException("Produto não encontrado.");
        }
        validar(produto);
        dao.atualizar(produto);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do produto inválido.");
        }
        if (dao.buscarPorId(id) == null) {
            throw new RegistroNaoEncontradoException("Produto não encontrado.");
        }
        dao.excluir(id);
    }

    private void validar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto inválido.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (!Double.isFinite(produto.getPreco()) || produto.getPreco() < 0) {
            throw new IllegalArgumentException("O preço deve ser válido e não pode ser negativo.");
        }
    }
}
