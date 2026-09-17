<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Usuario" %>
<%
  Usuario usuario = (Usuario) request.getAttribute("usuario");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar usuário</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
  <main class="container">
    <div class="topbar">
      <div>
        <p class="eyebrow">EDIÇÃO</p>
        <h2>Editar usuário</h2>
      </div>
      <a href="${pageContext.request.contextPath}/usuarios">Voltar</a>
    </div>

    <% if (request.getAttribute("erro") != null) { %>
      <div class="alert"><%= request.getAttribute("erro") %></div>
    <% } %>

    <% if (usuario != null) { %>
      <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/usuarios">
          <input type="hidden" name="acao" value="atualizar">
          <input type="hidden" name="id" value="<%= usuario.getId() %>">

          <div class="grid">
            <div class="field">
              <label for="nome">Nome</label>
              <input id="nome" name="nome" value="<%= usuario.getNome() %>" required maxlength="120">
            </div>
            <div class="field">
              <label for="email">E-mail</label>
              <input id="email" name="email" type="email" value="<%= usuario.getEmail() %>" required maxlength="180">
            </div>
          </div>

          <button type="submit">Salvar alterações</button>
        </form>
      </section>
    <% } else { %>
      <div class="alert">Usuário não encontrado.</div>
    <% } %>
  </main>
</body>
</html>
