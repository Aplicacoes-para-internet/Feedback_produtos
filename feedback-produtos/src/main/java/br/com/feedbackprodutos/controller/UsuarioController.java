package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/usuarios", "/api/usuarios/*"})
public class UsuarioController extends ApiServlet {
    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isApiRequest(request)) {
            try {
                request.setAttribute("usuarios", service.listar());
            } catch (SQLException exception) {
                request.setAttribute("erro", "Não foi possível carregar os usuários.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            request.getRequestDispatcher("/usuario/listar.jsp").forward(request, response);
            return;
        }

        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
                writeJson(response, service.listar(), HttpServletResponse.SC_OK);
                return;
            }
            Integer id = pathId(pathInfo);
            if (id == null) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do usuário inválido.");
                return;
            }
            Usuario usuario = service.buscarPorId(id);
            if (usuario == null) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado.");
                return;
            }
            writeJson(response, usuario, HttpServletResponse.SC_OK);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao consultar usuários.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao consultar usuários.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!isApiRequest(request)) {
            try {
                service.salvar(usuarioDoFormulario(request));
                response.sendRedirect(request.getContextPath() + "/usuarios?sucesso=Usuário+salvo");
            } catch (IllegalArgumentException | SQLException exception) {
                request.setAttribute("erro", "Não foi possível salvar o usuário: " + exception.getMessage());
                doGet(request, response);
            }
            return;
        }

        if (!isCollectionPath(request.getPathInfo())) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Rota de usuários não encontrada.");
            return;
        }
        try {
            Usuario created = service.salvar(readJson(request, Usuario.class));
            writeJson(response, created, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao salvar o usuário.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao salvar o usuário.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do usuário inválido.");
            return;
        }

        try {
            Usuario usuario = readJson(request, Usuario.class);
            usuario.setId(id);
            service.atualizar(usuario);
            writeJson(response, usuario, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao atualizar o usuário.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao atualizar o usuário.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do usuário inválido.");
            return;
        }
        try {
            service.excluir(id);
            writeNoContent(response);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao excluir o usuário.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao excluir o usuário.");
        }
    }

    private Usuario usuarioDoFormulario(HttpServletRequest request) {
        return new Usuario(0, request.getParameter("nome"), request.getParameter("email"));
    }
}
