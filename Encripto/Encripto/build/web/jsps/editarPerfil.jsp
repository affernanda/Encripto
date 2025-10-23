<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="JavaBeans.Usuario, java.text.SimpleDateFormat"%>

<%
    try {
        // Obtém a sessão e recupera o usuário logado
        Usuario user = (Usuario) session.getAttribute("usuarioLogado");

        if (user == null) {
            // Se não há usuário na sessão, manda pra login
            response.sendRedirect("login.jsp?erro=nao_logado");
            return;
        }

        // Atualiza apenas campos enviados
        String novoNome = request.getParameter("nome");
        String novoGenero = request.getParameter("genero");
        String novaSenha = request.getParameter("senha");
        String novaData = request.getParameter("dataNascimento");

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            user.nome = novoNome.trim();
        }

        if (novoGenero != null && !novoGenero.trim().isEmpty()) {
            user.genero = novoGenero.trim();
        }

        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            user.senhaHash = Usuario.hashSenha(novaSenha);
        }

        if (novaData != null && !novaData.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsed = sdf.parse(novaData);
            user.dataNascimento = new java.sql.Date(parsed.getTime());
        }

        // Atualiza no banco
        boolean sucesso = user.atualizarDados();

        if (sucesso) {
            // Atualiza sessão
            session.setAttribute("usuarioLogado", user);
            response.sendRedirect("perfil.jsp?status=ok");
        } else {
            out.print("<script>alert('Erro ao atualizar dados!'); history.back();</script>");
        }

    } catch (Exception e) {
        out.print("<script>alert('Erro inesperado: " + e.getMessage() + "'); history.back();</script>");
    }
%>
