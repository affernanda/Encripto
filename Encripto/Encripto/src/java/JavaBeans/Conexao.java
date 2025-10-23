package JavaBeans;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Conexao {

    public Connection con;
    public String sql;
    public PreparedStatement ps;
    public ResultSet tab;
    public String MeuBanco = "encripto";
    public String servidor = "jdbc:mysql://localhost:3306";
    public String usuario = "root";
    public String senha = "";
    public String statusSQL;

    public Conexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(servidor, usuario, senha);
            this.criarBanco();
            statusSQL = null;
        } catch (ClassNotFoundException ex) {
            statusSQL = "Driver JDBC não encontrado! " + ex.getMessage();
        } catch (SQLException ex) {
            statusSQL = "Servidor fora do ar ou comando SQL ! " + ex.getMessage();
        }
    }

    public void criarBanco() {
        try {
            sql = "create database if not exists " + this.MeuBanco;
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "use " + this.MeuBanco;
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "create table if not exists usuario ( "
                    + "pkuser int not null auto_increment, "
                    + "nome varchar(250) not null, "
                    + "email varchar(40) not null, "
                    + "cpf varchar(11) not null, "
                    + "data_nascimento date not null, "
                    + "genero varchar(15) not null, "
                    + "senha_hash varchar(255) not null, "
                    + "PRIMARY KEY (pkuser))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "create table if not exists enderecos ("
                    + "pkenderecos int not null auto_increment, "
                    + "pkuser int not null, "
                    + "cep varchar(9) not null, "
                    + "logradouro varchar(255) not null, "
                    + "numero varchar(8) not null, "
                    + "complemento varchar(200), "
                    + "bairro varchar(255) not null, "
                    + "cidade varchar(255) not null, "
                    + "uf varchar(2) not null, "
                    + "padrao BOOLEAN DEFAULT FALSE, "
                    + "PRIMARY KEY (pkenderecos), "
                    + "CONSTRAINT fk_enderecos_user FOREIGN KEY (pkuser) REFERENCES usuario(pkuser))";

            ps = con.prepareStatement(sql);
            ps.executeUpdate();

        } catch (SQLException err) {
            statusSQL = "Erro ao executar SQL " + err.getMessage();
        }
    }
}
