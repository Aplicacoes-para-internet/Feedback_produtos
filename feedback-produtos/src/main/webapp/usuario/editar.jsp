<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Usuario,br.com.feedbackprodutos.util.HtmlEscaper" %>
<% Usuario usuario = (Usuario) request.getAttribute("usuarioEditando"); %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar usuário</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <div class="topbar"><div><p class="eyebrow">PARTICIPANTES</p><h2>Editar usuário</h2></div><a href="${pageContext.request.contextPath}/usuarios">Usuários</a></div>
    <% if (request.getAttribute("erro") != null) { %><div class="alert"><%= HtmlEscaper.escape(String.valueOf(request.getAttribute("erro"))) %></div><% } %>
    <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/usuarios">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= usuario.getId() %>">
            <div class="grid">
                <div class="field"><label for="nome">Nome</label><input id="nome" name="nome" required maxlength="120" value="<%= HtmlEscaper.escape(usuario.getNome()) %>"></div>
                <div class="field"><label for="email">E-mail</label><input id="email" name="email" type="email" required maxlength="180" value="<%= HtmlEscaper.escape(usuario.getEmail()) %>"></div>
            </div>
            <div class="form-actions"><button type="submit">Salvar alterações</button><a class="button" href="${pageContext.request.contextPath}/usuarios">Cancelar</a></div>
        </form>
    </section>
</main>
</body>
</html>
