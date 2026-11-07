<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Feedback,br.com.feedbackprodutos.util.HtmlEscaper" %>
<% Feedback feedback = (Feedback) request.getAttribute("feedbackEditando"); %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar feedback</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <div class="topbar"><div><p class="eyebrow">AVALIAÇÕES</p><h2>Editar feedback</h2></div><a href="${pageContext.request.contextPath}/feedback">Feedbacks</a></div>
    <% if (request.getAttribute("erro") != null) { %><div class="alert"><%= HtmlEscaper.escape(String.valueOf(request.getAttribute("erro"))) %></div><% } %>
    <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/feedback">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= feedback.getId() %>">
            <div class="grid">
                <div class="field"><label>Produto</label><p><%= HtmlEscaper.escape(feedback.getProduto().getNome()) %></p></div>
                <div class="field"><label>Usuário</label><p><%= HtmlEscaper.escape(feedback.getUsuario().getNome()) %> - <%= HtmlEscaper.escape(feedback.getUsuario().getEmail()) %></p></div>
                <div class="field"><label for="nota">Nota</label><select id="nota" name="nota" required>
                    <option value="5" <%= feedback.getNota() == 5 ? "selected" : "" %>>5 - Excelente</option>
                    <option value="4" <%= feedback.getNota() == 4 ? "selected" : "" %>>4 - Muito bom</option>
                    <option value="3" <%= feedback.getNota() == 3 ? "selected" : "" %>>3 - Bom</option>
                    <option value="2" <%= feedback.getNota() == 2 ? "selected" : "" %>>2 - Regular</option>
                    <option value="1" <%= feedback.getNota() == 1 ? "selected" : "" %>>1 - Ruim</option>
                </select></div>
                <div class="field full"><label for="comentario">Comentário</label><textarea id="comentario" name="comentario" maxlength="1000" required><%= HtmlEscaper.escape(feedback.getComentario()) %></textarea></div>
            </div>
            <div class="form-actions"><button type="submit">Salvar alterações</button><a class="button" href="${pageContext.request.contextPath}/feedback">Cancelar</a></div>
        </form>
    </section>
</main>
</body>
</html>
