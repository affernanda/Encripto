<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="JavaBeans.Usuario, JavaBeans.Endereco" %>
<%
    // Recupera o usuário logado
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    if (user == null) {
        response.sendRedirect("../login.html");
        return;
    }
    request.setCharacterEncoding("UTF-8");

    // Cria o usuario
    Usuario u = new Usuario();
    u.pkuser = user.pkuser;
    u.nome = request.getParameter("nome");
    u.email = request.getParameter("email");
    u.cpf = request.getParameter("CPF");
    String dataStr = request.getParameter("dataNascimento");
    if (dataStr != null && !dataStr.isEmpty()) {
        u.dataNascimento = java.sql.Date.valueOf(dataStr);
    } else {
        u.dataNascimento = null;
    }
    u.genero = request.getParameter("genero");
    u.senhaHash = request.getParameter("senha");
    u.cargo = request.getParameter("cargo");

    u.incluir(); // insere no banco
    
    if (u.statusSQL != null){
        out.println("<script>alert('" + u.statusSQL + "'); window.location.href='novo_usuario.jsp';</script>");
        return; // interrompe o fluxo se deu erro
    } else {
        out.println("<script>alert('Usuario cadastrado'); window.location.href='novo_usuario.jsp';</script>");
    }
%>
