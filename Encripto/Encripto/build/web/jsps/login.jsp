<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import = "JavaBeans.Usuario"%>
<%
    session = request.getSession();
    String email = request.getParameter("email");
    String senha = request.getParameter("senha");
    
    Usuario user = new Usuario();
    
    user.email = email;
    user.senhaHash = senha;
    
    if (user.checarLogin()) {
        session.setAttribute("usuarioLogado", user);

        response.sendRedirect(request.getContextPath() + "/index_logado.html");
    } else {
        out.println("<script>alert('Email ou senha incorretos!'); window.location.href='../login.html';</script>");
    }
%>
