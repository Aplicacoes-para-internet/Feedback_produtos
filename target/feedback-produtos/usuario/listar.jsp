<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,br.com.feedbackprodutos.model.Usuario" %>
<% List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios"); %>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Usuários</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body><main class="container">
<div class="topbar"><div><p class="eyebrow">PARTICIPANTES</p><h2>Usuários</h2></div><a href="${pageContext.request.contextPath}/">Início</a></div>
<% if (request.getAttribute("erro") != null) { %><div class="alert"><%= request.getAttribute("erro") %></div><% } %><% if (request.getParameter("sucesso") != null) { %><div class="alert success"><%= request.getParameter("sucesso") %></div><% } %>
<section class="panel"><h3>Novo usuário</h3><form method="post" action="${pageContext.request.contextPath}/usuarios"><div class="grid"><div class="field"><label for="nome">Nome</label><input id="nome" name="nome" required maxlength="120"></div><div class="field"><label for="email">E-mail</label><input id="email" name="email" type="email" required maxlength="180"></div></div><button type="submit">Cadastrar usuário</button></form></section>
<section class="panel"><h3>Usuários cadastrados</h3><% if (usuarios == null || usuarios.isEmpty()) { %><p class="muted">Nenhum usuário cadastrado.</p><% } else { %><table><thead><tr><th>Nome</th><th>E-mail</th></tr></thead><tbody><% for (Usuario usuario : usuarios) { %><tr><td><%= usuario.getNome() %></td><td><%= usuario.getEmail() %></td></tr><% } %></tbody></table><% } %></section>
</main></body></html>
