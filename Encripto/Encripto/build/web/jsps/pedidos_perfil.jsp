<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Pedido, JavaBeans.Usuario, JavaBeans.Carrinho, JavaBeans.Produto, java.sql.ResultSet, java.util.List"%>

<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

// LISTA PEDIDOS
    Pedido pedido = new Pedido();
    List<Pedido> lista = pedido.listarPedidosPorUsuario(user.pkuser);
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Encripto - Pedidos</title>
        <link rel="stylesheet" href="../css/pedidos_perfil.css">
    </head>
    <body>

        <header>
            <div class="logo">
                <a href="../index_logado.jsp"
                   style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">
                    Encripto
                </a>
            </div>
            <nav>
                <a href="carrinho_logado.jsp" class="cart" id="cart-btn">🛒</a>
                <a href="perfil.jsp" class="cart">👤</a>
            </nav>
        </header>

        <main>
            <div class="container">

                <!-- ================= RESUMO DO PEDIDO ================= -->
                <div class="cart-container">
                    <h2>Pedidos</h2>

                    <div class="cart-header">
                        <span>Número do Pedido</span>
                        <span>Itens</span>
                        <span>Data</span>
                        <span>Total</span>
                        <span>Status</span>
                        <span>Mais Informações</span>
                    </div>

                    <%
                        if (lista != null && !lista.isEmpty()) {
                            for (Pedido ped : lista) {
                    %>
                    <div class="cart-item">
                        <span> <%= ped.pkpedido%></span>
                        <span> <%= ped.quantidade%></span>
                        <span> <%= ped.data_pedido%></span>
                        <span> <%= ped.total%></span>
                        <span> <%= ped.status%></span>
                        <span><a href="detalhes_pedido.jsp?pedido=<%= ped.pkpedido %>"><button>+</button></a></span>
                    </div>
                    <%  }
                    } else { %>
                    <p>Nenhum pedido feito.</p>
                    <% }%>
                </div>
        </main>
    </body>
</html>
