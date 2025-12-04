package JavaBeans;

import java.sql.SQLException;
import java.sql.ResultSet;

public class Produto extends Conexao {

    public int pkproduto;
    public String nome;
    public String descricao;
    public String avaliacao;
    public String quantidade;
    public Double valor;
    public String imagem; // ← agora é byte[]

    // Inserir produto
    public boolean inserir() {
        try {
            sql = "INSERT INTO produto (nome, descricao, avaliacao, quantidade, valor, imagem) VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, descricao);
            ps.setString(3, avaliacao);
            ps.setString(4, quantidade);
            ps.setDouble(5, valor);
            ps.setString(6, imagem); // ← grava o binário
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        }
    }

    // Listar produtos
    public ResultSet listar() {
        try {
            sql = "SELECT pkproduto, nome, descricao, valor, imagem FROM produto";
            ps = con.prepareStatement(sql);
            tab = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tab;
    }

    // Buscar imagem por ID
    public ResultSet buscarImagemPorId(int id) {
        try {
            sql = "SELECT imagem FROM produto WHERE pkproduto=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            tab = ps.executeQuery();
            return tab;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar imagem: " + e.getMessage());
            return null;
        }
    }

    public double getValor() {
        try {
            sql = "SELECT valor FROM produtos WHERE pkproduto = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, this.pkproduto);
            tab = ps.executeQuery();
            if (tab.next()) {
                return tab.getDouble("valor");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ResultSet buscarPorId(int id) {
        try {
            sql = "SELECT * FROM produto WHERE pkproduto = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            tab = ps.executeQuery();
            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
