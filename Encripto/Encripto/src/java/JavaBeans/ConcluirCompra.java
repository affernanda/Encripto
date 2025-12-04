package JavaBeans;

import JavaBeans.Carrinho;
import JavaBeans.Pedido;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ConcluirCompra")
public class ConcluirCompra extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        JavaBeans.Usuario user = (JavaBeans.Usuario) session.getAttribute("usuarioLogado");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        try {
            Carrinho carrinhoDAO = new Carrinho();
            ResultSet itens = carrinhoDAO.listarCarrinho(user.pkuser);

            // 1. Criar o pedido (só uma vez)
            Pedido pedido = new Pedido();
            pedido.pkuser = user.pkuser;
            pedido.data_pedido = new java.sql.Date(System.currentTimeMillis());
            pedido.status = "Aguardando pagamento";
            pedido.total = carrinhoDAO.calcularTotal(user.pkuser); // você precisa desse método
            pedido.pagamento = request.getParameter("pagamento");
            pedido.endereco = request.getParameter("endereco");
            pedido.frete = request.getParameter("frete");

            pedido.criarPedido();  // método novo que cria apenas o registro na tabela pedido

            // 2. Inserir os itens do carrinho no pedido_itens
            while (itens.next()) {
                int pkproduto = itens.getInt("pkproduto");
                int quantidade = itens.getInt("quantidade");
                double valor = itens.getDouble("valor");
                double subtotal = valor * quantidade;

                pedido.inserirItem(pkproduto, quantidade, valor, subtotal);
            }

            // 3. Limpar carrinho
            carrinhoDAO.limparCarrinho(user.pkuser);

            // 4. Redirecionar
            response.sendRedirect(request.getContextPath() + "/jsps/pedidos_perfil.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Erro ao concluir compra: " + e.getMessage());
        }
    }
}
