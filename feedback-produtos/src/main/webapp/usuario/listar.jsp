<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,br.com.feedbackprodutos.model.Usuario" %>
<% List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios"); %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Usuários</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
  <main class="container">
    <div class="topbar">
      <div>
        <p class="eyebrow">PARTICIPANTES</p>
        <h2>Usuários</h2>
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
      <h3>Novo usuário</h3>
      <form method="post" action="${pageContext.request.contextPath}/usuarios">
        <div class="grid">
          <div class="field">
            <label for="nome">Nome</label>
            <input id="nome" name="nome" required maxlength="120">
          </div>
          <div class="field">
            <label for="email">E-mail</label>
            <input id="email" name="email" type="email" required maxlength="180">
          </div>
        </div>
        <button type="submit">Cadastrar usuário</button>
      </form>
    </section>

    <section class="panel search-panel">
      <h3>Buscar usuário</h3>
      <form class="search-form" method="get" action="${pageContext.request.contextPath}/usuarios">
        <input type="hidden" name="acao" value="buscar">
        <div class="field">
          <label for="buscaId">ID do usuário</label>
          <input id="buscaId" name="id" type="text" inputmode="numeric" required placeholder="Digite o ID">
        </div>
        <button type="submit">Buscar</button>
        <a class="button" href="${pageContext.request.contextPath}/usuarios">Limpar</a>
      </form>
    </section>

    <section class="panel">
      <h3>Usuários cadastrados</h3>
      <% if (usuarios == null || usuarios.isEmpty()) { %>
        <p class="muted">Nenhum usuário cadastrado.</p>
      <% } else { %>
        <div class="table-container"><table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>E-mail</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <% for (Usuario usuario : usuarios) { %>
              <tr>
                <td class="id-cell"><%= usuario.getId() %></td>
                <td><%= usuario.getNome() %></td>
                <td><%= usuario.getEmail() %></td>
                <td>
                  <div class="actions">
                    <a class="button small" href="${pageContext.request.contextPath}/usuarios?acao=editar&id=<%= usuario.getId() %>">Editar</a>
                    <form method="post" action="${pageContext.request.contextPath}/usuarios" class="inline-form" onsubmit="return confirm('Tem certeza que deseja excluir este usuário?');">
                      <input type="hidden" name="acao" value="excluir">
                      <input type="hidden" name="id" value="<%= usuario.getId() %>">
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
