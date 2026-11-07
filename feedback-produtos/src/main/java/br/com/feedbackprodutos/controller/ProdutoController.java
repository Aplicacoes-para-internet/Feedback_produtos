package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.model.Produto;
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
            try {
                request.setAttribute("produtos", service.listar());
            } catch (SQLException exception) {
                request.setAttribute("erro", "Não foi possível carregar os produtos.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            request.getRequestDispatcher("/produto/listar.jsp").forward(request, response);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!isApiRequest(request)) {
            try {
                service.salvar(produtoDoFormulario(request));
                response.sendRedirect(request.getContextPath() + "/produtos?sucesso=Produto+salvo");
            } catch (IllegalArgumentException | SQLException exception) {
                request.setAttribute("erro", "Não foi possível salvar o produto: " + exception.getMessage());
                doGet(request, response);
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
