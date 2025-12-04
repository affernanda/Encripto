<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Endereco, JavaBeans.Usuario, JavaBeans.Carrinho, JavaBeans.Produto, java.sql.ResultSet, java.util.List" %>

<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

// LISTA ENDEREÇOS
// LISTA CARRINHO
    Carrinho carrinhoDAO = new Carrinho();
    ResultSet itens = carrinhoDAO.listarCarrinho(user.pkuser);
    double totalCarrinho = 0;
    double subtotal = 0;
    double totalFinal = 0;
    double frete = 0;
    String pagamento = request.getParameter("pagamento");
    Integer endereco = null;
    String enderecoStr = request.getParameter("enderecoSelecionado");
    if (enderecoStr != null && !enderecoStr.isEmpty()) {
        endereco = Integer.parseInt(enderecoStr);
    }
    String freteStr = request.getParameter("frete");
    if (freteStr != null && !freteStr.isEmpty()) {
        if (freteStr.equals("5")) {
            frete = 5;
        } else if (freteStr.equals("10")) {
            frete = 10;
        } else if (freteStr.equals("20")) {
            frete = 20;
        }
    }
    Endereco enderecoDAO = new Endereco();
    List<Endereco> lista = enderecoDAO.listarEnderecos(user.pkuser);
    Endereco enderecoSelecionadoObj = null;

    if (endereco != null) {
        for (Endereco e : lista) {
            if (e.pkenderecos == endereco) {
                enderecoSelecionadoObj = e;
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Encripto - Resumo do Pedido</title>
        <link rel="stylesheet" href="../css/resumo.css">
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
                <a href="perfil.jsp" class="cart">👤</a>
            </nav>
        </header>

        <main>
            <div class="container">

                <!-- ================= RESUMO DO PEDIDO ================= -->
                <div class="cart-container">
                    <h2>Resumo do Pedido</h2>

                    <div class="cart-header">
                        <span>Produto</span>
                        <span>Valor Unitário</span>
                        <span>Quantidade</span>
                        <span>Subtotal</span>
                    </div>

                    <!-- Exemplo de exibição dinâmica -->
                    <%
                        while (itens != null && itens.next()) {
                            int pkcar = itens.getInt("pkcar");
                            int pkproduto = itens.getInt("pkproduto");
                            String nome = itens.getString("nome");
                            String valorStr = itens.getString("valor");
                            double valor = Double.parseDouble(valorStr);
                            int quantidade = itens.getInt("quantidade");
                            String imagem = itens.getString("imagem");
                            subtotal = valor * quantidade;
                            totalCarrinho += subtotal;
                    %>
                    <div class="cart-item">
                        <span><%= nome%></span>
                        <span>R$ <%= String.format("%.2f", valor)%></span>
                        <span><%= quantidade%></span>
                        <span><%= subtotal%></span>
                    </div>
                    <% }%>

                    <div class="summary-details">
                        <div><span>Subtotal</span><span>R$ <%= String.format("%.2f", totalCarrinho)%></span></div>
                        <div><span>Frete</span><span>R$ <%= frete%></span></div>
                        <div class="total"><span>Total</span><span>R$ <%= String.format("%.2f", totalFinal = totalCarrinho + frete)%></span></div>
                    </div>


                    <div class="actions" style="display:flex; gap:15px;">
                        <a href="carrinho_logado.jsp" class="checkout">Voltar</a>
                        <form action="../ConcluirCompra" method="post">
                            <input type="hidden" name="frete" id="input-frete" value="<%= frete%>">
                            <input type="hidden" name="endereco" id="input-endereco" 
                            value="<%= enderecoSelecionadoObj != null ? enderecoSelecionadoObj.logradouro : ""%>">
                            <input type="hidden" name="pagamento" id="input-pagamento" value="<%= pagamento%>">
                            <button type="submit" class="checkout" id="btnConcluir">Concluir Compra</button>
                        </form>
                    </div>
                </div>

                <!-- ================= ENDEREÇO E FRETE ================= -->
                <div class="summary">
                    <h3>Endereço Selecionado</h3>
                    <%
                        if (enderecoSelecionadoObj != null) {
                    %>
                    <p>
                        <strong>Endereço:</strong><br>
                        <%= enderecoSelecionadoObj.logradouro%>, nº <%= enderecoSelecionadoObj.numero%><br>
                        <%= enderecoSelecionadoObj.bairro%> - <%= enderecoSelecionadoObj.cidade%> / <%= enderecoSelecionadoObj.uf%><br>
                        CEP: <%= enderecoSelecionadoObj.cep%>
                    </p>
                    <%
                    } else {
                    %>
                    <p style="color:red;">Nenhum endereço selecionado.</p>
                    <%
                        }
                    %>

                    <p><strong>Opção de Frete:</strong><br>
                        Preço do frete: R$ <%= frete%>
                    </p>

                    <hr style="margin:20px 0;">

                    <h3>Pagamento</h3>
                    <p><strong>Forma de Pagamento Selecionada:</strong><br>
                        <%= pagamento%>
                    </p>

                </div>
            </div>
        </main>

        <footer>
            <p class="footer-logo">ENCRIPTO</p>
        </footer>

        <script>
            document.addEventListener("DOMContentLoaded", () => {
                const infoPagamento = document.getElementById("infoPagamento");
                const btnConcluir = document.getElementById("btnConcluir");
                const form = btnConcluir.closest("form");

                form.addEventListener("submit", (e) => {
                    // Se passar nas validações, exibe mensagem
                    alert("Compra concluída! Obrigado por comprar com a Encripto.");
                });
            });
        </script>
    </body>
</html>
