<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Endereco, JavaBeans.Usuario, java.util.List" %>
<%
    // Recupera o usuário logado da sessão
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");

    // Se não estiver logado, redireciona para login
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }

    // Busca os endereços do usuário logado
    Endereco enderecoDAO = new Endereco();
    List<Endereco> lista = enderecoDAO.listarEnderecos(user.pkuser);
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Endereços - Encripto</title>

    <style>
        @font-face {
            font-family: 'FonteMarca';
            src: url('../fonts/calora.otf') format('opentype');
        }
        .form {
            font-size: 18px;
        }
        table {
            width: 100%;
        }
        td {
            padding: 5px 10px;
        }
        .padrao {
            color: green;
            font-weight: bold;
        }
    </style>

    <link rel="stylesheet" href="../css/enderecos.css" />
</head>
<body>
    <header>
        <div class="logo">
            <a href="../index_logado.html"
                style="text-decoration:none; color:black; font-family:'FonteMarca',sans-serif; font-size:50px;">Encripto</a>
        </div>
        <nav>
            <a href="#" class="cart" id="cart-btn">🛒</a>
            <a href="perfil.jsp" class="cart">👤</a>
        </nav>
    </header>

    <main class="profile-section">
        <div class="profile-container">
            <div class="profile-info">
                <div class="edit">
                    <div>
                        <h1>Endereços</h1>
                    </div>
                    <div>
                        <button class="carousel-btn prev">❮</button>
                        <button class="carousel-btn next">❯</button>
                    </div>
                    <a href="../enderecos_editar.html">Incluir Endereço</a>
                </div>

                <div class="carousel-container">
                    <div class="carousel">
                        <% 
                            if (lista != null && !lista.isEmpty()) { 
                                for (Endereco e : lista) { 
                        %>
                        <div class="form-slide">
                            <form class="form">
                                <table>
                                    <tr>
                                        <td><strong>Endereço:</strong> 
                                            <span class="<%= e.padrao ? "padrao" : "" %>">
                                                <%= e.padrao ? "Principal" : "Secundário" %>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr><td><strong>CEP:</strong> <%= e.cep %></td></tr>
                                    <tr><td><strong>Logradouro:</strong> <%= e.logradouro %>, nº <%= e.numero %></td></tr>
                                    <tr><td><strong>Complemento:</strong> <%= e.complemento != null && !e.complemento.isEmpty() ? e.complemento : "—" %></td></tr>
                                    <tr><td><strong>Bairro:</strong> <%= e.bairro %></td></tr>
                                    <tr><td><strong>Cidade:</strong> <%= e.cidade %></td></tr>
                                    <tr><td><strong>UF:</strong> <%= e.uf %></td></tr>
                                </table>
                            </form>
                        </div>
                        <% 
                                } 
                            } else { 
                        %>
                        <div class="form-slide">
                            <p>Nenhum endereço cadastrado ainda.</p>
                        </div>
                        <% } %>
                    </div>
                </div>

                <table>
                    <tr>
                        <td colspan="2">
                            <a href="perfil.jsp" class="update">Voltar ao Perfil →</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </main>

    <script src="../scripts/enderecos.js"></script>
    <script src="../scripts/index.js"></script>
</body>
</html>
