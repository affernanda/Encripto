<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="JavaBeans.Produto, JavaBeans.Usuario, java.sql.ResultSet" %>
<%
    // Recupera o usuÃ¡rio logado
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }
    request.setCharacterEncoding("UTF-8");

    // Cria o produto
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

    p.inserir();

    if (p.statusSQL != null) {
        out.println("<script>alert('" + p.statusSQL + "'); window.location.href='administrador.jsp';</script>");
        return; // interrompe o fluxo se deu erro
    } else {
        out.println("<script>alert('Produto cadastrado'); window.location.href='administrador.jsp';</script>");
    }

%>