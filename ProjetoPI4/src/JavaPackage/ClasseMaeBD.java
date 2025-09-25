package JavaPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClasseMaeBD {
    public Connection con;
    public String sql;
    public PreparedStatement ps;
    public ResultSet tab;
    public String servidor = "jdbc:mysql://localhost:3306";
    public String usuario = "root";
    public String senha = "";
    public String MeuBanco = "usuarios";
    public String statusSQL = "";

    public void conectar() {
        try {
            if (con != null && !con.isClosed()) {
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(servidor, usuario, senha);
            con.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + MeuBanco + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            con.close();
            con = DriverManager.getConnection(servidor + "/" + MeuBanco + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", usuario, senha);
            con.setAutoCommit(true);
            criarTabelas();
        } catch (Exception e) {
            statusSQL = "Erro ao conectar: " + e.getMessage();
            throw new RuntimeException(statusSQL, e);
        }
    }

    private void criarTabelas() {
        try {
            // Tabela usuario
            sql = "CREATE TABLE IF NOT EXISTS usuario (" +
                    " pkuser INT NOT NULL AUTO_INCREMENT," +
                    " nome VARCHAR(100) NOT NULL," +
                    " email VARCHAR(120) NOT NULL UNIQUE," +
                    " cpf BIGINT(11) NOT NULL UNIQUE," +
                    " senha_hash VARCHAR(255) NOT NULL," +
                    " grupo ENUM('ADMIN','ESTOQUISTA') NOT NULL," +
                    " status TINYINT(1) NOT NULL DEFAULT 1," +
                    " PRIMARY KEY (pkuser)" +
                    ") ENGINE=InnoDB;";
            con.createStatement().executeUpdate(sql);

            // Tabela produto
            sql = "CREATE TABLE IF NOT EXISTS produto (" +
                    " pkproduto INT NOT NULL AUTO_INCREMENT," +
                    " nome VARCHAR(200) NOT NULL," +
                    " descricao VARCHAR(2000) NOT NULL," +
                    " avaliacao DECIMAL(2,1) DEFAULT 0," +
                    " quantidade INT NOT NULL," +
                    " valor DECIMAL(10,2) NOT NULL," +
                    " status TINYINT(1) NOT NULL DEFAULT 1," +
                    " PRIMARY KEY (pkproduto)" +
                    ") ENGINE=InnoDB;";
            con.createStatement().executeUpdate(sql);

            // Tabela imagem_produto
            sql = "CREATE TABLE IF NOT EXISTS imagem_produto (" +
                    " pkimagem INT NOT NULL AUTO_INCREMENT," +
                    " produto_id INT NOT NULL," +
                    " nome_arquivo VARCHAR(255) NOT NULL," +
                    " diretorio VARCHAR(500) NOT NULL," +
                    " principal TINYINT(1) NOT NULL DEFAULT 0," +
                    " PRIMARY KEY (pkimagem)," +
                    " FOREIGN KEY (produto_id) REFERENCES produto(pkproduto) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;";
            con.createStatement().executeUpdate(sql);

        } catch (SQLException err) {
            statusSQL = "Erro ao criar tabelas: " + err.getMessage();
            throw new RuntimeException(statusSQL, err);
        }
    }

    public void beginTx() {
        try {
            if (con.getAutoCommit()) {
                con.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commitTx() {
        try {
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollbackTx() {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
