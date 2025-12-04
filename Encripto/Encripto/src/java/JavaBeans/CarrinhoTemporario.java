package JavaBeans;

import java.sql.SQLException;
import java.sql.ResultSet;

public class CarrinhoTemporario extends Conexao {

    public int pkcartemp;
    public int session_id;
    public int pkproduto;
    public double total;
    public int quantidade;

    // ======== INSERIR ITEM NO CARRINHO ========
    public void adicionarAoCarrinhoTemp() {
        try {
            // 1️⃣ Buscar valor do produto
            sql = "SELECT valor FROM produto WHERE pkproduto = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, this.pkproduto);
            ResultSet rsProd = ps.executeQuery();

            double valorProduto = 0;
            if (rsProd.next()) {
                valorProduto = rsProd.getDouble("valor");
            }

            // total correto do item
            double totalItem = valorProduto * this.quantidade;

            // 2️⃣ Verificar se já existe no carrinho
            sql = "SELECT quantidade, total FROM carrinhoTemporario WHERE session_id = ? AND pkproduto = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, this.session_id);
            ps.setInt(2, this.pkproduto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int quantidadeAtual = rs.getInt("quantidade");
                double totalAtual = rs.getDouble("total");

                int novaQuantidade = quantidadeAtual + this.quantidade;
                double novoTotal = totalAtual + totalItem;

                sql = "UPDATE carrinhoTemporario SET quantidade = ?, total = ? WHERE session_id = ? AND pkproduto = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, novaQuantidade);
                ps.setDouble(2, novoTotal);
                ps.setInt(3, this.session_id);
                ps.setInt(4, this.pkproduto);
                ps.executeUpdate();
            } else {
                sql = "INSERT INTO carrinhoTemporario (session_id, pkproduto, quantidade, total) VALUES (?, ?, ?, ?)";
                ps = con.prepareStatement(sql);
                ps.setInt(1, this.session_id);
                ps.setInt(2, this.pkproduto);
                ps.setInt(3, this.quantidade);
                ps.setDouble(4, totalItem);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======== LISTAR ITENS DO CARRINHO ========
    public ResultSet listarCarrinhoTemp(int session_id) {
        try {
            sql = "SELECT c.pkcartemp, c.pkproduto, c.quantidade, p.nome, p.valor, p.imagem "
                    + "FROM carrinhoTemporario c INNER JOIN produto p ON c.pkproduto = p.pkproduto "
                    + "WHERE c.session_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, session_id);
            tab = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Erro ao listar carrinho: " + e);
        }
        return tab;
    }

    // ======== REMOVER ITEM DO CARRINHO ========
    public void removerItemTemp(int pkcartemp) {
        try {
            sql = "DELETE FROM carrinhoTemporario WHERE pkcartemp = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, pkcartemp);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao remover item: " + e);
        }
    }

    // ======== LIMPAR CARRINHO APÓS COMPRA ========
    public void limparCarrinhoTemp(int session_id) {
        try {
            sql = "DELETE FROM carrinhoTemporario WHERE session_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, session_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao limpar carrinho: " + e);
        }
    }

    // ======== CALCULAR TOTAL DO CARRINHO ========
    public double calcularTotalTemp(int session_id) {
        double total = 0.0;

        try {
            sql = "SELECT SUM(total) AS totalCarrinho FROM carrinhoTemporario WHERE session_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, session_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("totalCarrinho");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao calcular total do carrinho: " + e);
        }

        return total;
    }

    public void insertnew(int session_id, int pkuser) {
        try {
            sql = "INSERT INTO carrinho (pkuser, pkproduto, total, quantidade) "
                    + "SELECT ?, pkproduto, total, quantidade "
                    + "FROM carrinhoTemporario "
                    + "WHERE session_id = ? ";

            ps = con.prepareStatement(sql);

            ps.setInt(1, pkuser);
            ps.setInt(2, session_id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir produtos temporários no Carrinho: " + e.getMessage());
        }
    }
}
