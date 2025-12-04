package JavaBeans;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/EditarPedido")
public class EditarPedido extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int pkpedido = Integer.parseInt(request.getParameter("pkpedido"));
        String status = request.getParameter("status");

        Pedido ped = new Pedido();
        ped.pkpedido = pkpedido;
        ped.status = status;

        if(ped.editarStatus()) {
            response.sendRedirect(request.getContextPath() + "/jsps/pedidos_estoquista.jsp");
        } else {
            response.getWriter().println("Erro ao atualizar status!");
        }
    }
}
