<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Pedido" %>

<%
    int pkpedido = Integer.parseInt(request.getParameter("pkpedido"));
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Encripto - Editar Pedido</title>
        <link rel="stylesheet" href="../css/editar_pedido.css">
    </head>
    <body>

        <header>
            <div class="logo">
                <a href="estoquista.jsp"
                   style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</a>
            </div>
            <nav>
                <a href="logout.jsp">Deslogar</a>
            </nav>
        </header>

        <main>
            <div class="container">

                <div class="cart-container">
                    <h2>Alterar Status do Pedido</h2><br>

                    <form action="../EditarPedido" method="post">

                        <input type="hidden" name="pkpedido" value="<%= pkpedido%>">

                        <div class="cart-header">
                            <span>Pedido</span>
                            <span>Status</span>
                        </div>

                        <div class="cart-item">
                            <span class="update">Nº <%= pkpedido%></span>

                            <select name="status" id="status">
                                <option value="Aguardando pagamento">Aguardando pagamento</option>
                                <option value="Pagamento rejeitado">Pagamento rejeitado</option>
                                <option value="Pagamento ok">Pagamento bem-sucedido</option>
                                <option value="Aguardando retirada">Aguardando retirada</option>
                                <option value="Em transito">Em trânsito</option>
                                <option value="Entregue">Entregue</option>
                            </select>

                            <button type="submit">Salvar ✔</button>
                        </div>
                    </form>

                    <br>
                    <a href="pedidos_estoquista.jsp" style="color:#007BFF; text-decoration: none;">⬅ Voltar</a>
                </div>
            </div>
        </main>

        <footer>
            <p class="footer-logo">ENCRIPTO</p>
        </footer>

    </body>
</html>
