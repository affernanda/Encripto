package JavaBeans;

import java.sql.*;
import java.io.*;

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
            statusSQL = "Servidor fora do ar ou comando SQL! " + ex.getMessage();
        }
    }

    public void criarBanco() {
        try {
            sql = "CREATE DATABASE IF NOT EXISTS " + this.MeuBanco;
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "USE " + this.MeuBanco;
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            // Criação das tabelas
            sql = "CREATE TABLE IF NOT EXISTS usuario ("
                    + "pkuser INT NOT NULL AUTO_INCREMENT, "
                    + "nome VARCHAR(250) NOT NULL, "
                    + "email VARCHAR(40) NOT NULL, "
                    + "cpf VARCHAR(11) NOT NULL, "
                    + "data_nascimento DATE NOT NULL, "
                    + "genero VARCHAR(15) NOT NULL, "
                    + "senha_hash VARCHAR(255) NOT NULL, "
                    + "cargo VARCHAR(255) NOT NULL, "
                    + "PRIMARY KEY (pkuser))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS enderecos ("
                    + "pkenderecos INT NOT NULL AUTO_INCREMENT, "
                    + "pkuser INT NOT NULL, "
                    + "cep VARCHAR(9) NOT NULL, "
                    + "logradouro VARCHAR(255) NOT NULL, "
                    + "numero VARCHAR(8) NOT NULL, "
                    + "complemento VARCHAR(200), "
                    + "bairro VARCHAR(255) NOT NULL, "
                    + "cidade VARCHAR(255) NOT NULL, "
                    + "uf VARCHAR(2) NOT NULL, "
                    + "padrao BOOLEAN DEFAULT FALSE, "
                    + "PRIMARY KEY (pkenderecos), "
                    + "CONSTRAINT fk_enderecos_user FOREIGN KEY (pkuser) REFERENCES usuario(pkuser))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS produto ("
                    + "pkproduto INT NOT NULL AUTO_INCREMENT, "
                    + "nome VARCHAR(255) NOT NULL, "
                    + "descricao VARCHAR(255) NOT NULL, "
                    + "avaliacao VARCHAR(1) DEFAULT '5' NOT NULL, "
                    + "quantidade INT(8) DEFAULT '10' NOT NULL, "
                    + "valor DECIMAL(10,2) NOT NULL, "
                    + "imagem VARCHAR(255) NOT NULL, "
                    + "PRIMARY KEY (pkproduto))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS pedido ("
                    + "pkpedido INT NOT NULL AUTO_INCREMENT, "
                    + "pkuser INT NOT NULL, "
                    + "status VARCHAR(50) NOT NULL DEFAULT 'Aguardando Pagamento', "
                    + "total DECIMAL(10,2) NOT NULL, "
                    + "data_pedido DATETIME NOT NULL, "
                    + "pagamento VARCHAR(255) NOT NULL, "
                    + "endereco VARCHAR(255) NOT NULL, "
                    + "frete VARCHAR(255) NOT NULL, "
                    + "PRIMARY KEY (pkpedido), "
                    + "CONSTRAINT fk_pedidos_user FOREIGN KEY (pkuser) REFERENCES usuario(pkuser))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
            
            sql = "CREATE TABLE IF NOT EXISTS pedido_itens ("
                    + "pkitem INT NOT NULL AUTO_INCREMENT, "
                    + "pkpedido INT NOT NULL, "
                    + "pkproduto INT NOT NULL, "
                    + "quantidade INT NOT NULL, "
                    + "valor_unitario DOUBLE NOT NULL, "
                    + "subtotal DECIMAL(10,2) NOT NULL, "
                    + "PRIMARY KEY (pkitem), "
                    + "CONSTRAINT fk_itens_pedido FOREIGN KEY (pkpedido) REFERENCES pedido(pkpedido), "
                    + "CONSTRAINT fk_itens_produto FOREIGN KEY (pkproduto) REFERENCES produto(pkproduto))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS carrinho ("
                    + "pkcar INT NOT NULL AUTO_INCREMENT, "
                    + "pkuser INT NOT NULL, "
                    + "pkproduto INT NOT NULL, "
                    + "total DECIMAL(10,2) NOT NULL, "
                    + "quantidade INT(8) NOT NULL, "
                    + "PRIMARY KEY (pkcar), "
                    + "CONSTRAINT fk_car_user FOREIGN KEY (pkuser) REFERENCES usuario(pkuser), "
                    + "CONSTRAINT fk_car_produto FOREIGN KEY (pkproduto) REFERENCES produto(pkproduto))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
            
            sql = "CREATE TABLE IF NOT EXISTS carrinhoTemporario ("
                    + "pkcartemp INT NOT NULL AUTO_INCREMENT, "
                    + "session_id INT NOT NULL, "
                    + "pkproduto INT NOT NULL, "
                    + "total DECIMAL(10,2) NOT NULL, "
                    + "quantidade INT(8) NOT NULL, "
                    + "PRIMARY KEY (pkcartemp), "
                    + "CONSTRAINT fk_car_temp_produto FOREIGN KEY (pkproduto) REFERENCES produto(pkproduto))";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            // Só insere imagens se a tabela estiver vazia
            sql = "SELECT COUNT(*) AS total FROM produto";
            ps = con.prepareStatement(sql);
            tab = ps.executeQuery();
            if (tab.next() && tab.getInt("total") == 0) {
                importarImagensComoProdutos("C:\\Users\\ferna\\Downloads\\Encripto\\Encripto\\web\\imgs");
            }

        } catch (SQLException err) {
            statusSQL = "Erro ao executar SQL: " + err.getMessage();
        }
    }

    public void importarImagensComoProdutos(String pastaImagens) {
        try {
            File dir = new File(pastaImagens);
            File[] arquivos = dir.listFiles((d, nome) -> nome.endsWith(".jpg") || nome.endsWith(".png"));

            if (arquivos != null) {
                for (File img : arquivos) {
                    String nomeProduto = img.getName().replace(".jpg", "").replace(".png", "");

                    sql = "INSERT INTO produto (nome, descricao, valor, imagem) VALUES (?, ?, ?, ?)";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, nomeProduto);
                    ps.setString(2, "Descrição automática do produto " + nomeProduto);
                    ps.setDouble(3, 10.00);
                    ps.setString(4, "imgs/" + img.getName());
                    ps.executeUpdate();
                }
            }

            // === Atualiza preços com valores aleatórios entre 5 e 200 ===
            sql = "UPDATE produto SET valor = ROUND(5 + (RAND() * (200 - 5)), 2) WHERE pkproduto IS NOT NULL";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();

            // === Atualiza descrições personalizadas ===
            String[] updates = {
                "UPDATE produto SET descricao = 'O clássico Bulbasaur, símbolo de equilíbrio entre força e natureza. Seu design verde e detalhes folhosos remetem à energia dos tipos Planta e Venenoso. Ideal para quem aprecia o início de toda jornada.' WHERE pkproduto = 1",
                "UPDATE produto SET descricao = 'Clawitzer, o crustáceo de alta potência. Traz um toque marítimo e tecnológico, simbolizando precisão e energia com sua poderosa garra-canhão.' WHERE pkproduto = 2",
                "UPDATE produto SET descricao = 'Edição Delibird, o mensageiro das neves! Com estética natalina e design leve, representa generosidade, alegria e espírito aventureiro.' WHERE pkproduto = 3",
                "UPDATE produto SET descricao = 'Exeggcutor, o “árvore-dragão tropical”. Um produto vibrante e imponente, que transmite energia solar e vitalidade. Perfeito para ambientes descontraídos e criativos.' WHERE pkproduto = 4",
                "UPDATE produto SET descricao = 'Criaturas rochosas de aparência poderosa. Seu design transmite firmeza e resistência, ideal para quem busca força e presença marcante.' WHERE pkproduto = 5",
                "UPDATE produto SET descricao = 'Gumshoos, o detetive natural! Seu visual elegante e determinado traduz inteligência e observação — ideal para quem gosta de estilo investigativo.' WHERE pkproduto = 6",
                "UPDATE produto SET descricao = 'Haundstone, o leal cão fantasma. Representa coragem, proteção e vínculo inquebrável. Ótimo para colecionadores que valorizam lealdade.' WHERE pkproduto = 7",
                "UPDATE produto SET descricao = 'O elétrico Helioptile! Design leve e moderno, remetendo à energia solar e inovação. Um toque de tecnologia para qualquer coleção.' WHERE pkproduto = 8",
                "UPDATE produto SET descricao = 'Edição Inteleon: símbolo de precisão e elegância. Inspirado em um espião aquático, combina estética refinada com estilo tático.' WHERE pkproduto = 9",
                "UPDATE produto SET descricao = 'Evolução natural do Bulbasaur, o Ivysaur representa crescimento e poder em equilíbrio. Ideal para quem valoriza progresso e evolução constante.' WHERE pkproduto = 10",
                "UPDATE produto SET descricao = 'Litleo, o pequeno leão flamejante. Transmite bravura e entusiasmo, com visual vibrante e cheio de energia.' WHERE pkproduto = 11",
                "UPDATE produto SET descricao = 'Marshadow, o espírito das sombras. Um produto envolto em aura mística, para fãs de raridades e mistério.' WHERE pkproduto = 12",
                "UPDATE produto SET descricao = 'Mega Abomasnow, símbolo de força glacial e imponência. Um item que reflete poder e resistência em meio à calmaria do gelo.' WHERE pkproduto = 13",
                "UPDATE produto SET descricao = 'Absol EX representa a elegância sombria. Esta edição traz aura misteriosa e detalhes prateados, perfeitos para quem aprecia design refinado.' WHERE pkproduto = 14",
                "UPDATE produto SET descricao = 'Mega Camerupt, o vulcão vivo. Seu visual ígneo e robusto simboliza energia e estabilidade.' WHERE pkproduto = 15",
                "UPDATE produto SET descricao = 'Mega Gardevoir, com acabamento dourado. Representa elegância, empatia e brilho interior.' WHERE pkproduto = 16",
                "UPDATE produto SET descricao = 'Mega Gardevoir, o símbolo de graça e poder psíquico. Um produto que une delicadeza e força interior.' WHERE pkproduto = 17",
                "UPDATE produto SET descricao = 'Gardevoir em sua forma clássica. Seu design transmite pureza e lealdade, ideal para quem busca harmonia.' WHERE pkproduto = 18",
                "UPDATE produto SET descricao = 'Mega Latias, símbolo de leveza e confiança. Um produto com linhas aerodinâmicas e toque espiritual.' WHERE pkproduto = 19",
                "UPDATE produto SET descricao = 'Edição especial dourada de Mega Lucario, representando aura e força interior. Perfeito para quem aprecia exclusividade e estilo.' WHERE pkproduto = 20",
                "UPDATE produto SET descricao = 'Mega Mawile, um produto que combina charme e poder oculto. Seus detalhes metálicos transmitem dualidade e mistério.' WHERE pkproduto = 21",
                "UPDATE produto SET descricao = 'Mega Venusaur — poder e natureza em equilíbrio. Representa crescimento, defesa e energia vital.' WHERE pkproduto = 22",
                "UPDATE produto SET descricao = 'Ninjask, símbolo de agilidade extrema. Um design leve e veloz, perfeito para quem valoriza dinamismo.' WHERE pkproduto = 23",
                "UPDATE produto SET descricao = 'Shuckle, o protetor das frutas fermentadas! Representa paciência, resistência e sabedoria. Ideal para quem aprecia estabilidade.' WHERE pkproduto = 24",
                "UPDATE produto SET descricao = 'Edição Shedinja, misteriosa e espiritual. Representa renascimento e mistério, sendo um item único para colecionadores intrigados pelo sobrenatural.' WHERE pkproduto = 25",
                "UPDATE produto SET descricao = 'Snover, o guardião das montanhas geladas. Une elementos de neve e floresta, transmitindo equilíbrio entre frio e vida natural. Ideal para quem aprecia o contraste entre força e serenidade.' WHERE pkproduto = 27;",
                "UPDATE produto SET descricao = 'Spearow, o pequeno pássaro destemido. Seu design leve e agressivo simboliza coragem e velocidade — perfeito para quem valoriza atitude e liberdade.' WHERE pkproduto = 28;",
                "UPDATE produto SET descricao = 'Spiritomb, o espírito aprisionado em 108 almas. Um produto enigmático que transmite misticismo e poder oculto, ideal para colecionadores que buscam o raro e o misterioso.' WHERE pkproduto = 29;",
                "UPDATE produto SET descricao = 'Steelix, o titã subterrâneo de aço. Este produto traz resistência e robustez em cada detalhe, simbolizando durabilidade e força inabalável.' WHERE pkproduto = 30;",
                "UPDATE produto SET descricao = 'Stufful, o urso de pelúcia mais adorável do universo Pokémon. Representa fofura, doçura e inocência — perfeito para quem busca aconchego e charme.' WHERE pkproduto = 31;",
                "UPDATE produto SET descricao = 'Vulpix, a raposa de seis caudas. Seu design elegante e caloroso reflete beleza, lealdade e o fogo da amizade. Um clássico atemporal.' WHERE pkproduto = 32;"
            };

            for (String updateSQL : updates) {
                ps = con.prepareStatement(updateSQL);
                ps.executeUpdate();
            }

            statusSQL = "Importação e atualizações concluídas com sucesso!";

        } catch (Exception e) {
            e.printStackTrace();
            statusSQL = "Erro ao importar imagens: " + e.getMessage();
        }
    }

}
