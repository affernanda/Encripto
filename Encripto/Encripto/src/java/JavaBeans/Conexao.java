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

            sql = "create table if not exists usuarios ( "
                    + "pkuser int not null auto_increment,"
                    + "email varchar(40) not null,"
                    + "senha varchar(20) not null,"
                    + "nome varchar(40) not null,"
                    + "idade varchar(5) not null,"
                    + "PRIMARY KEY (pkuser))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
            
            sql = "create table if not exists produtos ("
                    + "pkproduct int not null auto_increment, "
                    + "nome varchar(40) not null, "
                    + "descricao varchar(400) not null, "
                    + "preco double not null, "
                    + "avaliacao int not null, "
                    + "PRIMARY KEY (pkproduct))";
            
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
            
            sql = "create table if not exists carrinho ("
                    + "pkcart int not null auto_increment, "
                    + "pkuser int not null, "
                    + "pkproduct int not null, "
                    + "quantidade int(10) not null, "
                    + "total double not null, "
                    + "PRIMARY KEY (pkcart), "
                    + "CONSTRAINT fk_cart_user FOREIGN KEY (pkuser) REFERENCES usuarios(pkuser), "
                    + "CONSTRAINT fk_cart_product FOREIGN KEY (pkproduct) REFERENCES produtos(pkproduct))";
            
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
                    
        } catch (SQLException err) {
            statusSQL = "Erro ao executar SQL " + err.getMessage();
        }
    }
}
