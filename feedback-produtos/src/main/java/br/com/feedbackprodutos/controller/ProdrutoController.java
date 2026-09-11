package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.service.ProdutoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/produtos")
public class ProdrutoController extends HttpServlet {
	private final ProdutoService service = new ProdutoService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setAttribute("produtos", service.listar());
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível carregar os produtos: " + exception.getMessage());
		}
		request.getRequestDispatcher("/produto/listar.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			service.salvar(new Produto(0, request.getParameter("nome"), request.getParameter("descricao"),
					Double.parseDouble(request.getParameter("preco"))));
			response.sendRedirect(request.getContextPath() + "/produtos?sucesso=Produto+salvo");
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível salvar o produto: " + exception.getMessage());
			doGet(request, response);
		}
	}
}
