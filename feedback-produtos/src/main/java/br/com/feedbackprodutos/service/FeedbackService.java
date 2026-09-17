package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.FeedbackDAO;
import br.com.feedbackprodutos.model.Feedback;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
	private final FeedbackDAO dao = new FeedbackDAO();

	public List<Feedback> listar() throws SQLException { return dao.listar(); }

	public Feedback buscarPorId(int id) throws SQLException {
		validarId(id);
		return dao.buscarPorId(id);
	}

	public void salvar(Feedback feedback) throws SQLException {
		validarFeedback(feedback);
		dao.salvar(feedback);
	}

	public void atualizar(Feedback feedback) throws SQLException {
		validarFeedback(feedback);
		if (feedback.getId() <= 0 || !dao.atualizar(feedback)) {
			throw new IllegalArgumentException("Feedback não encontrado.");
		}
	}

	public void excluir(int id) throws SQLException {
		validarId(id);
		if (!dao.excluir(id)) {
			throw new IllegalArgumentException("Feedback não encontrado.");
		}
	}

	private void validarFeedback(Feedback feedback) {
		if (feedback == null || feedback.getProduto() == null || feedback.getUsuario() == null) {
			throw new IllegalArgumentException("Selecione produto e usuário.");
		}
		if (feedback.getNota() < 1 || feedback.getNota() > 5) {
			throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
		}
		if (feedback.getComentario() == null || feedback.getComentario().isBlank()) {
			throw new IllegalArgumentException("Informe um comentário.");
		}
	}

	private void validarId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("Informe um ID de feedback válido.");
		}
	}
}
