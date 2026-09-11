package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/usuarios")
public class UsuarioController extends HttpServlet {
	private final UsuarioService service = new UsuarioService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setAttribute("usuarios", service.listar());
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível carregar os usuários: " + exception.getMessage());
		}
		request.getRequestDispatcher("/usuario/listar.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			service.salvar(new Usuario(0, request.getParameter("nome"), request.getParameter("email")));
			response.sendRedirect(request.getContextPath() + "/usuarios?sucesso=Usuário+salvo");
		} catch (Exception exception) {
			request.setAttribute("erro", "Não foi possível salvar o usuário: " + exception.getMessage());
			doGet(request, response);
		}
	}
}
