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
        <title>Encripto - Perfil</title>
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

            .edit a {
                background-color: black;
                color: white;
                padding: 8px 16px;
                border-radius: 8px;
                text-decoration: none;
                font-family: 'Raleway', sans-serif;
                transition: background 0.3s;
                border: none;
                box-shadow: none;
                font-size: large;
            }

            .edit a:hover {
                background-color: #ff1a45;
            }
        </style>
    </head>
    <body>
        <header>
            <div class="logo">
                <a href="../index_logado.jsp" style="text-decoration:none; color:white; font-family:'FonteMarca',sans-serif; font-size:50px;">Encripto</a>
            </div>
            <nav>
                <a href="carrinho_logado.jsp" class="cart" id="cart-btn">🛒</a>
                <a href="perfil.jsp" class="cart">👤</a>
            </nav>
        </header>

        <main class="profile-section">
            <div class="profile-container">
                <div class="profile-info">
                    <div class="edit">
                        <div>
                            <h1><%= user.nome%></h1>
                        </div>
                        <a href="../perfil_editar.html">Editar</a>
                        <a href="pedidos_perfil.jsp ">Pedidos</a>
                        <a href="logout.jsp">Deslogar</a>
                    </div>

                    <form class="form">
                        <table>
                            <tr><td><strong>Nome:</strong></td><td><%= user.nome%></td></tr>
                            <tr><td><strong>E-mail:</strong></td><td><%= user.email%></td></tr>
                            <tr><td><strong>CPF:</strong></td><td><%= user.cpf%></td></tr>
                            <tr><td><strong>Data de Nascimento:</strong></td><td><%= user.dataNascimento != null ? sdf.format(user.dataNascimento) : "Não informado"%></td></tr>
                            <tr><td><strong>Gênero:</strong></td><td><%= user.genero%></td></tr>
                        </table>

                        <br>
                        <a href="enderecos.jsp" class="update">⬅ Endereços Cadastrados</a>
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
