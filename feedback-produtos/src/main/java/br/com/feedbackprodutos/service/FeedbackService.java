package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.FeedbackDAO;
import br.com.feedbackprodutos.model.Feedback;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
	private final FeedbackDAO dao = new FeedbackDAO();

	public List<Feedback> listar() throws SQLException { return dao.listar(); }

	public void salvar(Feedback feedback) throws SQLException {
		if (feedback == null || feedback.getProduto() == null || feedback.getUsuario() == null) {
			throw new IllegalArgumentException("Selecione produto e usuário.");
		}
		if (feedback.getNota() < 1 || feedback.getNota() > 5) {
			throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
		}
		if (feedback.getComentario() == null || feedback.getComentario().isBlank()) {
			throw new IllegalArgumentException("Informe um comentário.");
		}
		dao.salvar(feedback);
	}
}
