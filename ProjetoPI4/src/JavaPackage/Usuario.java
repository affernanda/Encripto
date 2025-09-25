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
    public String senha;
    public String nome;
    public String cpf;
    public String grupo;
    public Boolean status;
    
    // ================= SHA256 ====================
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

    // ================= UTILITARIOS ====================
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
    
    private boolean cpfValido(String cpf) throws SQLException {
        if (cpf == null) return false;

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Deve ter 11 dígitos
        if (cpf.length() != 11) return false;

        // Não pode ser uma sequência repetida
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Calcula primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;

            // Calcula segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;

            // Verifica se os dígitos calculados conferem com os do CPF
            return cpf.charAt(9) - '0' == firstDigit && cpf.charAt(10) - '0' == secondDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ================= AUTENTICACAO ====================
    public Usuario autenticar(String email, String senhaPura) {
        try {
            conectar();
            sql = "SELECT pkuser, nome, email, cpf, senha_hash, grupo, status " +
                  "FROM usuario WHERE email=? AND senha_hash=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, sha256(senhaPura));
            tab = ps.executeQuery();
            if (tab.next()) {
                Usuario u = new Usuario();
                u.pkuser = tab.getInt("pkuser");
                u.nome = tab.getString("nome");
                u.email = tab.getString("email");
                u.cpf = tab.getString("cpf");
                u.senha = tab.getString("senha_hash");
                u.grupo = tab.getString("grupo");
                u.status = tab.getBoolean("status");
                if (!u.status) return null;
                return u;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar: " + e.getMessage(), e);
        }
    }
    
    // ================= CADASTRO ====================
    public void incluirUsuario(Scanner sc) {
        try {
            conectar();
            System.out.println("=== Inclusao de Usuario ===");

            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            System.out.print("CPF: ");
            String cpf = sc.nextLine().trim();

            if(emailExiste(email)) { System.out.println("ERRO: ja existe usuario com este e-mail."); return; }
            if(cpfExiste(cpf)) { System.out.println("ERRO: ja existe usuario com este CPF."); return; }
            if(!cpfValido(cpf)) { System.out.println("ERRO: o CPF nao e valido, insira um CPF valido."); return; }

            String senha1, senha2;
            while(true){
                System.out.print("Senha: "); senha1 = sc.nextLine();
                System.out.print("Confirme a senha: "); senha2 = sc.nextLine();
                if(senha1.equals(senha2)) break;
                System.out.println("Senhas nao coincidem.");
            }

            String grupo;
            while(true){
                System.out.print("Grupo [1-ADMIN | 2-ESTOQUISTA]: ");
                String g = sc.nextLine().trim();
                if(g.equals("1")) { grupo="ADMIN"; break; }
                if(g.equals("2")) { grupo="ESTOQUISTA"; break; }
                System.out.println("Opcao invalida.");
            }

            System.out.print("Salvar? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if(!conf.equals("S")) { System.out.println("Operacao cancelada."); return; }

            beginTx();
            sql = "INSERT INTO usuario (nome, email, cpf, senha_hash, grupo, status) VALUES (?,?,?,?,?,1)";
            ps = con.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setString(4, sha256(senha1));
            ps.setString(5, grupo);
            ps.executeUpdate();
            commitTx();

            System.out.println("Usuario incluido com sucesso.");

        } catch(SQLException e) {
            rollbackTx();
            System.out.println("Erro ao incluir: " + e.getMessage());
        }
    }

    // ================= LISTAGEM ====================
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try{
            conectar();
            sql = "SELECT pkuser, nome, email, status, grupo FROM usuario ORDER BY pkuser";
            tab = con.createStatement().executeQuery(sql);
            while(tab.next()){
                Usuario u = new Usuario();
                u.pkuser = tab.getInt("pkuser");
                u.nome = tab.getString("nome");
                u.email = tab.getString("email");
                u.status = tab.getBoolean("status");
                u.grupo = tab.getString("grupo");
                lista.add(u);
            }
        }catch(SQLException e){ System.out.println("Erro ao listar usuarios: "+e.getMessage()); }
        return lista;
    }

    public void imprimirUsuarios(List<Usuario> lista){
        System.out.println("ID | Nome | Email | Status | Grupo");
        System.out.println("-----------------------------------------------");
        for(Usuario u: lista){
            String st = (u.status!=null && u.status) ? "ATIVO" : "INATIVO";
            String linha = String.format("%-3s| %-28s| %-28s| %-7s| %-11s",
                    u.pkuser, u.nome, u.email, st, u.grupo);
            System.out.println(linha);
        }
    }

    // ================= ATUALIZACAO ====================
    public void atualizarDados(Scanner sc) {
        try{
            conectar();
            System.out.print("ID do usuario a alterar: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            sql = "SELECT * FROM usuario WHERE pkuser=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            tab = ps.executeQuery();
            if(!tab.next()){ System.out.println("Usuario nao encontrado."); return; }

            String nomeAtual = tab.getString("nome");
            String cpfAtual = tab.getString("cpf");
            String grupoAtual = tab.getString("grupo");

            System.out.print("Novo nome ["+nomeAtual+"]: ");
            String novoNome = sc.nextLine().trim(); if(novoNome.isEmpty()) novoNome=nomeAtual;

            System.out.print("Novo CPF ["+cpfAtual+"]: ");
            String novoCpf = sc.nextLine().trim(); if(novoCpf.isEmpty()) novoCpf=cpfAtual;

            String novoGrupo;
            while(true){
                System.out.print("Novo grupo (1-ADMIN, 2-ESTOQUISTA) ["+grupoAtual+"]: ");
                String g = sc.nextLine().trim();
                if(g.isEmpty()){ novoGrupo=grupoAtual; break; }
                if(g.equals("1")){ novoGrupo="ADMIN"; break; }
                if(g.equals("2")){ novoGrupo="ESTOQUISTA"; break; }
                System.out.println("Opcao invalida.");
            }

            System.out.print("Alterar senha? (S/N): ");
            String alt = sc.nextLine().trim().toUpperCase();
            String novaSenha=null;
            if(alt.equals("S")){
                while(true){
                    System.out.print("Nova senha: "); String s1=sc.nextLine();
                    System.out.print("Confirme: "); String s2=sc.nextLine();
                    if(s1.equals(s2)){ novaSenha=s1; break; }
                    System.out.println("Senhas nao coincidem.");
                }
            }

            if(!novoCpf.equals(cpfAtual) && cpfExiste(novoCpf) && cpfValido(novoCpf)) { System.out.println("CPF ja existe ou não é válido"); return; }

            System.out.print("Salvar alteracoes? (S/N): ");
            String conf = sc.nextLine().trim().toUpperCase();
            if(!conf.equals("S")){ System.out.println("Operacao cancelada."); return; }

            beginTx();
            if(novaSenha==null){
                sql="UPDATE usuario SET nome=?, cpf=?, grupo=? WHERE pkuser=?";
                ps=con.prepareStatement(sql);
                ps.setString(1,novoNome);
                ps.setString(2,novoCpf);
                ps.setString(3,novoGrupo);
                ps.setInt(4,id);
            }else{
                sql="UPDATE usuario SET nome=?, cpf=?, grupo=?, senha_hash=? WHERE pkuser=?";
                ps=con.prepareStatement(sql);
                ps.setString(1,novoNome);
                ps.setString(2,novoCpf);
                ps.setString(3,novoGrupo);
                ps.setString(4,sha256(novaSenha));
                ps.setInt(5,id);
            }
            ps.executeUpdate();
            commitTx();
            System.out.println("Usuario atualizado com sucesso.");

        }catch(SQLException e){ rollbackTx(); System.out.println("Erro ao atualizar: "+e.getMessage());
        }catch(NumberFormatException nfe){ System.out.println("ID invalido."); }
    }

    // ================= ATIVAR / INATIVAR ====================
    public void alternarStatus(Scanner sc){
        try{
            conectar();
            System.out.print("ID do usuario: ");
            int id=Integer.parseInt(sc.nextLine().trim());
            sql="SELECT status,nome FROM usuario WHERE pkuser=?";
            ps=con.prepareStatement(sql);
            ps.setInt(1,id);
            tab=ps.executeQuery();
            if(!tab.next()){ System.out.println("Usuario nao encontrado."); return; }

            boolean statusAtual=tab.getBoolean("status");
            String nome=tab.getString("nome");
            System.out.println("Usuario: "+nome+" esta "+(statusAtual?"ATIVO":"INATIVO"));
            System.out.print("Deseja "+(statusAtual?"desativar":"ativar")+"? (S/N): ");
            String conf=sc.nextLine().trim().toUpperCase();
            if(!conf.equals("S")){ System.out.println("Operacao cancelada."); return; }

            beginTx();
            sql="UPDATE usuario SET status=? WHERE pkuser=?";
            ps=con.prepareStatement(sql);
            ps.setBoolean(1,!statusAtual);
            ps.setInt(2,id);
            ps.executeUpdate();
            commitTx();
            System.out.println("Status atualizado com sucesso.");

        }catch(SQLException e){ rollbackTx(); System.out.println("Erro: "+e.getMessage());
        }catch(NumberFormatException nfe){ System.out.println("ID invalido."); }
    }
}
