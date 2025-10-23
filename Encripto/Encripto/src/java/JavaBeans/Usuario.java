package JavaBeans;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usuario extends Conexao {

    public Integer pkuser;
    public String nome;
    public String email;
    public String cpf;
    public java.sql.Date dataNascimento;
    public String genero;
    public String senhaHash;

    public Usuario() {
        super();
    }

    // Hash SHA-256
    public static String hashSenha(String senha) throws NoSuchAlgorithmException {
    if (senha == null || senha.trim().isEmpty()) {
        return null; // retorna null se a senha não foi informada
    }
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(senha.getBytes());
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
}


    private boolean cpfExiste(String cpf) throws SQLException {
        sql = "SELECT 1 FROM usuario WHERE cpf=?";
        ps = con.prepareStatement(sql);
        ps.setString(1, cpf);
        tab = ps.executeQuery();
        return tab.next();
    }

    private boolean cpfValido(String cpf) throws SQLException {
        if (cpf == null) {
            return false;
        }

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Não pode ser uma sequência repetida
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Calcula primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }

            // Calcula segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }

            // Verifica se os dígitos calculados conferem com os do CPF
            return cpf.charAt(9) - '0' == firstDigit && cpf.charAt(10) - '0' == secondDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checarLogin() throws NoSuchAlgorithmException {
        try {
            sql = "select * from usuario where email = ? and senha_hash = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, hashSenha(senhaHash));
            tab = ps.executeQuery();
            if (tab.next()) {
                this.pkuser = tab.getInt("pkuser");
                this.nome = tab.getString("nome");
                this.email = tab.getString("email");
                this.cpf = tab.getString("cpf");
                this.dataNascimento = tab.getDate("data_nascimento");
                this.genero = tab.getString("genero");
                this.senhaHash = tab.getString("senha_hash");
                return true;
            }
            this.statusSQL = null;
        } catch (SQLException ex) {
            this.statusSQL = "Erro ao tentar buscar Usuário! Tente novamente! " + ex.getMessage();
        }
        return false;
    }

    // Verifica se email existe
    public boolean checarEmail() {
        try {
            sql = "select * from usuario where email = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            tab = ps.executeQuery();
            if (tab.next()) {
                this.pkuser = tab.getInt("pkuser");
                this.nome = tab.getString("nome");
                this.email = tab.getString("email");
                this.cpf = tab.getString("cpf");
                this.dataNascimento = tab.getDate("data_nascimento");
                this.genero = tab.getString("genero");
                this.senhaHash = tab.getString("senha_hash");
                return true;
            }
            this.statusSQL = null;
        } catch (SQLException ex) {
            this.statusSQL = "Erro ao tentar buscar o email! Tente novamente! " + ex.getMessage();
        }
        return false;
    }

    public void incluir() throws NoSuchAlgorithmException, SQLException {
        if (checarEmail()) {
            this.statusSQL = "ERRO: ja existe usuario com este e-mail.";
            return;
        }
        if (cpfExiste(cpf)) {
            this.statusSQL = "ERRO: ja existe usuario com este CPF.";
            return;
        }
        if (!cpfValido(cpf)) {
            this.statusSQL = "ERRO: o CPF nao e valido, insira um CPF valido.";
            return;
        }
        try {
            sql = "INSERT INTO usuario (nome, email, cpf, data_nascimento, genero, senha_hash) VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS); // <- ESSENCIAL
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setDate(4, dataNascimento);
            ps.setString(5, genero);
            ps.setString(6, hashSenha(senhaHash));

            int rowsAffected = ps.executeUpdate(); // <- Confirma se inseriu

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.pkuser = generatedKeys.getInt(1); // <- Captura o pkuser
                }
            } else {
                this.statusSQL = "Nenhuma linha foi inserida.";
            }

        } catch (SQLException e) {
            this.statusSQL = "Erro ao inserir usuário: " + e.getMessage();
        }
    }

    public boolean atualizarDados() throws NoSuchAlgorithmException {
        sql = "UPDATE usuario SET ";
        boolean hasField = false;

        if (this.nome != null) {
            sql += "nome = ?, ";
            hasField = true;
        }
        if (this.email != null) {
            sql += "email = ?, ";
            hasField = true;
        }
        if (this.cpf != null) {
            sql += "cpf = ?, ";
            hasField = true;
        }
        if (this.dataNascimento != null) {
            sql += "data_nascimento = ?,";
            hasField = true;
        }
        if (this.genero != null) {
            sql += "genero = ?,";
            hasField = true;
        }
        if (this.senhaHash != null) {
            sql += "senha_hash = ?,";
            hasField = true;
        }

        sql = sql.trim();
        if (sql.endsWith(",")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        sql += " WHERE pkuser = ?";

        if (!hasField) {
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            if (this.nome != null) {
                ps.setString(index++, this.nome);
            }
            if (this.email != null) {
                ps.setString(index++, this.email);
            }
            if (this.cpf != null) {
                ps.setString(index++, this.cpf);
            }
            if (this.dataNascimento != null) {
                ps.setDate(index++, this.dataNascimento);
            }
            if (this.genero != null) {
                ps.setString(index++, this.genero);
            }
            if (this.senhaHash != null) {
                ps.setString(index++, this.senhaHash);
            }

            ps.setInt(index, this.pkuser);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            this.statusSQL = "Erro ao Alterar usuario ! <br> " + ex.getMessage();
        }
        return false;
    }

    public void deletar() {
        try {
            sql = "delete from usuario where ucase(trim(pkuser)) = ucase(trim(?))";
            ps = con.prepareStatement(sql);
            ps.setInt(1, this.pkuser);
            ps.executeUpdate();
            this.statusSQL = null;
        } catch (SQLException ex) {
            this.statusSQL = "Erro ao Alterar usuario ! <br> "
                    + ex.getMessage();
        }
    }

}
