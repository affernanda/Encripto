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
        <title>Encripto - NovoUsuario</title>
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
                    <form class="form" action="novoUsuario.jsp" method="post">
                        <div class="edit">
                            <div>
                                <h1>Novo Usuário</h1>
                            </div>
                            <button type="submit">Salvar</button>
                        </div>
                        <table>
                            <tr>
                                <td><label for="nome">Nome</label></td>
                                <td><input type="text" id="nome" placeholder="nome" name="nome" required></td>
                            </tr>
                            <tr>
                                <td><label for="email">Email</label></td>
                                <td><input type="text" id="email" placeholder="email" name="email" required></td>
                            </tr>
                            <tr>
                                <td><label for="cpf">CPF</label></td>
                                <td><input type="text" id="CPF" placeholder="CPF" name="CPF" required></td>
                            </tr>
                            <tr>
                                <td><label for="dataNascimento">Data de Nascimento</label></td>
                                <td><input type="date" id="dataNascimento" placeholder="dataNascimento"
                                           name="dataNascimento" required></td>
                            </tr>
                            <tr>
                                <td><label for="genero">Gênero</label></td>
                                <td>
                                    <div class="radio">
                                        <label><input type="radio" name="genero" value="Mulher" required>Mulher</label>
                                        <label><input type="radio" name="genero" value="Homem" required>Homem</label>
                                        <label><input type="radio" name="genero" value="Não-binário" required>Não-binário</label>
                                        <label><input type="radio" name="genero" value="Não Informar" required>Não Informar</label>
                                        <label><input type="radio" name="genero" value="Outros" required> Outros</label>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><label for="senha">Senha</label></td>
                                <td><input type="password" id="senha" placeholder="senha" name="senha" required></td>
                            </tr>
                            <tr>
                                <td><label for="cargo">Cargo</label></td><td>
                                    <select name="cargo" id="cargo" required>
                                        <option value="Admin">Administrador</option>
                                        <option value="Estoquista">Estoquista</option>
                                        <option value="Usuario">Usuario</option>
                                    </select></td>
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
