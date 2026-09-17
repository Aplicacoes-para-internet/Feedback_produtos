<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,br.com.feedbackprodutos.model.Produto" %>
<% List<Produto> produtos = (List<Produto>) request.getAttribute("produtos"); %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Produtos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
  <main class="container">
    <div class="topbar">
      <div>
        <p class="eyebrow">CATÁLOGO</p>
        <h2>Produtos</h2>
      </div>
      <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/produtos">Produtos</a>
        <a href="${pageContext.request.contextPath}/usuarios">Usuários</a>
        <a href="${pageContext.request.contextPath}/feedback">Feedbacks</a>
      </nav>
    </div>

    <% if (request.getAttribute("erro") != null) { %>
      <div class="alert"><%= request.getAttribute("erro") %></div>
    <% } %>
    <% if (request.getParameter("sucesso") != null) { %>
      <div class="alert success"><%= request.getParameter("sucesso") %></div>
    <% } %>

    <section class="panel">
      <h3>Novo produto</h3>
      <form method="post" action="${pageContext.request.contextPath}/produtos">
        <div class="grid">
          <div class="field">
            <label for="nome">Nome</label>
            <input id="nome" name="nome" required maxlength="120">
          </div>
          <div class="field">
            <label for="preco">Preço</label>
            <input id="preco" name="preco" type="number" min="0" step="0.01" required>
          </div>
          <div class="field full">
            <label for="descricao">Descrição</label>
            <textarea id="descricao" name="descricao" maxlength="500"></textarea>
          </div>
        </div>
        <button type="submit">Cadastrar produto</button>
      </form>
    </section>

    <section class="panel search-panel">
      <h3>Buscar produto</h3>
      <form class="search-form" method="get" action="${pageContext.request.contextPath}/produtos">
        <input type="hidden" name="acao" value="buscar">
        <div class="field">
          <label for="buscaId">ID do produto</label>
          <input id="buscaId" name="id" type="text" inputmode="numeric" required placeholder="Digite o ID">
        </div>
        <button type="submit">Buscar</button>
        <a class="button" href="${pageContext.request.contextPath}/produtos">Limpar</a>
      </form>
    </section>

    <section class="panel">
      <h3>Produtos cadastrados</h3>
      <% if (produtos == null || produtos.isEmpty()) { %>
        <p class="muted">Nenhum produto cadastrado.</p>
      <% } else { %>
        <div class="table-container"><table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Preço</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <% for (Produto produto : produtos) { %>
              <tr>
                <td class="id-cell"><%= produto.getId() %></td>
                <td><%= produto.getNome() %></td>
                <td><%= produto.getDescricao() %></td>
                <td>R$ <%= String.format("%.2f", produto.getPreco()) %></td>
                <td>
                  <div class="actions">
                    <a class="button small" href="${pageContext.request.contextPath}/produtos?acao=editar&id=<%= produto.getId() %>">Editar</a>
                    <form method="post" action="${pageContext.request.contextPath}/produtos" class="inline-form" onsubmit="return confirm('Tem certeza que deseja excluir este produto?');">
                      <input type="hidden" name="acao" value="excluir">
                      <input type="hidden" name="id" value="<%= produto.getId() %>">
                      <button type="submit" class="danger">Excluir</button>
                    </form>
                  </div>
                </td>
              </tr>
            <% } %>
          </tbody>
        </table></div>
      <% } %>
    </section>
  </main>
</body>
</html>
