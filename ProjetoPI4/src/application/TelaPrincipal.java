package application;

import JavaPackage.Imagem;
import JavaPackage.Produto;
import JavaPackage.Usuario;
import java.util.List;
import java.util.Scanner;

public class TelaPrincipal {

    public static final Scanner sc = new Scanner(System.in);
    public static Usuario sessao;

    public static void main(String[] args) {
        System.out.println("=== Sistema de Colecionaveis ===");

        while (true) {
            System.out.print("Email: ");
            String email = leLinha();
            System.out.print("Senha: ");
            String senha = leLinha();

            Usuario model = new Usuario();
            sessao = model.autenticar(email, senha);

            if (sessao == null) {
                System.out.println("Credenciais invalidas ou usuario inativo. Acesso negado.");
            } else {
                System.out.println("Bem-vindo, " + sessao.nome + " [" + sessao.grupo + "]");
                menuPrincipal();
                break;
            }
        }

        System.out.println("Ate logo.");
    }

    private static String leLinha() {
        String s = sc.nextLine();
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    private static void menuPrincipal() {
        Usuario model = new Usuario();
        while (true) {
            System.out.println();
            System.out.println("Menu Principal");

            // Mudança: adicionado botão Listar Produtos para ADMIN e ESTOQUISTA
            if ("ADMIN".equals(sessao.grupo)) {
                System.out.println("1) Listagem de produtos"); // mudou de pedidos para produtos
                System.out.println("2) Listagem de usuarios");
                System.out.println("0) Sair");
            } else if ("ESTOQUISTA".equals(sessao.grupo)) {
                System.out.println("1) Listagem de produtos"); // Estoquista só vê produtos
                System.out.println("0) Sair");
            }

            System.out.print("Escolha: ");
            String op = leLinha();

            switch (op) {
                case "1":
                    menuProdutos(); // novo método para listar, incluir, editar produtos
                    break;
                case "2":
                    if (!"ADMIN".equals(sessao.grupo)) {
                        System.out.println("Opcao invalida.");
                    } else {
                        menuUsuarios();
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void menuUsuarios() {
        Usuario model = new Usuario();
        while (true) {
            System.out.println();
            System.out.println("Usuarios cadastrados:");
            List<Usuario> lista = model.listarUsuarios();
            model.imprimirUsuarios(lista);

            System.out.println();
            System.out.println("1) Incluir");
            System.out.println("2) Alterar Informacoes de Usuario Selecionando ID");
            System.out.println("3) Tornar Ativo/Inativo");
            System.out.println("0) Voltar ao menu principal");
            System.out.print("Escolha: ");
            String op = leLinha();

            switch (op) {
                case "1":
                    model.incluirUsuario(sc);
                    break;
                case "2":
                    model.atualizarDados(sc);
                    break;
                case "3":
                    model.alternarStatus(sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    // ================= Menu Produtos =================
    private static void menuProdutos() {
        Produto prod = new Produto();
        while (true) {
            System.out.println();
            System.out.println("Produtos cadastrados (decrescente):");
            List<Produto> lista = prod.listarProdutos(); // listar produtos do banco
            prod.imprimirProdutos(lista);

            if ("ADMIN".equals(sessao.grupo)) {
                System.out.println();
                System.out.println("Escolha uma opcao:");
                System.out.println("i) Incluir novo produto");
                System.out.println("ID do produto) Editar / Alterar Imagens / Ativar-Inativar");
                System.out.println("0) Voltar ao menu principal");
            } else if ("ESTOQUISTA".equals(sessao.grupo)) {
                System.out.println();
                System.out.println("Escolha:");
                System.out.println("Digite o ID do produto desejado para alterar qtd.");
                System.out.println("ou");
                System.out.println("0) Voltar ao menu principal");

            }
            System.out.print("");
            String op = leLinha();

            if (op.equalsIgnoreCase("i")) {
                if (!"ADMIN".equals(sessao.grupo)) {
                    System.out.println("Opcao invalida.");
                } else {
                    prod.incluirProduto(sc);
                }
            } else if (op.equals("0")) {
                return;
            } else {
                try {
                    int idProduto = Integer.parseInt(op);
                    if ("ADMIN".equals(sessao.grupo)) {
                        prod.editarProduto(sc, idProduto);
                    } else if ("ESTOQUISTA".equals(sessao.grupo)) {
                        prod.editarQuantidadeProduto(sc, idProduto);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Opcao invalida.");
                }
            }
        }
    }

    public static void menuImagem() {
        Imagem img = new Imagem();
        while (true) {
            System.out.println();
            System.out.println("Imagens cadastradas:");
            List<Imagem> lista2 = img.listarImagens();
            img.imprimirImagem(lista2);
            System.out.println();
            System.out.println("Escolha:");
            System.out.println("ID da imagem - Editar ");
            System.out.println("0 - Voltar ao menu principal ");
            String op = leLinha();

            if (op.equals("0")) {
                return;
            } else {
                try {
                    int pkimagem = Integer.parseInt(op);
                    img.editarImagemProduto(sc, pkimagem);
                } catch (NumberFormatException e) {
                    System.out.println("Opcao invalida.");
                }
            }
        }
    }
}
