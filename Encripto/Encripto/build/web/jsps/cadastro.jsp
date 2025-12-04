<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Usuario, JavaBeans.Endereco, java.sql.Date, java.text.SimpleDateFormat" %>
<%
    Usuario user = new Usuario();

    user.nome = request.getParameter("nome");
    user.email = request.getParameter("email");
    user.cpf = request.getParameter("cpf");
    user.genero = request.getParameter("genero");
    user.senhaHash = request.getParameter("senha");

    String dataStr = request.getParameter("dataNascimento");
    if (dataStr != null && !dataStr.isEmpty()) {
        user.dataNascimento = java.sql.Date.valueOf(dataStr);
    } else {
        user.dataNascimento = null;
    }
    user.cargo = "Usuario";

    user.incluir();

    if (user.statusSQL != null) {
        out.println("<script>alert('" + user.statusSQL + "'); window.location.href='../cadastro.html';</script>");
        return; // interrompe o fluxo se deu erro
    } else {
        session.setAttribute("usuarioLogado", user);
    }

    // --- Endereço ---
    Endereco end = new Endereco();
    end.pkuser = user.pkuser;
    end.cep = request.getParameter("cep");
    end.logradouro = request.getParameter("logradouro");
    end.numero = request.getParameter("numero");
    end.complemento = request.getParameter("complemento");
    end.bairro = request.getParameter("bairro");
    end.cidade = request.getParameter("cidade");
    end.uf = request.getParameter("uf");
    end.padrao = request.getParameter("padrao") != null; // true se marcado

    // Inclui endereço e garante que só um padrão exista
    end.incluirEndereco();

    if (end.statusSQL != null) {
        out.println("<script>alert('" + end.statusSQL + "'); window.location.href='../cadastro.html';</script>");
        return;
    } else {
        out.println("<script>alert('Usuário e endereço criados com sucesso!'); window.location.href='../login.html';</script>");
    }
%>
