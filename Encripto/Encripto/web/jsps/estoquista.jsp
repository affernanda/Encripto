<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Pedido, JavaBeans.Usuario, JavaBeans.Carrinho, JavaBeans.Produto, java.sql.ResultSet, java.util.List"%>

<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

// LISTA PEDIDOS
    Pedido pedido = new Pedido();
    List<Pedido> lista = pedido.listarPedidos();
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Encripto - Estoquista</title>
        <link rel="stylesheet" href="../css/administrador.css">
        <style>
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
    <header>
        <div class="logo">
            <a href="estoquista.jsp" style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</a>
        </div>
        <nav>
            <a href="logout.jsp">Deslogar</a>
        </nav>
    </header>
    <body>
        <main class="profile-section">
            <div class="profile-container">
                    <div class="edit">
                        <a href="pedidos_estoquista.jsp" class="botao-finalizar">Listar Pedidos</a>
                    </div>
                </div>
        </main>
        
                <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>
    </body>
</html>
