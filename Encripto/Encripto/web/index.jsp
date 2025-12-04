<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Produto, java.sql.ResultSet" %>

<% 
String session_id = session.getId();
int session_id_int = Math.abs(session_id.hashCode() % 100);
session.setAttribute("session_id_int", session_id_int);

%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Encripto</title>
        <link rel="stylesheet" href="css/index.css"/>
        <style>
            @font-face {
                font-family: 'FonteMarca';
                src: url('../fonts/calora.otf') format('opentype');
                font-weight: normal;
                font-style: normal;
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
    </head>
    <body>
        <header>
            <div class="logo">
                <a href="index.jsp" style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</a>
            </div>
            <nav>
                <a href="jsps/carrinho_logado.jsp" class="cart" id="cart-btn">🛒</a>
                <a href="login.html" class="cart">👤</a>
            </nav>
        </header>

        <main>
            <!-- ======== CARROSSEL ======== -->
            <section class="carousel">
                <div class="slides" id="carousel">
                    <%
                        Produto prod1 = new Produto();
                        ResultSet imagens = prod1.listar();

                        if (imagens != null) {
                            while (imagens.next()) {
                    %>
                    <img src="<%= imagens.getString("imagem")%>" alt="Produto">
                    <%
                            }
                        }
                    %>
                </div>
            </section>

            <section class="section">
                <h2>Nossos Produtos</h2><br>
                <div class="cards">
                    <%
                        Produto prod = new Produto();
                        ResultSet produtos = prod.listar();

                        if (produtos != null) {
                            while (produtos.next()) {
                                int id = produtos.getInt("pkproduto");
                                String nome = produtos.getString("nome");
                                String valor = produtos.getString("valor");
                    %>
                    <div class="card">
                        <img src="<%= produtos.getString("imagem")%>">

                        <div class="card-info">
                            <h3><%= nome%></h3>
                            <p>R$ <%= valor%></p>
                            <a href="jsps/produto_naologado.jsp?id=<%= id%>" class="btn">Detalhes</a>
                            <form action="jsps/adicionarCarrinho.jsp" method="post" class="form-carrinho">
                                <input type="hidden" name="pkproduto" value="<%= id%>">
                                <input type="hidden" name="total" value="<%= valor%>">
                                <button type="submit" class="btn">Adicionar ao carrinho</button>
                            </form>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p>Nenhum produto encontrado.</p>
                    <%
                        }
                    %>
                </div>
            </section>
        </main>

        <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>

        <script>
            document.addEventListener("DOMContentLoaded", () => {
                const carousel = document.getElementById("carousel");
                if (carousel) {
                    let scrollAmount = 0;
                    function autoScroll() {
                        if (carousel.scrollLeft + carousel.clientWidth >= carousel.scrollWidth) {
                            scrollAmount = 0;
                        } else {
                            scrollAmount += 2;
                        }
                        carousel.scrollTo({left: scrollAmount, behavior: "smooth"});
                    }
                    setInterval(autoScroll, 80);
                }
            });
        </script>
    </body>
</html>
