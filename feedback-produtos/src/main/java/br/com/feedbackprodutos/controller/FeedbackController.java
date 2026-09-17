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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@WebServlet("/feedback")
public class FeedbackController extends HttpServlet {
	private final FeedbackService feedbackService = new FeedbackService();
	private final ProdutoService produtoService = new ProdutoService();
	private final UsuarioService usuarioService = new UsuarioService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String acao = request.getParameter("acao");
			if ("buscar".equals(acao)) {
				Feedback feedback = feedbackService.buscarPorId(lerId(request));
				if (feedback == null) {
					throw new IllegalArgumentException("Feedback não encontrado.");
				}
				request.setAttribute("feedbacks", Collections.singletonList(feedback));
				request.setAttribute("produtos", produtoService.listar());
				request.setAttribute("usuarios", usuarioService.listar());
				request.getRequestDispatcher("/feedback/listar.jsp").forward(request, response);
				return;
			}
			if ("editar".equals(acao)) {
				int id = lerId(request);
				Feedback feedback = feedbackService.buscarPorId(id);
				if (feedback == null) {
					throw new IllegalArgumentException("Feedback não encontrado.");
				}
				request.setAttribute("feedback", feedback);
				request.setAttribute("produtos", produtoService.listar());
				request.setAttribute("usuarios", usuarioService.listar());
				request.getRequestDispatcher("/feedback/formulario.jsp").forward(request, response);
				return;
			}
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
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				feedbackService.excluir(lerId(request));
				redirecionarComMensagem(request, response, "sucesso", "Feedback excluído com sucesso.");
				return;
			}

			int produtoId = Integer.parseInt(request.getParameter("produtoId"));
			int usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
			int nota = Integer.parseInt(request.getParameter("nota"));
			Produto produto = produtoService.buscarPorId(produtoId);
			Usuario usuario = usuarioService.buscarPorId(usuarioId);
			Feedback feedback = new Feedback("atualizar".equals(acao) ? lerId(request) : 0, produto, usuario, nota,
					request.getParameter("comentario"), null);
			if ("atualizar".equals(acao)) {
				feedbackService.atualizar(feedback);
				redirecionarComMensagem(request, response, "sucesso", "Feedback atualizado com sucesso.");
			} else {
				feedbackService.salvar(feedback);
				redirecionarComMensagem(request, response, "sucesso", "Feedback enviado com sucesso.");
			}
		} catch (Exception exception) {
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", mensagemDeErroExclusao(exception));
			} else if ("atualizar".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", "Não foi possível atualizar o feedback: " + exception.getMessage());
			} else {
				request.setAttribute("erro", "Não foi possível salvar o feedback: " + exception.getMessage());
				doGet(request, response);
			}
		}
	}

	private int lerId(HttpServletRequest request) {
		String valor = request.getParameter("id");
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Informe o ID do feedback.");
		}
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Informe um ID de feedback válido.");
		}
	}

	private String mensagemDeErroExclusao(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			return exception.getMessage();
		}
		return "Não foi possível excluir o feedback.";
	}

	private void redirecionarComMensagem(HttpServletRequest request, HttpServletResponse response,
			String tipo, String mensagem) throws IOException {
		String valor = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
		response.sendRedirect(request.getContextPath() + "/feedback?" + tipo + "=" + valor);
	}
}
