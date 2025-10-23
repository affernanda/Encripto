package JavaBeans;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Endereco extends Conexao {

    public Integer pkenderecos;
    public Integer pkuser;
    public String cep;
    public String logradouro;
    public String numero;
    public String complemento;
    public String bairro;
    public String cidade;
    public String uf;
    public boolean padrao; // Indica se é o endereço padrão do usuário

    public Endereco() {
        super();
    }

    // Incluir novo endereço
    public void incluirEndereco() {
        try {
            // Se o endereço for padrão, zera todos os outros endereços do usuário
            if (padrao) {
                sql = "UPDATE enderecos SET padrao = FALSE WHERE pkuser = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, pkuser);
                ps.executeUpdate();
            }

            sql = "INSERT INTO enderecos (pkuser, cep, logradouro, numero, complemento, bairro, cidade, uf, padrao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pkuser);
            ps.setString(2, cep);
            ps.setString(3, logradouro);
            ps.setString(4, numero);
            ps.setString(5, complemento);
            ps.setString(6, bairro);
            ps.setString(7, cidade);
            ps.setString(8, uf);
            ps.setBoolean(9, padrao);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.pkenderecos = generatedKeys.getInt(1);
                }
            } else {
                statusSQL = "Nenhum endereço foi inserido.";
            }
        } catch (SQLException e) {
            e.printStackTrace();  // <-- imprime o erro completo no console do servidor
            statusSQL = "Erro ao inserir endereço: " + e.getMessage();
        }
    }

    // Atualizar endereço existente
    public boolean atualizarEndereco() {
        try {
            // Se marcar como padrão, zera todos os outros endereços do usuário
            if (padrao) {
                sql = "UPDATE enderecos SET padrao = FALSE WHERE pkuser = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, pkuser);
                ps.executeUpdate();
            }

            sql = "UPDATE enderecos SET cep = ?, logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, uf = ?, padrao = ? WHERE pkenderecos = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, cep);
            ps.setString(2, logradouro);
            ps.setString(3, numero);
            ps.setString(4, complemento);
            ps.setString(5, bairro);
            ps.setString(6, cidade);
            ps.setString(7, uf);
            ps.setBoolean(8, padrao);
            ps.setInt(9, pkenderecos);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            statusSQL = "Erro ao atualizar endereço: " + e.getMessage();
            return false;
        }
    }

    // Deletar endereço
    public void deletarEndereco() {
        try {
            sql = "DELETE FROM enderecos WHERE pkenderecos = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, pkenderecos);
            ps.executeUpdate();
            statusSQL = null;
        } catch (SQLException e) {
            statusSQL = "Erro ao deletar endereço: " + e.getMessage();
        }
    }

    // Listar todos os endereços de um usuário
    public List<Endereco> listarEnderecos(int pkuser) {
        List<Endereco> lista = new ArrayList<>();
        try {
            sql = "SELECT * FROM enderecos WHERE pkuser = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, pkuser);
            tab = ps.executeQuery();
            while (tab.next()) {
                Endereco e = new Endereco();
                e.pkenderecos = tab.getInt("pkenderecos");
                e.pkuser = tab.getInt("pkuser");
                e.cep = tab.getString("cep");
                e.logradouro = tab.getString("logradouro");
                e.numero = tab.getString("numero");
                e.complemento = tab.getString("complemento");
                e.bairro = tab.getString("bairro");
                e.cidade = tab.getString("cidade");
                e.uf = tab.getString("uf");
                e.padrao = tab.getBoolean("padrao");
                lista.add(e);
            }
        } catch (SQLException e) {
            statusSQL = "Erro ao listar endereços: " + e.getMessage();
        }
        return lista;
    }
}
