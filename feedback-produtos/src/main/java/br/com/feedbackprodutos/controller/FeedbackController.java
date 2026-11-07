package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Feedback;
import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.model.Usuario;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import br.com.feedbackprodutos.service.FeedbackService;
import br.com.feedbackprodutos.service.ProdutoService;
import br.com.feedbackprodutos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/feedback", "/api/feedbacks/*"})
public class FeedbackController extends ApiServlet {
    private final FeedbackService feedbackService = new FeedbackService();
    private final ProdutoService produtoService = new ProdutoService();
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isApiRequest(request)) {
            if ("editar".equals(request.getParameter("acao"))) {
                Integer id = formId(request.getParameter("id"));
                try {
                    Feedback feedback = id == null ? null : feedbackService.buscarPorId(id);
                    if (feedback == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.setAttribute("erro", "Feedback não encontrado.");
                        mostrarLista(request, response);
                    } else {
                        request.setAttribute("feedbackEditando", feedback);
                        request.getRequestDispatcher("/feedback/editar.jsp").forward(request, response);
                    }
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível carregar o feedback para edição.", exception);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.setAttribute("erro", "Não foi possível carregar o feedback para edição.");
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
                writeJson(response, feedbackService.listar(), HttpServletResponse.SC_OK);
                return;
            }
            Integer id = pathId(pathInfo);
            if (id == null) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do feedback inválido.");
                return;
            }
            Feedback feedback = feedbackService.buscarPorId(id);
            if (feedback == null) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Feedback não encontrado.");
                return;
            }
            writeJson(response, feedback, HttpServletResponse.SC_OK);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao consultar feedbacks.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao consultar feedbacks.");
        }
    }

    private void mostrarLista(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            request.setAttribute("feedbacks", feedbackService.listar());
            request.setAttribute("produtos", produtoService.listar());
            request.setAttribute("usuarios", usuarioService.listar());
        } catch (SQLException exception) {
            getServletContext().log("Não foi possível carregar os feedbacks.", exception);
            request.setAttribute("erro", "Não foi possível carregar os feedbacks.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        request.getRequestDispatcher("/feedback/listar.jsp").forward(request, response);
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
                    request.setAttribute("erro", "ID do feedback inválido.");
                    mostrarLista(request, response);
                    return;
                }
                try {
                    feedbackService.excluir(id);
                    response.sendRedirect(request.getContextPath() + "/feedback?sucesso=Feedback+exclu%C3%ADdo");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível excluir o feedback.", exception);
                    request.setAttribute("erro", "Não foi possível excluir o feedback.");
                    mostrarLista(request, response);
                }
                return;
            }
            if ("atualizar".equals(acao)) {
                Integer id = formId(request.getParameter("id"));
                if (id == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("erro", "ID do feedback inválido.");
                    mostrarLista(request, response);
                    return;
                }
                Feedback feedback = null;
                try {
                    feedback = feedbackService.buscarPorId(id);
                    if (feedback == null) {
                        throw new RegistroNaoEncontradoException("Feedback não encontrado.");
                    }
                    feedback.setNota(Integer.parseInt(request.getParameter("nota")));
                    feedback.setComentario(request.getParameter("comentario"));
                    feedbackService.atualizar(feedback);
                    response.sendRedirect(request.getContextPath() + "/feedback?sucesso=Feedback+atualizado");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (NumberFormatException exception) {
                    feedback.setComentario(request.getParameter("comentario"));
                    request.setAttribute("erro", "A nota deve ser um número entre 1 e 5.");
                    request.setAttribute("feedbackEditando", feedback);
                    request.getRequestDispatcher("/feedback/editar.jsp").forward(request, response);
                } catch (IllegalArgumentException exception) {
                    feedback.setComentario(request.getParameter("comentario"));
                    request.setAttribute("erro", exception.getMessage());
                    request.setAttribute("feedbackEditando", feedback);
                    request.getRequestDispatcher("/feedback/editar.jsp").forward(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível atualizar o feedback.", exception);
                    if (feedback == null) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        request.setAttribute("erro", "Não foi possível carregar o feedback para edição.");
                        mostrarLista(request, response);
                    } else {
                        feedback.setComentario(request.getParameter("comentario"));
                        request.setAttribute("erro", "Não foi possível atualizar o feedback.");
                        request.setAttribute("feedbackEditando", feedback);
                        request.getRequestDispatcher("/feedback/editar.jsp").forward(request, response);
                    }
                }
                return;
            }
            try {
                feedbackService.salvar(feedbackDoFormulario(request));
                response.sendRedirect(request.getContextPath() + "/feedback?sucesso=Feedback+enviado");
            } catch (IllegalArgumentException exception) {
                request.setAttribute("erro", exception.getMessage());
                mostrarLista(request, response);
            } catch (SQLException exception) {
                getServletContext().log("Não foi possível salvar o feedback.", exception);
                request.setAttribute("erro", "Não foi possível salvar o feedback.");
                mostrarLista(request, response);
            }
            return;
        }

        if (!isCollectionPath(request.getPathInfo())) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Rota de feedbacks não encontrada.");
            return;
        }
        try {
            Feedback created = feedbackService.salvar(readJson(request, Feedback.class));
            writeJson(response, created, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao salvar o feedback.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao salvar o feedback.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do feedback inválido.");
            return;
        }

        try {
            Feedback feedback = readJson(request, Feedback.class);
            feedback.setId(id);
            feedbackService.atualizar(feedback);
            writeJson(response, feedbackService.buscarPorId(id), HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao atualizar o feedback.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao atualizar o feedback.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do feedback inválido.");
            return;
        }
        try {
            feedbackService.excluir(id);
            writeNoContent(response);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao excluir o feedback.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao excluir o feedback.");
        }
    }

    private Feedback feedbackDoFormulario(HttpServletRequest request) {
        try {
            Produto produto = new Produto();
            produto.setId(Integer.parseInt(request.getParameter("produtoId")));
            Usuario usuario = new Usuario();
            usuario.setId(Integer.parseInt(request.getParameter("usuarioId")));
            int nota = Integer.parseInt(request.getParameter("nota"));
            return new Feedback(0, produto, usuario, nota, request.getParameter("comentario"), null);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Produto, usuário e nota devem ser IDs/números válidos.");
        }

    }

}
