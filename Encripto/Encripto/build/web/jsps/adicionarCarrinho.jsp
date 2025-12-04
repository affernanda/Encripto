<%@ page import="java.util.*, JavaBeans.Carrinho, JavaBeans.CarrinhoTemporario, JavaBeans.Produto, JavaBeans.Usuario" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    boolean deslogado = (user == null);

    int pkproduto = Integer.parseInt(request.getParameter("pkproduto"));
    Integer session_id_int = (Integer) session.getAttribute("session_id_int");

    Produto prod = new Produto();
    prod.pkproduto = pkproduto;
    double valor = prod.getValor();

    if (!deslogado) { 
        Carrinho car = new Carrinho();
        car.pkuser = user.pkuser;
        car.pkproduto = pkproduto;
        car.quantidade = 1;
        car.total = valor;

        car.adicionarAoCarrinho();
        out.println("<script>alert('Produto adicionado ao carrinho!'); window.location.href='../index_logado.jsp';</script>");

    } else {
        CarrinhoTemporario cartemp = new CarrinhoTemporario();
        cartemp.session_id = session_id_int;
        cartemp.pkproduto = pkproduto;
        cartemp.quantidade = 1;
        cartemp.total = valor;

        cartemp.adicionarAoCarrinhoTemp();
        out.println("<script>alert('Produto adicionado ao carrinho!'); window.location.href='../index.jsp';</script>");
    }

%>