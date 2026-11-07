package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Produto;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import br.com.feedbackprodutos.service.ProdutoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/produtos", "/api/produtos/*"})
public class ProdutoController extends ApiServlet {
    private final ProdutoService service = new ProdutoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isApiRequest(request)) {
            if ("editar".equals(request.getParameter("acao"))) {
                Integer id = formId(request.getParameter("id"));
                try {
                    Produto produto = id == null ? null : service.buscarPorId(id);
                    if (produto == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.setAttribute("erro", "Produto não encontrado.");
                        mostrarLista(request, response);
                    } else {
                        request.setAttribute("produtoEditando", produto);
                        request.getRequestDispatcher("/produto/editar.jsp").forward(request, response);
                    }
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível carregar o produto para edição.", exception);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.setAttribute("erro", "Não foi possível carregar o produto para edição.");
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
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do produto inválido.");
                return;
            }
            Produto produto = service.buscarPorId(id);
            if (produto == null) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                return;
            }
            writeJson(response, produto, HttpServletResponse.SC_OK);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao consultar produtos.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao consultar produtos.");
        }
    }

    private void mostrarLista(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            request.setAttribute("produtos", service.listar());
        } catch (SQLException exception) {
            getServletContext().log("Não foi possível carregar os produtos.", exception);
            request.setAttribute("erro", "Não foi possível carregar os produtos.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        request.getRequestDispatcher("/produto/listar.jsp").forward(request, response);
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
                    request.setAttribute("erro", "ID do produto inválido.");
                    mostrarLista(request, response);
                    return;
                }
                try {
                    service.excluir(id);
                    response.sendRedirect(request.getContextPath() + "/produtos?sucesso=Produto+exclu%C3%ADdo");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível excluir o produto.", exception);
                    request.setAttribute("erro", exception.getSQLState() != null
                            && exception.getSQLState().startsWith("23")
                                    ? "Não é possível excluir este produto porque há avaliações vinculadas."
                                    : "Não foi possível excluir o produto.");
                    mostrarLista(request, response);
                }
                return;
            }
            if ("atualizar".equals(acao)) {
                Integer id = formId(request.getParameter("id"));
                if (id == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("erro", "ID do produto inválido.");
                    mostrarLista(request, response);
                    return;
                }
                Produto produto;
                try {
                    produto = produtoDoFormulario(request);
                } catch (IllegalArgumentException exception) {
                    produto = new Produto(id, request.getParameter("nome"),
                            request.getParameter("descricao"), 0);
                    request.setAttribute("erro", exception.getMessage());
                    request.setAttribute("produtoEditando", produto);
                    request.getRequestDispatcher("/produto/editar.jsp").forward(request, response);
                    return;
                }
                produto.setId(id);
                try {
                    service.atualizar(produto);
                    response.sendRedirect(request.getContextPath() + "/produtos?sucesso=Produto+atualizado");
                } catch (RegistroNaoEncontradoException exception) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("erro", exception.getMessage());
                    mostrarLista(request, response);
                } catch (IllegalArgumentException exception) {
                    request.setAttribute("erro", exception.getMessage());
                    request.setAttribute("produtoEditando", produto);
                    request.getRequestDispatcher("/produto/editar.jsp").forward(request, response);
                } catch (SQLException exception) {
                    getServletContext().log("Não foi possível atualizar o produto.", exception);
                    request.setAttribute("erro", "Não foi possível atualizar o produto.");
                    request.setAttribute("produtoEditando", produto);
                    request.getRequestDispatcher("/produto/editar.jsp").forward(request, response);
                }
                return;
            }
            try {
                service.salvar(produtoDoFormulario(request));
                response.sendRedirect(request.getContextPath() + "/produtos?sucesso=Produto+salvo");
            } catch (IllegalArgumentException exception) {
                request.setAttribute("erro", exception.getMessage());
                mostrarLista(request, response);
            } catch (SQLException exception) {
                getServletContext().log("Não foi possível salvar o produto.", exception);
                request.setAttribute("erro", "Não foi possível salvar o produto.");
                mostrarLista(request, response);
            }
            return;
        }

        if (!isCollectionPath(request.getPathInfo())) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Rota de produtos não encontrada.");
            return;
        }
        try {
            Produto created = service.salvar(readJson(request, Produto.class));
            writeJson(response, created, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao salvar o produto.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao salvar o produto.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do produto inválido.");
            return;
        }

        try {
            Produto produto = readJson(request, Produto.class);
            produto.setId(id);
            service.atualizar(produto);
            writeJson(response, produto, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao atualizar o produto.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao atualizar o produto.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = pathId(request.getPathInfo());
        if (id == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID do produto inválido.");
            return;
        }
        try {
            service.excluir(id);
            writeNoContent(response);
        } catch (IllegalArgumentException exception) {
            writeValidationError(response, exception);
        } catch (SQLException exception) {
            writeDatabaseError(response, exception, "Erro interno ao excluir o produto.");
        } catch (RuntimeException exception) {
            writeUnexpectedError(response, exception, "Erro interno ao excluir o produto.");
        }
    }

    private Produto produtoDoFormulario(HttpServletRequest request) {
        String preco = request.getParameter("preco");
        try {
            return new Produto(0, request.getParameter("nome"), request.getParameter("descricao"),
                    preco == null || preco.isBlank() ? 0 : Double.parseDouble(preco));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Preço inválido.");
        }
    }
}
