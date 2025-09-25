package JavaPackage;

import application.TelaPrincipal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Produto extends ClasseMaeBD {

    public Integer pkproduto;
    public String nome;
    public String descricao;
    public Double avaliacao;
    public Integer quantidade;
    public Double valor;
    public Boolean status;

    // ================= UTILITARIOS ===================
    private static String corta(String s, int n) {
        if (s == null) {
            return "";
        }
        if (s.length() <= n) {
            return s;
        }
        return s.substring(0, n - 1) + "…";
    }

    // ================= LISTAGEM DE PRODUTOS ===================
    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        try {
            conectar();
            String sql = "SELECT pkproduto, nome, descricao, avaliacao, quantidade, valor, status "
                    + "FROM produto ORDER BY pkproduto DESC";
            ResultSet tab = con.createStatement().executeQuery(sql);
            while (tab.next()) {
                Produto p = new Produto();
                p.pkproduto = tab.getInt("pkproduto");
                p.nome = tab.getString("nome");
                p.descricao = tab.getString("descricao");
                p.avaliacao = tab.getDouble("avaliacao");
                p.quantidade = tab.getInt("quantidade");
                p.valor = tab.getDouble("valor");
                p.status = tab.getBoolean("status");
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    public void imprimirProdutos(List<Produto> lista) {
        System.out.println("ID | Nome | Quantidade | Valor | Avaliacao | Status");
        System.out.println("-----------------------------------------------------------");
        for (Produto p : lista) {
            String st = (p.status != null && p.status) ? "ATIVO" : "INATIVO";
            String linha = String.format("%-3s| %-28s| %-9s| %-10.2f| %-8.1f| %-8s",
                    p.pkproduto, corta(p.nome, 28), p.quantidade, p.valor, p.avaliacao, st);
            System.out.println(linha);
        }
    }

    // ================= INCLUIR PRODUTO ===================
    public void incluirProduto(Scanner sc) {
        try {
            conectar();
            System.out.println("=== Inclusao de Produto ===");

            System.out.print("Nome do produto: ");
            String nome = sc.nextLine().trim();

            System.out.print("Descricao detalhada: ");
            String descricao = sc.nextLine().trim();

            Double avaliacao;
            while (true) {
                try {
                    System.out.print("Avaliacao (0 a 5, step 0.5): ");
                    avaliacao = Double.parseDouble(sc.nextLine().trim());
                    if (avaliacao >= 0 && avaliacao <= 5 && (avaliacao * 2) % 1 == 0) {
                        break;
                    }
                    System.out.println("Valor invalido.");
                } catch (NumberFormatException e) {
                    System.out.println("Entrada invalida. Digite apenas numeros (ex: 0, 1.5, 5).");
                }
            }

            System.out.print("Quantidade em estoque: ");
            int qtd = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Valor do produto (R$): ");
            double valor = Double.parseDouble(sc.nextLine().trim());

            beginTx();
            String sql = "INSERT INTO produto (nome, descricao, avaliacao, quantidade, valor, status) VALUES (?,?,?,?,?,1)";
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, nome);
            ps.setString(2, descricao);
            ps.setDouble(3, avaliacao);
            ps.setInt(4, qtd);
            ps.setDouble(5, valor);
            ps.executeUpdate();

            ResultSet tab = ps.getGeneratedKeys();
            int idProduto = 0;
            if (tab.next()) {
                idProduto = tab.getInt(1);
            }

            commitTx();
            System.out.println("Produto incluido com sucesso. ID = " + idProduto);

            // Chama menu de incluir imagem
            Imagem img = new Imagem();
            img.incluirImagemProduto(sc, idProduto);

        } catch (Exception e) {
            rollbackTx();
            System.out.println("Erro ao incluir produto: " + e.getMessage());
        }
    }

    // ================= EDITAR PRODUTO ===================
    public void editarProduto(Scanner sc, int idProduto) {
        while (true) {
            System.out.println("O que deseja?");
            System.out.println("1) Editar produto");
            System.out.println("2) Editar imagem");
            System.out.println("3) Ativar/inativar produto");
            System.out.println("0) Voltar");
            System.out.print("Escolha: ");

            int escolha = Integer.parseInt(sc.nextLine().trim());

            if (escolha == 1) {
                try {
                    conectar();
                    String sql = "SELECT * FROM produto WHERE pkproduto=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, idProduto);
                    ResultSet tab = ps.executeQuery();
                    if (!tab.next()) {
                        return;
                    }

                    System.out.print("Novo nome [" + tab.getString("nome") + "]: ");
                    String nome = sc.nextLine().trim();
                    if (nome.isEmpty()) {
                        nome = tab.getString("nome");
                    }

                    System.out.print("Nova descricao [" + tab.getString("descricao") + "]: ");
                    String descricao = sc.nextLine().trim();
                    if (descricao.isEmpty()) {
                        descricao = tab.getString("descricao");
                    }

                    double avaliacao;
                    while (true) {
                        try {
                            System.out.print("Avaliacao (0 a 5, step 0.5): ");
                            String avalStr = sc.nextLine().trim();
                            if (avalStr.isEmpty()) {
                                avaliacao = tab.getDouble("avaliacao");
                                break;
                            }
                            avaliacao = Double.parseDouble(avalStr);

                            if (avaliacao >= 0 && avaliacao <= 5 && (avaliacao * 2) % 1 == 0) {
                                break;
                            } else {
                                System.out.println("Valor invalido. Digite um número de 0 a 5 em passos de 0.5.");
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Entrada invalida. Digite apenas numeros (ex: 0, 1.5, 5).");
                        }
                    }

                    System.out.print("Nova quantidade [" + tab.getInt("quantidade") + "]: ");
                    String qtdStr = sc.nextLine().trim();
                    int quantidade = qtdStr.isEmpty() ? tab.getInt("quantidade") : Integer.parseInt(qtdStr);

                    System.out.print("Novo valor [" + tab.getDouble("valor") + "]: ");
                    String valStr = sc.nextLine().trim();
                    double valor = valStr.isEmpty() ? tab.getDouble("valor") : Double.parseDouble(valStr);

                    beginTx();
                    sql = "UPDATE produto SET nome=?, descricao=?, avaliacao=?, quantidade=?, valor=? WHERE pkproduto=?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, nome);
                    ps.setString(2, descricao);
                    ps.setDouble(3, avaliacao);
                    ps.setInt(4, quantidade);
                    ps.setDouble(5, valor);
                    ps.setInt(6, idProduto);
                    ps.executeUpdate();
                    commitTx();

                    System.out.println("Produto atualizado com sucesso.");
                    
                } catch (SQLException e) {
                    rollbackTx();
                    System.out.println("Erro ao atualizar produto: " + e.getMessage());
                }
            } else if (escolha == 2) {
                TelaPrincipal menu = new TelaPrincipal();
                menu.menuImagem();
            } else if (escolha == 3) {
                alternarStatusProduto(sc, idProduto);
            } else {
                return;
            }
        }
    }

    public boolean editarQuantidadeProduto(Scanner sc, int idProduto) {
        try {
            conectar();
            String sql = "SELECT quantidade, nome FROM produto WHERE pkproduto=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idProduto);
            ResultSet tab = ps.executeQuery();
            if (!tab.next()) {
                System.out.println("Produto não encontrado.");
                return false;
            }

            int qtdAtual = tab.getInt("quantidade");
            String nome = tab.getString("nome");

            System.out.println("Produto: " + nome + " (Quantidade atual: " + qtdAtual + ")");
            System.out.print("Nova quantidade: ");
            String qtdStr = sc.nextLine().trim();
            int novaQtd = qtdStr.isEmpty() ? qtdAtual : Integer.parseInt(qtdStr);

            System.out.print("Salvar alteracoes? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return false;
            }

            sql = "UPDATE produto SET quantidade=? WHERE pkproduto=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, novaQtd);
            ps.setInt(2, idProduto);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Quantidade atualizada com sucesso.");
                return true;
            } else {
                System.out.println("Nenhuma alteração realizada.");
                return false;
            }

        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao atualizar quantidade: " + e.getMessage());
            return false;
        }
    }

    // ================= ATIVAR / INATIVAR PRODUTO ===================
    public void alternarStatusProduto(Scanner sc, int idProduto) {
        try {
            conectar();
            String sql = "SELECT status, nome FROM produto WHERE pkproduto=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idProduto);
            ResultSet tab = ps.executeQuery();
            if (!tab.next()) {
                System.out.println("Produto nao encontrado.");
                return;
            }

            boolean statusAtual = tab.getBoolean("status");
            String nome = tab.getString("nome");
            System.out.println("Produto: " + nome + " esta atualmente " + (statusAtual ? "ATIVO" : "INATIVO"));
            System.out.print("Deseja " + (statusAtual ? "desativar" : "ativar") + "? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return;
            }

            beginTx();
            sql = "UPDATE produto SET status=? WHERE pkproduto=?";
            ps = con.prepareStatement(sql);
            ps.setBoolean(1, !statusAtual);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
            commitTx();

            System.out.println("Status atualizado com sucesso.");

        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao alterar status: " + e.getMessage());
        }
    }
}
