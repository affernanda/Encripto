<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, JavaBeans.Carrinho, JavaBeans.CarrinhoTemporario, JavaBeans.Usuario" %>

<%
    session = request.getSession();
    CarrinhoTemporario carrinhotempDAO = new CarrinhoTemporario();
    String email = request.getParameter("email");
    String senha = request.getParameter("senha");
    carrinhotempDAO.session_id = (Integer) session.getAttribute("session_id_int");
    Usuario user = new Usuario();

    user.email = email;
    user.senhaHash = senha;

    if (user.checarLogin()) {
        if (user.cargo.equals("Usuario")) {
            int pkuser = user.pkuser;
            session.setAttribute("usuarioLogado", user);
            carrinhotempDAO.insertnew(carrinhotempDAO.session_id, pkuser);
            carrinhotempDAO.limparCarrinhoTemp(carrinhotempDAO.session_id);
            response.sendRedirect(request.getContextPath() + "/index_logado.jsp");
        } else if (user.cargo.equals("Estoquista")) {
            session.setAttribute("usuarioLogado", user);
            response.sendRedirect(request.getContextPath() + "/jsps/estoquista.jsp");
        } else if (user.cargo.equals("Admin")) {
            session.setAttribute("usuarioLogado", user);
            response.sendRedirect(request.getContextPath() + "/jsps/administrador.jsp");
        }
    } else {
        out.println("<script>alert('Email ou senha incorretos!'); window.location.href='../login.html';</script>");
    }
%>
