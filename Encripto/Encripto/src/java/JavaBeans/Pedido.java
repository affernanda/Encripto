package JavaBeans;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Pedido extends Conexao {

    public Integer pkpedido;
    public Integer pkuser;
    public Integer pkproduto;
    public String status;
    public Double total;
    public Integer quantidade;
    public String pagamento;
    public String endereco;
    public String frete;
    public java.sql.Date data_pedido;

    public Pedido() {
        super();
    }

    public void incluirPedido() {
        try {
            // 1 - Criar o pedido
            this.status = "Aguardando pagamento";

            sql = "INSERT INTO pedido (pkuser, status, total, data_pedido, pagamento, endereco, frete) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pkuser);
            ps.setString(2, status);
            ps.setDouble(3, total); // total do carrinho calculado antes
            ps.setDate(4, data_pedido);
            ps.setString(5, pagamento);
            ps.setString(6, endereco);
            ps.setString(7, frete);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.pkpedido = rs.getInt(1);
            }

            // 2 - Inserir itens do carrinho
            Carrinho car = new Carrinho();
            ResultSet itens = car.listarCarrinho(pkuser);

            while (itens.next()) {
                int pkproduto = itens.getInt("pkproduto");
                int quantidade = itens.getInt("quantidade");
                double subtotal = itens.getDouble("total");

                sql = "INSERT INTO item_pedido (pkpedido, pkproduto, quantidade, subtotal) VALUES (?, ?, ?, ?)";
                ps = con.prepareStatement(sql);
                ps.setInt(1, pkpedido);
                ps.setInt(2, pkproduto);
                ps.setInt(3, quantidade);
                ps.setDouble(4, subtotal);
                ps.executeUpdate();
            }

            // 3 - Limpar carrinho
            car.limparCarrinho(pkuser);

            statusSQL = "Pedido criado corretamente!";
        } catch (SQLException e) {
            e.printStackTrace();
            statusSQL = "Erro ao criar pedido: " + e.getMessage();
        }
    }

    // Listar todos os pedidos
    public List<Pedido> listarPedidos() {
        List<Pedido> lista = new ArrayList<>();
        try {
            sql = "SELECT * FROM pedido ORDER BY pkpedido DESC";
            ps = con.prepareStatement(sql);
            tab = ps.executeQuery();

            while (tab.next()) {
                Pedido ped = new Pedido();
                ped.pkpedido = tab.getInt("pkpedido");
                ped.pkuser = tab.getInt("pkuser");
                ped.status = tab.getString("status");
                ped.total = tab.getDouble("total");
                ped.data_pedido = tab.getDate("data_pedido");
                ped.pagamento = tab.getString("pagamento");
                ped.endereco = tab.getString("endereco");
                ped.frete = tab.getString("frete");

                lista.add(ped);
            }
        } catch (SQLException e) {
            statusSQL = "Erro ao listar pedidos: " + e.getMessage();
        }
        return lista;
    }

    public List<Pedido> listarPedidosPorUsuario(int pkuser) {
        List<Pedido> lista = new ArrayList<>();

        try {
            sql = "SELECT * FROM pedido WHERE pkuser = ? ORDER BY pkpedido DESC";
            ps = con.prepareStatement(sql);
            ps.setInt(1, pkuser);
            tab = ps.executeQuery();

            while (tab.next()) {
                Pedido ped = new Pedido();
                ped.pkpedido = tab.getInt("pkpedido");
                ped.pkuser = tab.getInt("pkuser");
                ped.status = tab.getString("status");
                ped.total = tab.getDouble("total");
                ped.data_pedido = tab.getDate("data_pedido");

                // Buscar quantidade de itens no pedido
                String sql2 = "SELECT SUM(quantidade) AS totalItens FROM pedido_itens WHERE pkpedido = ?";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setInt(1, ped.pkpedido);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    ped.quantidade = rs2.getInt("totalItens");
                }

                lista.add(ped);
            }
        } catch (SQLException e) {
            statusSQL = "Erro ao listar pedidos: " + e.getMessage();
        }

        return lista;
    }

    // Editar status
    public boolean editarStatus() {
        try {
            sql = "UPDATE pedido SET status = ? WHERE pkpedido = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, pkpedido);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            statusSQL = "Erro ao atualizar status: " + e.getMessage();
            return false;
        }
    }

    public void criarPedido() throws SQLException {
        sql = "INSERT INTO pedido (pkuser, status, total, data_pedido, pagamento, endereco, frete) VALUES (?, ?, ?, ?, ?, ?, ?)";
        ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, pkuser);
        ps.setString(2, status);
        ps.setDouble(3, total);
        ps.setDate(4, data_pedido);
        ps.setString(5, pagamento);
        ps.setString(6, endereco);
        ps.setString(7, frete);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            pkpedido = rs.getInt(1);
        }
    }

    public void inserirItem(int pkproduto, int quantidade, double unitario, double subtotal) throws SQLException {
        sql = "INSERT INTO pedido_itens (pkpedido, pkproduto, quantidade, valor_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        ps = con.prepareStatement(sql);
        ps.setInt(1, pkpedido);
        ps.setInt(2, pkproduto);
        ps.setInt(3, quantidade);
        ps.setDouble(4, unitario);
        ps.setDouble(5, subtotal);
        ps.executeUpdate();
    }

    public Pedido buscarPedidoPorId(int pkpedido) {
        try {
            sql = "SELECT * FROM pedido WHERE pkpedido = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, pkpedido);
            tab = ps.executeQuery();

            if (tab.next()) {
                Pedido ped = new Pedido();
                ped.pkpedido = tab.getInt("pkpedido");
                ped.pkuser = tab.getInt("pkuser");
                ped.status = tab.getString("status");
                ped.total = tab.getDouble("total");
                ped.data_pedido = tab.getDate("data_pedido");
                ped.pagamento = tab.getString("pagamento");
                ped.endereco = tab.getString("endereco");
                ped.frete = tab.getString("frete");

                return ped;
            }

        } catch (SQLException e) {
            statusSQL = "Erro ao buscar pedido: " + e.getMessage();
        }
        return null;
    }

    public ResultSet listarItensDoPedido(int pkpedido) {
        try {
            sql = "SELECT pi.*, p.nome AS nomeProduto, p.valor AS valor_unitario "
                    + "FROM pedido_itens pi "
                    + "INNER JOIN produto p ON pi.pkproduto = p.pkproduto "
                    + "WHERE pi.pkpedido = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, pkpedido);
            return ps.executeQuery();

        } catch (SQLException e) {
            statusSQL = "Erro ao listar itens do pedido: " + e.getMessage();
            return null;
        }
    }

}
