package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Feedback;
import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.service.FeedbackService;
import br.com.feedbackprodutos.service.ProdutoService;
import br.com.feedbackprodutos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/feedback")
public class FeedbackController extends HttpServlet {
	private final FeedbackService feedbackService = new FeedbackService();
	private final ProdutoService produtoService = new ProdutoService();
	private final UsuarioService usuarioService = new UsuarioService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setAttribute("feedbacks", feedbackService.listar());
			request.setAttribute("produtos", produtoService.listar());
			request.setAttribute("usuarios", usuarioService.listar());
			request.getRequestDispatcher("/feedback/listar.jsp").forward(request, response);
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível carregar os feedbacks: " + exception.getMessage());
			request.getRequestDispatcher("/feedback/listar.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			int produtoId = Integer.parseInt(request.getParameter("produtoId"));
			int usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
			int nota = Integer.parseInt(request.getParameter("nota"));
			Produto produto = produtoService.buscarPorId(produtoId);
			Usuario usuario = usuarioService.buscarPorId(usuarioId);
			feedbackService.salvar(new Feedback(0, produto, usuario, nota,
					request.getParameter("comentario"), null));
			response.sendRedirect(request.getContextPath() + "/feedback?sucesso=Feedback+enviado");
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível salvar o feedback: " + exception.getMessage());
			doGet(request, response);
		}
	}
}
