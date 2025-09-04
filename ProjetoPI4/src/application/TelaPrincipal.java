package application;

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
        if (s == null) return "";
        return s.trim();
    }

    private static void menuPrincipal() {
        Usuario model = new Usuario();
        while (true) {
            System.out.println();
            System.out.println("Menu Principal");
            System.out.println("1) Listagem de produtos (todos)");
            System.out.println("2) Listagem de usuarios (somente ADMIN)");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = leLinha();

            switch (op) {
                case "1":
                    model.listarProdutos();
                    break;
                case "2":
                    if (!"ADMIN".equals(sessao.grupo)) {
                        System.out.println("Acesso restrito a administradores.");
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
}
