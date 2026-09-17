package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebServlet("/usuarios")
public class UsuarioController extends HttpServlet {
	private final UsuarioService service = new UsuarioService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String acao = request.getParameter("acao");
			if ("buscar".equals(acao)) {
				Usuario usuario = service.buscarPorId(lerId(request));
				if (usuario == null) {
					throw new IllegalArgumentException("Usuário não encontrado.");
				}
				request.setAttribute("usuarios", Collections.singletonList(usuario));
				request.getRequestDispatcher("/usuario/listar.jsp").forward(request, response);
				return;
			}
			if ("editar".equals(acao)) {
				int id = lerId(request);
				Usuario usuario = service.buscarPorId(id);
				if (usuario == null) {
					throw new IllegalArgumentException("Usuário não encontrado.");
				}
				request.setAttribute("usuario", usuario);
				request.getRequestDispatcher("/usuario/formulario.jsp").forward(request, response);
				return;
			}
			request.setAttribute("usuarios", service.listar());
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível carregar os usuários: " + exception.getMessage());
		}
		request.getRequestDispatcher("/usuario/listar.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				service.excluir(lerId(request));
				redirecionarComMensagem(request, response, "sucesso", "Usuário excluído com sucesso.");
				return;
			}
			if ("atualizar".equals(acao)) {
				Usuario usuario = new Usuario(lerId(request), request.getParameter("nome"), request.getParameter("email"));
				service.atualizar(usuario);
				redirecionarComMensagem(request, response, "sucesso", "Usuário atualizado com sucesso.");
				return;
			}
			service.salvar(new Usuario(0, request.getParameter("nome"), request.getParameter("email")));
			redirecionarComMensagem(request, response, "sucesso", "Usuário salvo com sucesso.");
		} catch (Exception exception) {
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", mensagemDeErroExclusao(exception));
			} else if ("atualizar".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", "Não foi possível atualizar o usuário: " + exception.getMessage());
			} else {
				request.setAttribute("erro", "Não foi possível salvar o usuário: " + exception.getMessage());
				doGet(request, response);
			}
		}
	}

	private int lerId(HttpServletRequest request) {
		String valor = request.getParameter("id");
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Informe o ID do usuário.");
		}
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Informe um ID de usuário válido.");
		}
	}

	private String mensagemDeErroExclusao(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			return exception.getMessage();
		}
		return "Não foi possível excluir o usuário.";
	}

	private void redirecionarComMensagem(HttpServletRequest request, HttpServletResponse response,
			String tipo, String mensagem) throws IOException {
		String valor = java.net.URLEncoder.encode(mensagem, java.nio.charset.StandardCharsets.UTF_8);
		response.sendRedirect(request.getContextPath() + "/usuarios?" + tipo + "=" + valor);
	}
}
