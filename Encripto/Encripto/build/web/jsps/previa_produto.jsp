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
            <div class="logo"><p href="" style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</p>
            </div>
            <nav>
                <p href="" class="cart" id="cart-btn">🛒</p>
                <p href="" class="cart">👤</p>
            </nav>
        </header>

        <main>

            <div class="body2">
                <div class="produto-container">
                    <%
                        Produto p = new Produto();
                        p.pkproduto = p.pkproduto;
                        p.nome = request.getParameter("nome");
                        p.descricao = request.getParameter("descricao");
                        p.avaliacao = request.getParameter("avaliacao");
                        p.quantidade = request.getParameter("quantidade");

                        String valorStr = request.getParameter("valor");
                        if (valorStr != null && !valorStr.isEmpty()) {
                            p.valor = Double.parseDouble(valorStr);
                        } else {
                            p.valor = null;
                        }

                        String img = request.getParameter("imagem");
                        if (img != null && !img.isEmpty()) {
                            if (!img.startsWith("imgs/")) {
                                img = "imgs/" + img;
                            }
                            p.imagem = img;
                        } else {
                            p.imagem = null;
                        }
                    %>
                    <img src="../<%= p.imagem%>" alt="<%= p.nome%>">
                    <div class="info-produto">
                        <div class="titulo-preco">
                            <h2><%= p.nome%></h2>
                            <span>R$ <%= p.valor%></span>
                        </div>
                        <hr>
                        <div class="descricao">
                            <p><%= p.descricao != null ? p.descricao : "Produto sem descrição disponível."%></p>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>

        <script src="../scripts/produto.js"></script>

    </body>

</html>