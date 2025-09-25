package JavaPackage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Imagem extends ClasseMaeBD {

    public Integer pkimagem;
    public Integer produto_id;
    public String nome_arquivo;
    public String diretorio;
    public Integer principal;

    // ================= LISTAGEM DE IMAGENS ===================
    public List<Imagem> listarImagens() {
        List<Imagem> lista2 = new ArrayList<>();
        try {
            conectar();
            String sql = "SELECT pkimagem, produto_id, nome_arquivo, diretorio, principal "
                    + "FROM imagem_produto ORDER BY pkimagem DESC";
            ResultSet tab = con.createStatement().executeQuery(sql);
            while (tab.next()) {
                Imagem i = new Imagem();
                i.pkimagem = tab.getInt("pkimagem");
                i.produto_id = tab.getInt("produto_id");
                i.nome_arquivo = tab.getString("nome_arquivo");
                i.diretorio = tab.getString("diretorio");
                i.principal = tab.getInt("principal");
                lista2.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar imagens: " + e.getMessage());
        }
        return lista2;
    }

    public void imprimirImagem(List<Imagem> lista) {
        System.out.println("ID_Imagem | ID_Produto | Nome_Arquivo | Diretorio | Imagem_Principal");
        System.out.println("-----------------------------------------------------------");
        for (Imagem i : lista) {
            String linha = String.format("%-30s|%-30s|%-30s|%-30s|%-30s",
                    i.pkimagem, i.produto_id, i.nome_arquivo, i.diretorio, i.principal);
            System.out.println(linha);
        }
    }

    // ================= INCLUIR IMAGEM ===================
    public void incluirImagemProduto(Scanner sc, int idProduto) {
        while (true) {
            System.out.println("=== Inclusao de imagem para produto ID: " + idProduto + " ===");
            System.out.print("Nome do arquivo: ");
            String nomeArquivo = sc.nextLine().trim();
            System.out.print("Diretorio: img/" + idProduto + "/");
            String diretorio = sc.nextLine().trim();
            System.out.print("Imagem principal? (S/N): ");
            boolean principal = sc.nextLine().trim().equalsIgnoreCase("S");

            System.out.print("Salvar alteracoes? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return;
            }

            try {
                conectar();
                if (principal) {
                    String sql = "UPDATE imagem_produto SET principal=0 WHERE produto_id=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, idProduto);
                    ps.executeUpdate();
                }
                String sql = "INSERT INTO imagem_produto (produto_id, nome_arquivo, diretorio, principal) VALUES (?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idProduto);
                ps.setString(2, nomeArquivo);
                ps.setString(3, diretorio);
                ps.setBoolean(4, principal);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Erro ao incluir imagem: " + e.getMessage());
            }

            System.out.println("Deseja incluir outra imagem? (S/N): ");
            String op = sc.nextLine().trim();
            if (!op.equalsIgnoreCase("S")) {
                break;
            }
        }
    }

    // ================= EDITAR IMAGEM ===================
    public void editarImagemProduto(Scanner sc, int pkimagem) {
        try {
            conectar();
            String sql = "SELECT * FROM imagem_produto WHERE pkimagem=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, pkimagem);
            ResultSet tab = ps.executeQuery();

            if (!tab.next()) {
                System.out.println("Imagem não encontrada.");
                return;
            }

            // Dados atuais
            int produtoId = tab.getInt("produto_id");
            String nomeAtual = tab.getString("nome_arquivo");
            String dirAtual = tab.getString("diretorio");
            boolean principalAtual = tab.getBoolean("principal");

            System.out.println("=== Edição de Imagem (ID " + pkimagem + ") ===");
            System.out.print("Novo nome de arquivo [" + nomeAtual + "]: ");
            String nomeArquivo = sc.nextLine().trim();
            if (nomeArquivo.isEmpty()) {
                nomeArquivo = nomeAtual;
            }

            System.out.print("Novo diretório [" + dirAtual + "]: ");
            String diretorio = sc.nextLine().trim();
            if (diretorio.isEmpty()) {
                diretorio = dirAtual;
            }

            System.out.print("Imagem principal? (S/N) [" + (principalAtual ? "S" : "N") + "]: ");
            String respPrincipal = sc.nextLine().trim().toUpperCase();
            boolean principalNovo = respPrincipal.isEmpty() ? principalAtual : respPrincipal.equals("S");

            System.out.print("Salvar alterações? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operação cancelada.");
                return;
            }

            beginTx();
            if (principalNovo) {
                // Zera as principais desse produto antes de definir a nova
                sql = "UPDATE imagem_produto SET principal=0 WHERE produto_id=?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, produtoId);
                ps.executeUpdate();
            }

            sql = "UPDATE imagem_produto SET nome_arquivo=?, diretorio=?, principal=? WHERE pkimagem=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nomeArquivo);
            ps.setString(2, diretorio);
            ps.setBoolean(3, principalNovo);
            ps.setInt(4, pkimagem);

            int linhasAfetadas = ps.executeUpdate();
            commitTx();

            if (linhasAfetadas > 0) {
                System.out.println("Imagem atualizada com sucesso!");
            } else {
                System.out.println("Nenhuma alteração realizada.");
            }

        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao editar imagem: " + e.getMessage());
        }
    }

}
