<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Pedido, JavaBeans.Usuario, java.util.List"%>

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
        <title>Encripto - NovoProduto</title>
        <link rel="stylesheet" href="../css/novo_usuario.css">
        <style>
            .edit button[type="submit"] {
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

            .edit button[type="submit"]:hover {
                background-color: #ff1a45;
            }
        </style>
    </head>
    <header>
        <div class="logo">
            <a href="administrador.jsp" style="text-decoration: none; color: white; font-family: 'FonteMarca', sans-serif; font-size: 50px;">Encripto</a>
        </div>
        <nav>
            <a href="logout.jsp">Deslogar</a>
        </nav>
    </header>
    <body>
        <main class="profile-section">
            <div class="profile-container">
                <div class="profile-info">
                    <form class="form" method="post">
                        <div class="edit">
                            <div>
                                <h1>Novo Produto</h1>
                            </div>
                            <button type="submit" formaction="previa_produto.jsp">Visualizar Prévia</button>
                            <button type="submit" formaction="novoProduto.jsp">Salvar</button>
                        </div>
                        <table>
                            <tr>
                                <td><label for="nome">Nome</label></td>
                                <td><input type="text" id="nome" placeholder="nome" name="nome" required></td>
                            </tr>
                            <tr>
                                <td><label for="descricao">Descrição</label></td>
                                <td><input type="text" id="descricao" placeholder="descricao" name="descricao" required></td>
                            </tr>
                            <tr>
                                <td><label for="avaliacao">Avaliação</label></td>
                                <td><input type="number" min="0" max="5" id="avaliacao" placeholder="avaliacao" name="avaliacao" required></td>
                            </tr>
                            <tr>
                                <td><label for="quantidade">Quantidade</label></td>
                                <td><input type="number" id="quantidade" placeholder="quantidade" name="quantidade" required></td>
                            </tr>
                            <tr>
                                <td><label for="valor">Valor</label></td>
                                <td><input type="number" id="valor" placeholder="valor" name="valor" required></td>
                            </tr>
                            <tr>
                                <td><label for="imagem">Caminho da Imagem</label></td>
                                <td><input type="text" id="imagem" placeholder="imgs/xxxx.png" name="imagem" required></td>
                            </tr>
                        </table>
                    </form>
                    <a href="administrador.jsp" class="update">⬅Voltar a Home</a>
                </div>
            </div>
        </main>
        <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>
    </body>
</html>