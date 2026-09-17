package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.service.ProdutoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebServlet("/produtos")
public class ProdutoController extends HttpServlet {
	private final ProdutoService service = new ProdutoService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String acao = request.getParameter("acao");
			if ("buscar".equals(acao)) {
				Produto produto = service.buscarPorId(lerId(request));
				if (produto == null) {
					throw new IllegalArgumentException("Produto não encontrado.");
				}
				request.setAttribute("produtos", Collections.singletonList(produto));
				request.getRequestDispatcher("/produto/listar.jsp").forward(request, response);
				return;
			}
			if ("editar".equals(acao)) {
				int id = lerId(request);
				Produto produto = service.buscarPorId(id);
				if (produto == null) {
					throw new IllegalArgumentException("Produto não encontrado.");
				}
				request.setAttribute("produto", produto);
				request.getRequestDispatcher("/produto/formulario.jsp").forward(request, response);
				return;
			}
			request.setAttribute("produtos", service.listar());
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível carregar os produtos: " + exception.getMessage());
		}
		request.getRequestDispatcher("/produto/listar.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				service.excluir(lerId(request));
				redirecionarComMensagem(request, response, "sucesso", "Produto excluído com sucesso.");
				return;
			}
			if ("atualizar".equals(acao)) {
				String nome = request.getParameter("nome");
				String descricao = request.getParameter("descricao");
				String precoTexto = request.getParameter("preco");
				double preco = Double.parseDouble(precoTexto);
				Produto produto = new Produto(lerId(request), nome, descricao, preco);
				service.atualizar(produto);
				redirecionarComMensagem(request, response, "sucesso", "Produto atualizado com sucesso.");
				return;
			}
			String nome = request.getParameter("nome");
			String descricao = request.getParameter("descricao");
			String precoTexto = request.getParameter("preco");
			double preco = Double.parseDouble(precoTexto);
			service.salvar(new Produto(0, nome, descricao, preco));
			redirecionarComMensagem(request, response, "sucesso", "Produto salvo com sucesso.");
		} catch (Exception exception) {
			String acao = request.getParameter("acao");
			if ("excluir".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", mensagemDeErroExclusao(exception));
			} else if ("atualizar".equals(acao)) {
				redirecionarComMensagem(request, response, "erro", "Não foi possível atualizar o produto: " + exception.getMessage());
			} else {
				request.setAttribute("erro", "Não foi possível salvar o produto: " + exception.getMessage());
				doGet(request, response);
			}
		}
	}

	private int lerId(HttpServletRequest request) {
		String valor = request.getParameter("id");
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Informe o ID do produto.");
		}
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Informe um ID de produto válido.");
		}
	}

	private String mensagemDeErroExclusao(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			return exception.getMessage();
		}
		return "Não foi possível excluir o produto.";
	}

	private void redirecionarComMensagem(HttpServletRequest request, HttpServletResponse response,
			String tipo, String mensagem) throws IOException {
		String valor = java.net.URLEncoder.encode(mensagem, java.nio.charset.StandardCharsets.UTF_8);
		response.sendRedirect(request.getContextPath() + "/produtos?" + tipo + "=" + valor);
	}
}
