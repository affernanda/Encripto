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

    // Cria o endereço
    Endereco e = new Endereco();
    e.pkuser = user.pkuser;
    e.cep = request.getParameter("cep");
    e.logradouro = request.getParameter("logradouro");
    e.numero = request.getParameter("numero");
    e.complemento = request.getParameter("complemento");
    e.bairro = request.getParameter("bairro");
    e.cidade = request.getParameter("cidade");
    e.uf = request.getParameter("uf");
    e.padrao = "sim".equalsIgnoreCase(request.getParameter("padrao"));

    e.incluirEndereco(); // insere no banco

    if (e.statusSQL == null) {
        String origem = request.getParameter("origem");
        String destino;

        if ("carrinho".equalsIgnoreCase(origem)) {
            destino = "carrinho_logado.jsp";
        } else {
            destino = "perfil.jsp";
        }

        out.println("<script>");
        out.println("alert('Endereço cadastrado com sucesso!');");
        out.println("window.location.href='" + destino + "';");
        out.println("</script>");
    } else {
        out.println("<script>alert('Erro ao cadastrar endereço: " + e.statusSQL + "'); history.back();</script>");
    }
%>
