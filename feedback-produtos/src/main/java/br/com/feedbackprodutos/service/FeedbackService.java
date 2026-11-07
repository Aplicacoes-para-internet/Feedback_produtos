package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.FeedbackDAO;
import br.com.feedbackprodutos.dao.ProdutoDAO;
import br.com.feedbackprodutos.dao.UsuarioDAO;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import br.com.feedbackprodutos.model.Feedback;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private final FeedbackDAO dao = new FeedbackDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Feedback> listar() throws SQLException {
        return dao.listar();
    }

    public Feedback buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Feedback salvar(Feedback feedback) throws SQLException {
        validar(feedback, null);
        int id = dao.salvar(feedback);
        return dao.buscarPorId(id);
    }

    public void atualizar(Feedback feedback) throws SQLException {
        if (feedback == null || feedback.getId() <= 0) {
            throw new IllegalArgumentException("Feedback inválido para atualização.");
        }
        if (dao.buscarPorId(feedback.getId()) == null) {
            throw new RegistroNaoEncontradoException("Feedback não encontrado.");
        }
        validar(feedback, feedback.getId());
        dao.atualizar(feedback);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do feedback inválido.");
        }
        if (dao.buscarPorId(id) == null) {
            throw new RegistroNaoEncontradoException("Feedback não encontrado.");
        }
        dao.excluir(id);
    }

    private void validar(Feedback feedback, Integer idIgnorado) throws SQLException {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback inválido.");
        }
        if (feedback.getProduto() == null || feedback.getProduto().getId() <= 0) {
            throw new IllegalArgumentException("Produto obrigatório.");
        }
        if (feedback.getUsuario() == null || feedback.getUsuario().getId() <= 0) {
            throw new IllegalArgumentException("Usuário obrigatório.");
        }
        if (feedback.getNota() < 1 || feedback.getNota() > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
        }
        if (feedback.getComentario() == null || feedback.getComentario().isBlank()) {
            throw new IllegalArgumentException("O comentário é obrigatório.");
        }

        if (produtoDAO.buscarPorId(feedback.getProduto().getId()) == null) {
            throw new RegistroNaoEncontradoException("Produto não encontrado.");
        }
        if (usuarioDAO.buscarPorId(feedback.getUsuario().getId()) == null) {
            throw new RegistroNaoEncontradoException("Usuário não encontrado.");
        }

        if (idIgnorado != null && dao.buscarPorId(idIgnorado) == null) {
            throw new RegistroNaoEncontradoException("Feedback não encontrado.");
        }
    }
}
