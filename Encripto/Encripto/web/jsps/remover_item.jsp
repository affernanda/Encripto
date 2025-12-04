<%@ page import="JavaBeans.Carrinho, JavaBeans.Usuario, JavaBeans.CarrinhoTemporario" %>
<%
    
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    boolean deslogado = (user == null);
    
    if(!deslogado){
    int pkcar = Integer.parseInt(request.getParameter("pkcar"));
    Carrinho c = new Carrinho();
    c.removerItem(pkcar);
    response.sendRedirect("carrinho_logado.jsp");
    } else {
    int pkcartemp = Integer.parseInt(request.getParameter("pkcartemp"));
    CarrinhoTemporario ctemp = new CarrinhoTemporario();
    ctemp.removerItemTemp(pkcartemp);
    response.sendRedirect("carrinho_logado.jsp");
    }
%>
