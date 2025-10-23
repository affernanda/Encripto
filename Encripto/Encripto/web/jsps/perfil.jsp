<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Usuario, java.text.SimpleDateFormat" %>
<%
    // Recupera o usuário logado da sessão
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");

    // Se não estiver logado, redireciona para login
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

    // Apenas formata a data para exibição
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>


<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <title>Perfil - Encripto</title>
    <link rel="stylesheet" href="../css/perfil.css" />
    <style>
        @font-face {
            font-family: 'FonteMarca';
            src: url('../fonts/calora.otf') format('opentype');
        }
        table {
            font-size: 18px;
        }
        td {
            padding: 5px 10px;
        }
    </style>
</head>
<body>
    <header>
        <div class="logo">
            <a href="../index_logado.html" style="text-decoration:none; color:black; font-family:'FonteMarca',sans-serif; font-size:50px;">Encripto</a>
        </div>
        <nav>
            <a href="#" class="cart" id="cart-btn">🛒</a>
            <a href="perfil.jsp" class="cart">👤</a>
        </nav>
    </header>

    <main class="profile-section">
        <div class="profile-container">
            <div class="profile-info">
                <div class="edit">
                    <div>
                        <h1><%= user.nome %></h1>
                    </div>
                    <a href="../perfil_editar.html">Editar</a>
                    <a href="logout.jsp">Deslogar</a>
                </div>

                <form class="form">
                    <table>
                        <tr><td><strong>Nome:</strong></td><td><%= user.nome %></td></tr>
                        <tr><td><strong>E-mail:</strong></td><td><%= user.email %></td></tr>
                        <tr><td><strong>CPF:</strong></td><td><%= user.cpf %></td></tr>
                        <tr><td><strong>Data de Nascimento:</strong></td><td><%= user.dataNascimento != null ? sdf.format(user.dataNascimento) : "Não informado" %></td></tr>
                        <tr><td><strong>Gênero:</strong></td><td><%= user.genero %></td></tr>
                    </table>

                    <br>
                    <a href="enderecos.jsp" class="update">Endereços Cadastrados →</a>
                </form>
            </div>

            <div class="profile-photo">
                <div class="photo-circle"></div>
            </div>
        </div>
    </main>

    <script src="../scripts/index.js"></script>
</body>
</html>
