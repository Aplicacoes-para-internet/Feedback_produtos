<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Produto" %>
<%
  Produto produto = (Produto) request.getAttribute("produto");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar produto</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
  <main class="container">
    <div class="topbar">
      <div>
        <p class="eyebrow">EDIÇÃO</p>
        <h2>Editar produto</h2>
      </div>
      <a href="${pageContext.request.contextPath}/produtos">Voltar</a>
    </div>

    <% if (request.getAttribute("erro") != null) { %>
      <div class="alert"><%= request.getAttribute("erro") %></div>
    <% } %>

    <% if (produto != null) { %>
      <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/produtos">
          <input type="hidden" name="acao" value="atualizar">
          <input type="hidden" name="id" value="<%= produto.getId() %>">

          <div class="grid">
            <div class="field">
              <label for="nome">Nome</label>
              <input id="nome" name="nome" value="<%= produto.getNome() %>" required maxlength="120">
            </div>
            <div class="field">
              <label for="preco">Preço</label>
              <input id="preco" name="preco" type="number" min="0" step="0.01" value="<%= produto.getPreco() %>" required>
            </div>
            <div class="field full">
              <label for="descricao">Descrição</label>
              <textarea id="descricao" name="descricao" maxlength="500"><%= produto.getDescricao() %></textarea>
            </div>
          </div>

          <button type="submit">Salvar alterações</button>
        </form>
      </section>
    <% } else { %>
      <div class="alert">Produto não encontrado.</div>
    <% } %>
  </main>
</body>
</html>
