package br.com.feedbackprodutos.controller;

import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

abstract class ApiServlet extends HttpServlet {

    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected boolean isApiRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/");
    }

    protected <T> T readJson(HttpServletRequest request, Class<T> type) {
        String contentType = request.getContentType();
        if (contentType == null
                || !contentType.toLowerCase(java.util.Locale.ROOT).startsWith("application/json")) {
            throw new IllegalArgumentException("Content-Type deve ser application/json.");
        }

        try {
            T value = MAPPER.readValue(request.getReader(), type);
            if (value == null) {
                throw new IllegalArgumentException("O corpo JSON não pode ser nulo.");
            }
            return value;
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("JSON malformado ou incompatível com o recurso.", exception);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler o corpo JSON.", exception);
        }
    }

    protected Integer pathId(String pathInfo) {
        if (pathInfo == null || !pathInfo.matches("/[1-9][0-9]*")) {
            return null;
        }

        try {
            return Integer.valueOf(pathInfo.substring(1));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    protected boolean isCollectionPath(String pathInfo) {
        return pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo);
    }

    protected void writeJson(HttpServletResponse response, Object body, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(response.getWriter(), body);
    }

    protected void writeNoContent(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.setContentLength(0);
    }

    protected void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, Map.of("erro", message), status);
    }

    protected void writeValidationError(HttpServletResponse response, IllegalArgumentException exception)
            throws IOException {
        int status = exception instanceof RegistroNaoEncontradoException
                ? HttpServletResponse.SC_NOT_FOUND
                : HttpServletResponse.SC_BAD_REQUEST;
        writeError(response, status, exception.getMessage());
    }

    protected void writeDatabaseError(HttpServletResponse response, SQLException exception, String message)
            throws IOException {
        String state = exception.getSQLState();
        if (state != null && state.startsWith("23")) {
            writeError(response, HttpServletResponse.SC_CONFLICT,
                    "A operação conflita com dados relacionados ou uma restrição de unicidade.");
        } else if (state != null && state.startsWith("22")) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Um ou mais valores excedem os limites aceitos.");
        } else {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }

    protected void writeUnexpectedError(HttpServletResponse response, RuntimeException exception, String message)
            throws IOException {
        getServletContext().log(message, exception);
        writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
}
