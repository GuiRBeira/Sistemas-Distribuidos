// Alunos: Cristian Andre Sanches RA: 1940724
//        Guilherme Ricardo Beira RA: 2270080

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Servidor {
    private int porta = 1025;
    private static List<String> fortunes = new ArrayList<>();
    private static Random random = new Random();

    public void iniciar() {
        carregarFortunes("fortune-br.txt");

        System.out.println("Servidor iniciado na porta: " + porta);
        try (ServerSocket server = new ServerSocket(porta)) {
            while (true) {
                Socket socket = server.accept();
                System.out.println("Novo cliente conectado: " + socket.getInetAddress());
                new Thread(new ClienteHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lê arquivo fortune-br.txt e divide pelo delimitador "%"
    private void carregarFortunes(String caminho) {
        try {
            String conteudo = new String(Files.readAllBytes(Paths.get(caminho)));
            String[] partes = conteudo.split("(?m)^%$");
            for (String frase : partes) {
                frase = frase.trim();
                if (!frase.isEmpty()) {
                    fortunes.add(frase);
                }
            }
            System.out.println("Carregadas " + fortunes.size() + " fortunes.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar fortune-br.txt: " + e.getMessage());
        }
    }

    // Salvar nova fortune no arquivo
    private static synchronized void salvarFortune(String texto, String caminho) {
        try (FileWriter writer = new FileWriter(caminho, true)) {
            writer.write("\n" + texto.trim() + "\n%\n");
            fortunes.add(texto.trim());
            System.out.println("Nova fortune adicionada.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar fortune: " + e.getMessage());
        }
    }

    private static class ClienteHandler implements Runnable {
        private Socket socket;
        private DataInputStream entrada;
        private DataOutputStream saida;
        private boolean aguardandoFortune = false; // estado de espera para "write"

        public ClienteHandler(Socket socket) {
            this.socket = socket;
            try {
                entrada = new DataInputStream(socket.getInputStream());
                saida = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String mensagem;
                while (true) {
                    mensagem = entrada.readUTF();
                    System.out.println("Cliente disse: " + mensagem);

                    if (aguardandoFortune) {
                        salvarFortune(mensagem, "fortune-br.txt");
                        saida.writeUTF("Sua frase foi salva no banco de fortunes!");
                        aguardandoFortune = false;
                    } else if (mensagem.equalsIgnoreCase("sair")) {
                        saida.writeUTF("Conexão encerrada. Tchau!");
                        break;
                    } else if (mensagem.equalsIgnoreCase("read")) {
                        if (!fortunes.isEmpty()) {
                            String frase = fortunes.get(random.nextInt(fortunes.size()));
                            saida.writeUTF("Fortune:\n" + frase);
                        } else {
                            saida.writeUTF("Nenhuma fortune carregada.");
                        }
                    } else if (mensagem.equalsIgnoreCase("write")) {
                        saida.writeUTF("Digite o texto da nova fortune:");
                        aguardandoFortune = true;
                    } else {
                        saida.writeUTF("Servidor recebeu: " + mensagem);
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
