package Teste;

public class GerarHash {
    public static void main(String[] args) {
        String senha = "123456"; // sua senha
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            System.out.println("Hash SHA-256: " + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}