<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.feedbackprodutos.model.Produto,br.com.feedbackprodutos.util.HtmlEscaper,java.util.Locale" %>
<% Produto produto = (Produto) request.getAttribute("produtoEditando"); %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar produto</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <div class="topbar"><div><p class="eyebrow">CATÁLOGO</p><h2>Editar produto</h2></div><a href="${pageContext.request.contextPath}/produtos">Produtos</a></div>
    <% if (request.getAttribute("erro") != null) { %><div class="alert"><%= HtmlEscaper.escape(String.valueOf(request.getAttribute("erro"))) %></div><% } %>
    <section class="panel">
        <form method="post" action="${pageContext.request.contextPath}/produtos">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= produto.getId() %>">
            <div class="grid">
                <div class="field"><label for="nome">Nome</label><input id="nome" name="nome" required maxlength="120" value="<%= HtmlEscaper.escape(produto.getNome()) %>"></div>
                <div class="field"><label for="preco">Preço</label><input id="preco" name="preco" type="number" min="0" step="0.01" required value="<%= String.format(Locale.ROOT, "%.2f", produto.getPreco()) %>"></div>
                <div class="field full"><label for="descricao">Descrição</label><textarea id="descricao" name="descricao" maxlength="500"><%= HtmlEscaper.escape(produto.getDescricao()) %></textarea></div>
            </div>
            <div class="form-actions"><button type="submit">Salvar alterações</button><a class="button" href="${pageContext.request.contextPath}/produtos">Cancelar</a></div>
        </form>
    </section>
</main>
</body>
</html>
