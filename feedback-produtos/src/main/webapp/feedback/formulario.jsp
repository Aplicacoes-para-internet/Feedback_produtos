<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Feedback, br.com.feedbackprodutos.model.Produto, br.com.feedbackprodutos.model.Usuario" %>
<%@ page import="java.util.List" %>
<%
  Feedback feedback = (Feedback) request.getAttribute("feedback");
  List<Produto> produtos = (List<Produto>) request.getAttribute("produtos");
  List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar feedback</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
  <main class="container">
    <div class="topbar">
      <div>
        <p class="eyebrow">EDIÇÃO</p>
        <h2>Editar feedback</h2>
      </div>
      <a href="${pageContext.request.contextPath}/feedback">Voltar</a>
    </div>

    <% if (request.getAttribute("erro") != null) { %>
      <div class="alert"><%= request.getAttribute("erro") %></div>
    <% } %>

    <% if (feedback != null) { %>
      <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/feedback">
          <input type="hidden" name="acao" value="atualizar">
          <input type="hidden" name="id" value="<%= feedback.getId() %>">

          <div class="grid">
            <div class="field">
              <label for="produtoId">Produto</label>
              <select id="produtoId" name="produtoId" required>
                <% for (Produto produto : produtos) { %>
                  <option value="<%= produto.getId() %>" <%= produto.getId() == feedback.getProduto().getId() ? "selected" : "" %>><%= produto.getNome() %></option>
                <% } %>
              </select>
            </div>

            <div class="field">
              <label for="usuarioId">Usuário</label>
              <select id="usuarioId" name="usuarioId" required>
                <% for (Usuario usuario : usuarios) { %>
                  <option value="<%= usuario.getId() %>" <%= usuario.getId() == feedback.getUsuario().getId() ? "selected" : "" %>><%= usuario.getNome() %> - <%= usuario.getEmail() %></option>
                <% } %>
              </select>
            </div>

            <div class="field">
              <label for="nota">Nota</label>
              <select id="nota" name="nota" required>
                <% for (int i = 5; i >= 1; i--) { %>
                  <option value="<%= i %>" <%= i == feedback.getNota() ? "selected" : "" %>><%= i %> - <%= i == 5 ? "Excelente" : i == 4 ? "Muito bom" : i == 3 ? "Bom" : i == 2 ? "Regular" : "Ruim" %></option>
                <% } %>
              </select>
            </div>

            <div class="field full">
              <label for="comentario">Comentário</label>
              <textarea id="comentario" name="comentario" maxlength="1000" required><%= feedback.getComentario() %></textarea>
            </div>
          </div>

          <button type="submit">Salvar alterações</button>
        </form>
      </section>
    <% } else { %>
      <div class="alert">Feedback não encontrado.</div>
    <% } %>
  </main>
</body>
</html>
