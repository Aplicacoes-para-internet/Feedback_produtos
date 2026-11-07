<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,br.com.feedbackprodutos.model.Feedback,br.com.feedbackprodutos.model.Produto,br.com.feedbackprodutos.model.Usuario,br.com.feedbackprodutos.util.HtmlEscaper" %>
<% List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks"); List<Produto> produtos = (List<Produto>) request.getAttribute("produtos"); List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios"); %>
<!DOCTYPE html>
<html lang="pt-BR"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Feedbacks</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><main class="container">
<div class="topbar"><div><p class="eyebrow">AVALIAÇÕES</p><h2>Feedbacks de produtos</h2></div><a href="${pageContext.request.contextPath}/">Início</a></div>
<% if (request.getAttribute("erro") != null) { %><div class="alert"><%= HtmlEscaper.escape(String.valueOf(request.getAttribute("erro"))) %></div><% } %>
<% if (request.getParameter("sucesso") != null) { %><div class="alert success"><%= HtmlEscaper.escape(request.getParameter("sucesso")) %></div><% } %>
<section class="panel"><h3>Nova avaliação</h3>
<% if (produtos == null || produtos.isEmpty() || usuarios == null || usuarios.isEmpty()) { %>
<p class="muted">Cadastre pelo menos um produto e um usuário antes de enviar uma avaliação.</p>
<% } else { %>
<form method="post" action="${pageContext.request.contextPath}/feedback"><div class="grid">
<div class="field"><label for="produtoId">Produto</label><select id="produtoId" name="produtoId" required><% for (Produto produto : produtos) { %><option value="<%= produto.getId() %>"><%= HtmlEscaper.escape(produto.getNome()) %></option><% } %></select></div>
<div class="field"><label for="usuarioId">Usuário</label><select id="usuarioId" name="usuarioId" required><% for (Usuario usuario : usuarios) { %><option value="<%= usuario.getId() %>"><%= HtmlEscaper.escape(usuario.getNome()) %> - <%= HtmlEscaper.escape(usuario.getEmail()) %></option><% } %></select></div>
<div class="field"><label for="nota">Nota</label><select id="nota" name="nota" required><option value="5">5 - Excelente</option><option value="4">4 - Muito bom</option><option value="3">3 - Bom</option><option value="2">2 - Regular</option><option value="1">1 - Ruim</option></select></div>
<div class="field full"><label for="comentario">Comentário</label><textarea id="comentario" name="comentario" maxlength="1000" required></textarea></div></div><button type="submit">Enviar feedback</button></form>
<% } %></section>
<section class="panel"><h3>Histórico</h3><% if (feedbacks == null || feedbacks.isEmpty()) { %><p class="muted">Nenhum feedback enviado ainda.</p><% } else { %><table><thead><tr><th>Produto</th><th>Usuário</th><th>Nota</th><th>Comentário</th><th>Data</th><th>Ações</th></tr></thead><tbody><% for (Feedback feedback : feedbacks) { %><tr><td><%= HtmlEscaper.escape(feedback.getProduto().getNome()) %></td><td><%= HtmlEscaper.escape(feedback.getUsuario().getNome()) %></td><td><%= feedback.getNota() %>/5</td><td><%= HtmlEscaper.escape(feedback.getComentario()) %></td><td><%= HtmlEscaper.escape(String.valueOf(feedback.getDataEnvio())) %></td><td class="actions"><a class="button button-small" href="${pageContext.request.contextPath}/feedback?acao=editar&amp;id=<%= feedback.getId() %>">Editar</a><form method="post" action="${pageContext.request.contextPath}/feedback" onsubmit="return confirm('Excluir este feedback?');"><input type="hidden" name="acao" value="excluir"><input type="hidden" name="id" value="<%= feedback.getId() %>"><button class="button-small danger" type="submit">Excluir</button></form></td></tr><% } %></tbody></table><% } %></section>
</main></body></html>
