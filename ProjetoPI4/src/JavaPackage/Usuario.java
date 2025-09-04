package JavaPackage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Usuario extends ClasseMaeBD {

    public Integer pkuser;
    public String email;
    public String senha; // apenas para leitura; em BD usamos hash
    public String nome;
    public String cpf;
    public String grupo; // "ADMIN" ou "ESTOQUISTA"
    public Boolean status; // true=ativo, false=inativo

    // ===== Utilidades =====

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean emailExiste(String email) throws SQLException {
        sql = "SELECT 1 FROM usuario WHERE email=?";
        ps = con.prepareStatement(sql);
        ps.setString(1, email);
        tab = ps.executeQuery();
        return tab.next();
    }

    private boolean cpfExiste(String cpf) throws SQLException {
        sql = "SELECT 1 FROM usuario WHERE cpf=?";
        ps = con.prepareStatement(sql);
        ps.setString(1, cpf);
        tab = ps.executeQuery();
        return tab.next();
    }

    // ===== Autenticacao =====

    public Usuario autenticar(String email, String senhaPura) {
        try {
            conectar();
            sql = "SELECT pkuser, nome, email, cpf, grupo, status " +
                  "FROM usuario WHERE email=? AND senha_hash=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, senhaPura);
            tab = ps.executeQuery();
            if (tab.next()) {
                Usuario u = new Usuario();
                u.pkuser = tab.getInt("pkuser");
                u.nome = tab.getString("nome");
                u.email = tab.getString("email");
                u.cpf = tab.getString("cpf");
                u.grupo = tab.getString("grupo");
                u.status = tab.getBoolean("status");
                if (!u.status) return null; // bloqueia inativos
                return u;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar: " + e.getMessage(), e);
        }
    }

    // ===== Cadastro =====

    public void incluirUsuario(Scanner sc) {
        try {
            conectar();
            System.out.println("=== Inclusao de Usuario ===");
            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            System.out.print("CPF (somente números ou com mascara): ");
            String cpf = sc.nextLine().trim();

            if (emailExiste(email)) {
                System.out.println("ERRO: ja existe usuario com este e-mail.");
                return;
            }
            if (cpfExiste(cpf)) {
                System.out.println("ERRO: ja existe usuario com este CPF.");
                return;
            }

            String senha1, senha2;
            while (true) {
                System.out.print("Senha: ");
                senha1 = sc.nextLine();
                System.out.print("Confirme a senha: ");
                senha2 = sc.nextLine();
                if (senha1.equals(senha2)) break;
                System.out.println("As senhas nao coincidem. Tente novamente.");
            }

            String grupo;
            while (true) {
                System.out.print("Grupo [1-Administrador | 2-Estoquista]: ");
                String g = sc.nextLine().trim();
                if (g.equals("1")) { grupo = "ADMIN"; break; }
                if (g.equals("2")) { grupo = "ESTOQUISTA"; break; }
                System.out.println("Opcao invalida.");
            }

            System.out.println("Novo usuario sera criado como ATIVO.");
            System.out.print("Salvar? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return;
            }

            beginTx();
            sql = "INSERT INTO usuario (nome, email, cpf, senha_hash, grupo, status) VALUES (?,?,?,?,?,1)";
            ps = con.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setString(4, senha1);
            ps.setString(5, grupo);
            ps.executeUpdate();
            commitTx();
            System.out.println("Usuario incluído com sucesso.");
        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao incluir: " + e.getMessage());
        }
    }

    // ===== Listagem =====

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try {
            conectar();
            sql = "SELECT pkuser, nome, email, status, grupo FROM usuario ORDER BY pkuser";
            tab = con.createStatement().executeQuery(sql);
            while (tab.next()) {
                Usuario u = new Usuario();
                u.pkuser = tab.getInt("pkuser");
                u.nome = tab.getString("nome");
                u.email = tab.getString("email");
                u.status = tab.getBoolean("status");
                u.grupo = tab.getString("grupo");
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public void imprimirUsuarios(List<Usuario> lista) {
        System.out.println("ID | Nome                         | Email                        | Status | Grupo");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Usuario u : lista) {
            String st = (u.status != null && u.status) ? "ATIVO " : "INATIVO";
            String linha = String.format("%-3s| %-28s| %-28s| %-7s| %-11s",
                    u.pkuser, corta(u.nome,28), corta(u.email,28), st, u.grupo);
            System.out.println(linha);
        }
    }

    private static String corta(String s, int n) {
        if (s == null) return "";
        if (s.length() <= n) return s;
        return s.substring(0, n-1) + "…";
    }

    // ===== Atualizacao =====

    public void atualizarDados(Scanner sc) {
        try {
            conectar();
            System.out.print("Informe o ID do usuario a alterar: ");
            String idtxt = sc.nextLine().trim();
            Integer id = Integer.parseInt(idtxt);

            sql = "SELECT pkuser, nome, email, cpf, grupo, status FROM usuario WHERE pkuser=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            tab = ps.executeQuery();
            if (!tab.next()) {
                System.out.println("Usuario nao encontrado.");
                return;
            }

            String nomeAtual = tab.getString("nome");
            String emailAtual = tab.getString("email");
            String cpfAtual = tab.getString("cpf");
            String grupoAtual = tab.getString("grupo");
            boolean statusAtual = tab.getBoolean("status");

            System.out.println("Alterando usuario: " + nomeAtual + " (" + emailAtual + ")");
            System.out.print("Novo nome [" + nomeAtual + "]: ");
            String novoNome = sc.nextLine().trim();
            if (novoNome.isEmpty()) novoNome = nomeAtual;

            System.out.print("Novo CPF [" + cpfAtual + "]: ");
            String novoCpf = sc.nextLine().trim();
            if (novoCpf.isEmpty()) novoCpf = cpfAtual;

            String novoGrupo;
            while (true) {
                System.out.print("Novo grupo (1-Administrador, 2-Estoquista) [" + grupoAtual + "]: ");
                String g = sc.nextLine().trim();
                if (g.isEmpty()) { novoGrupo = grupoAtual; break; }
                if (g.equals("1")) { novoGrupo = "ADMIN"; break; }
                if (g.equals("2")) { novoGrupo = "ESTOQUISTA"; break; }
                System.out.println("Opcao invalida.");
            }

            String novaSenhaHash = null;
            System.out.print("Deseja alterar a senha? (S/N): ");
            String alt = sc.nextLine().trim().toUpperCase();
            if (alt.equals("S")) {
                while (true) {
                    System.out.print("Nova senha: ");
                    String s1 = sc.nextLine();
                    System.out.print("Confirme a nova senha: ");
                    String s2 = sc.nextLine();
                    if (s1.equals(s2)) { novaSenhaHash = s1; break; }
                    System.out.println("As senhas nao coincidem. Tente novamente.");
                }
            }

            // valida CPF duplicado se mudou
            if (!novoCpf.equals(cpfAtual) && cpfExiste(novoCpf)) {
                System.out.println("ERRO: ja existe usuario com este CPF.");
                return;
            }

            System.out.print("Salvar alteracoes? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return;
            }

            beginTx();
            if (novaSenhaHash == null) {
                sql = "UPDATE usuario SET nome=?, cpf=?, grupo=? WHERE pkuser=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, novoNome);
                ps.setString(2, novoCpf);
                ps.setString(3, novoGrupo);
                ps.setInt(4, id);
            } else {
                sql = "UPDATE usuario SET nome=?, cpf=?, grupo=?, senha_hash=? WHERE pkuser=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, novoNome);
                ps.setString(2, novoCpf);
                ps.setString(3, novoGrupo);
                ps.setString(4, novaSenhaHash);
                ps.setInt(5, id);
            }
            ps.executeUpdate();
            commitTx();
            System.out.println("Usuario atualizado com sucesso.");
        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao atualizar: " + e.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("ID invalido.");
        }
    }

    // ===== Ativar / Inativar =====

    public void alternarStatus(Scanner sc) {
        try {
            conectar();
            System.out.print("Informe o ID do usuario: ");
            String idtxt = sc.nextLine().trim();
            Integer id = Integer.parseInt(idtxt);

            sql = "SELECT status, nome FROM usuario WHERE pkuser=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            tab = ps.executeQuery();
            if (!tab.next()) {
                System.out.println("Usuario nao encontrado.");
                return;
            }
            boolean statusAtual = tab.getBoolean("status");
            String nome = tab.getString("nome");

            System.out.println("Usuario: " + nome + " esta atualmente " + (statusAtual ? "ATIVO" : "INATIVO"));
            System.out.print("Deseja " + (statusAtual ? "desativar" : "ativar") + "? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if (!conf.equals("S")) {
                System.out.println("Operacao cancelada.");
                return;
            }

            beginTx();
            sql = "UPDATE usuario SET status=? WHERE pkuser=?";
            ps = con.prepareStatement(sql);
            ps.setBoolean(1, !statusAtual);
            ps.setInt(2, id);
            ps.executeUpdate();
            commitTx();
            System.out.println("Status atualizado com sucesso.");
        } catch (SQLException e) {
            rollbackTx();
            System.out.println("Erro ao alterar status: " + e.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("ID invalido.");
        }
    }

    // ===== Produtos =====

    public void listarProdutos() {
        try {
            conectar();
            sql = "SELECT pkproduto, nome, descricao FROM produto ORDER BY pkproduto";
            tab = con.createStatement().executeQuery(sql);
            System.out.println("ID | Produto                      | Descricao");
            System.out.println("---------------------------------------------------------------");
            while (tab.next()) {
                int id = tab.getInt("pkproduto");
                String nome = tab.getString("nome");
                String desc = tab.getString("descricao");
                String linha = String.format("%-3d| %-28s| %s", id, corta(nome,28), desc==null? "": desc);
                System.out.println(linha);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
    }
}
