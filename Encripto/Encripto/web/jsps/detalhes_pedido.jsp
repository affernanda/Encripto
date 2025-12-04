<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Endereco, JavaBeans.Usuario, JavaBeans.Pedido, JavaBeans.Produto, java.sql.ResultSet, java.util.List" %>

<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

    String pedidoStr = request.getParameter("pedido");
    if (pedidoStr == null) {
        out.println("<h2>Pedido inválido</h2>");
        return;
    }

    int pkpedido = Integer.parseInt(pedidoStr);

    Pedido pedDAO = new Pedido();
    Pedido ped = pedDAO.buscarPedidoPorId(pkpedido);
    ResultSet itens = pedDAO.listarItensDoPedido(pkpedido);
%>


<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Encripto - Detalhes do Pedido</title>
        <link rel="stylesheet" href="../css/detalhes_pedido.css">
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
                    <h2>Detalhes do Pedido <%= ped.pkpedido%></h2><br>

                    <div class="cart-header">
                        <span>Itens</span>
                        <span>Valor Unitário</span>
                        <span>Quantidade</span>
                        <span>Subtotal</span>
                    </div>

                    <%
                        while (itens != null && itens.next()) {
                    %>
                    <div class="cart-item">
                        <span><%= itens.getString("nomeProduto")%></span>
                        <span>R$ <%= itens.getDouble("valor_unitario")%></span>
                        <span><%= itens.getInt("quantidade")%></span>
                        <span>R$ <%= itens.getDouble("subtotal")%></span>
                    </div>
                    <%
                        }
                    %>


                    <br>
                    <div class="summary-details">
                        <div><span class="update">Frete: </span><span> R$ <%= ped.frete%></span></div>
                        <div><span class="update">Total: </span><span> R$ <%= ped.total%></span></div>
                        <div><span class="update">Forma de Pagamento: </span><span><%= ped.pagamento%></span></div>
                        <div><span class="update">Endereço: </span><span><%= ped.endereco%></span></div>
                        <div><span class="update">Status: </span><span><%= ped.status%></span></div>
                    </div>

                    <a href="pedidos_perfil.jsp" style="color:#007BFF; text-decoration: none;">⬅ Voltar</a>
                </div>
            </div>
        </main>
    </body>
</html>