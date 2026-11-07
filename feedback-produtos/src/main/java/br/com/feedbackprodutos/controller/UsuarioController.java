package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
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
            if ("editar".equals(request.getParameter("acao"))) {
                Integer id = formId(request.getParameter("id"));
                try {
                    Usuario usuario = id == null ? null : service.buscarPorId(id);
                    if (usuario == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.setAttribute("erro", "Usuário não encontrado.");
                        mostrarLista(request, response);
                    } else {
                        request.setAttribute("usuarioEditando", usuario);
                        request.getRequestDispatcher("/usuario/editar.jsp").forward(request, response);
                    }
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível carregar o usuário para edição.", exception);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.setAttribute("erro", "Não foi possível carregar o usuário para edição.");
                    mostrarLista(request, response);
                }
                return;
            }
            mostrarLista(request, response);
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

    private void mostrarLista(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            request.setAttribute("usuarios", service.listar());
        } catch (SQLException exception) {
            getServletContext().log("Não foi possível carregar os usuários.", exception);
            request.setAttribute("erro", "Não foi possível carregar os usuários.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        request.getRequestDispatcher("/usuario/listar.jsp").forward(request, response);
    }

    private Integer formId(String value) {
        if (value == null || !value.matches("[1-9][0-9]*")) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!isApiRequest(request)) {
            String acao = request.getParameter("acao");
            if ("excluir".equals(acao)) {
                Integer id = formId(request.getParameter("id"));
                if (id == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("erro", "ID do usuário inválido.");
                    mostrarLista(request, response);
                    return;
                }
                try {
                    service.excluir(id);
                    response.sendRedirect(request.getContextPath() + "/usuarios?sucesso=Usu%C3%A1rio+exclu%C3%ADdo");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível excluir o usuário.", exception);
                    request.setAttribute("erro", exception.getSQLState() != null
                            && exception.getSQLState().startsWith("23")
                                    ? "Não é possível excluir este usuário porque há avaliações vinculadas."
                                    : "Não foi possível excluir o usuário.");
                    mostrarLista(request, response);
                }
                return;
            }
            if ("atualizar".equals(acao)) {
                Integer id = formId(request.getParameter("id"));
                if (id == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("erro", "ID do usuário inválido.");
                    mostrarLista(request, response);
                    return;
                }
                Usuario usuario = usuarioDoFormulario(request);
                usuario.setId(id);
                try {
                    service.atualizar(usuario);
                    response.sendRedirect(request.getContextPath() + "/usuarios?sucesso=Usu%C3%A1rio+atualizado");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (IllegalArgumentException exception) {
                    request.setAttribute("erro", exception.getMessage());
                    request.setAttribute("usuarioEditando", usuario);
                    request.getRequestDispatcher("/usuario/editar.jsp").forward(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível atualizar o usuário.", exception);
                    request.setAttribute("erro", "Não foi possível atualizar o usuário.");
                    request.setAttribute("usuarioEditando", usuario);
                    request.getRequestDispatcher("/usuario/editar.jsp").forward(request, response);
                }
                return;
            }
            try {
                service.salvar(usuarioDoFormulario(request));
                response.sendRedirect(request.getContextPath() + "/usuarios?sucesso=Usuário+salvo");
            } catch (IllegalArgumentException exception) {
                request.setAttribute("erro", exception.getMessage());
                mostrarLista(request, response);
            } catch (SQLException exception) {
                getServletContext().log("Não foi possível salvar o usuário.", exception);
                request.setAttribute("erro", "Não foi possível salvar o usuário.");
                mostrarLista(request, response);
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
