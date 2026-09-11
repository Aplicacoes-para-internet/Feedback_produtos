<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Feedback de Produtos</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container home">
	<p class="eyebrow">MVC • DAO • SERVICE</p>
	<h1>Feedback de Produtos</h1>
	<p class="intro">Cadastre produtos e usuários e reúna avaliações em um único lugar.</p>
	<nav class="menu">
		<a class="button primary" href="${pageContext.request.contextPath}/feedback">Avaliar produto</a>
		<a class="button" href="${pageContext.request.contextPath}/produtos">Gerenciar produtos</a>
		<a class="button" href="${pageContext.request.contextPath}/usuarios">Gerenciar usuários</a>
	</nav>
</main>
</body>
</html>
