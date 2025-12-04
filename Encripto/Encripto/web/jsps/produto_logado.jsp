<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Produto, java.sql.ResultSet" %>

<!DOCTYPE html>

<style>
    @font-face {
        font-family: 'FonteMarca';
        src: url('../fonts/calora.otf') format('opentype');
        font-weight: normal;
        font-style: normal;
    }

    .btn {
        display: inline-block;
        background: #ff4d6d;
        color: #fff;
        text-decoration: none;
        cursor: pointer;
        font-size: 0.85rem;
        padding: 5px 12px;
        border: none;
        border-radius: 5px;
        margin-top: 10px
    }

    .btn:hover{
        background: #ff1a45;
        border: none;
    }
    
    footer {
    padding: 80px 40px 50px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    margin: 0;
}

.footer-logo {
    font-family: 'FonteMarca', sans-serif;
    font-size: 180px;
    font-weight: 900;
    letter-spacing: -8px;
    color: #e3e3e3;
    opacity: 0.45;
    line-height: 1;
    margin: 0;
    text-align:center;
}
</style>

<html lang="pt-BR">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Encripto - Produto</title>
        <link rel="stylesheet" href="../css/produto.css" />
    </head>

    <body>
        <header>
            <div class="logo"><a href="../index_logado.jsp"
                                 style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</a>
            </div>
            <nav>
                <a href="carrinho_logado.jsp" class="cart" id="cart-btn">🛒</a>
                <a href="perfil.jsp" class="cart">👤</a>
            </nav>
        </header>

        <main>

            <div class="body2">
                <div class="produto-container">
                    <%
                        String idParam = request.getParameter("id");
                        if (idParam != null) {
                            try {
                                int id = Integer.parseInt(idParam);

                                Produto prod = new Produto();
                                ResultSet produto = prod.buscarPorId(id);

                                if (produto != null && produto.next()) {
                                    String nome = produto.getString("nome");
                                    String valor = produto.getString("valor");
                                    String imagem = produto.getString("imagem");
                                    String descricao = produto.getString("descricao");
                    %>
                    <img src="../<%= imagem%>" alt="<%= nome%>">
                    <div class="info-produto">
                        <div class="titulo-preco">
                            <h2><%= nome%></h2>
                            <span>R$ <%= valor%></span>
                        </div>
                        <hr>
                        <div class="descricao">
                            <p><%= descricao != null ? descricao : "Produto sem descrição disponível."%></p>
                        </div>
                        <form action="adicionarCarrinho.jsp" method="post" class="form-carrinho">
                            <input type="hidden" name="pkproduto" value="<%= id%>">
                            <input type="hidden" name="total" value="<%= valor%>">
                            <button type="submit" class="btn">Adicionar ao carrinho</button>
                        </form>
                    </div>
                    <%
                    } else {
                    %>
                    <p>Produto não encontrado.</p>
                    <%
                        }
                    } catch (NumberFormatException e) {
                    %>
                    <p>ID inválido.</p>
                    <%
                        }
                    } else {
                    %>
                    <p>Nenhum produto selecionado.</p>
                    <%
                        }
                    %>
                </div>
            </div>
        </main>

        <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>

        <script src="../scripts/produto.js"></script>

    </body>

</html>
