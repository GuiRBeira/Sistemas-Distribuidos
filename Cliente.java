import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private String host = "localhost"; // ou IP do servidor
    private int porta = 1025;

    public void iniciar() {
        try (Socket socket = new Socket(host, porta);
             DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado ao servidor em " + host + ":" + porta);
            System.out.println("Digite mensagens para enviar (digite 'sair' para encerrar):");

            String mensagem, resposta;

            while (true) {
                // Lê mensagem do teclado
                System.out.print("Você: ");
                mensagem = scanner.nextLine();

                // Envia para o servidor
                saida.writeUTF(mensagem);

                // Se for "sair", encerra
                if (mensagem.equalsIgnoreCase("sair")) {
                    resposta = entrada.readUTF();
                    System.out.println("Servidor: " + resposta);
                    break;
                }

                // Recebe resposta do servidor
                resposta = entrada.readUTF();
                System.out.println("Servidor: " + resposta);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Cliente().iniciar();
    }
}
