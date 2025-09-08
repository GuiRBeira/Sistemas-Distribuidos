// Alunos: Cristian Andre Sanches RA: 1940724
//        Guilherme Ricardo Beira RA: 2270080

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class Principal_v0 {

    private static final String FORTUNE_FILE = "src/fortune-br.txt";

    /**
     * Implementa a leitura de 1 (uma) fortuna aleatória no arquivo.
     * @return Uma String contendo uma fortuna.
     * @throws IOException se ocorrer um erro durante a leitura do arquivo.
     */
    public String read() throws IOException {
        List<String> fortunes = new ArrayList<>();
        StringBuilder currentFortune = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(FORTUNE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Checa se a linha contém apenas o separador "%" (com ou sem espaços)
                if (line.trim().equals("%")) {
                    // Se for o separador, adiciona a fortuna acumulada na lista
                    if (currentFortune.length() > 0) {
                        fortunes.add(currentFortune.toString().trim());
                        currentFortune.setLength(0); // Reinicia o StringBuilder
                    }
                } else {
                    // Se não for o separador, adiciona a linha à fortuna atual
                    currentFortune.append(line).append("\n");
                }
            }
            // Adiciona a última fortuna, caso o arquivo não termine com o separador
            if (currentFortune.length() > 0) {
                fortunes.add(currentFortune.toString().trim());
            }
        }
        // Se nenhuma fortuna foi encontrada, lança uma exceção
        if (fortunes.isEmpty()) {
            throw new IOException("Nenhuma fortuna encontrada no arquivo.");
        }
        // Seleciona e retorna uma fortuna aleatória da lista
        Random random = new Random();
        int randomIndex = random.nextInt(fortunes.size());
        return fortunes.get(randomIndex);
    }
    /*
     * Implementa a escrita de uma nova fortuna no arquivo, adicionando ao final.
     * @param newFortune A string contendo a nova fortuna.
     * @throws IOException se ocorrer um erro durante a escrita no arquivo.
     */
    public void write(String newFortune) throws IOException {
        // OBTER o caminho para o arquivo de fortunas.
        Path filePath = Paths.get(FORTUNE_FILE);

        // Formatar a nova linha de fortuna pra ficar parecido com a estrutura do arquivo original.
        // O padrão é uma nova linha seguida de "%"
        String formattedFortune = "\n" + newFortune + "\n" + "%";

        // Escrever a nova fortuna no final do arquivo.
        Files.write(filePath, formattedFortune.getBytes(), StandardOpenOption.APPEND);
    }
    
    // O método main() e outras lógicas da classe Principal_v0 podem ser
    // mantidas conforme o esqueleto original do laboratório.
    public static void main(String[] args) {
        Principal_v0 app = new Principal_v0();
        try {
            String fortune = app.read();
            System.out.println("Fortuna aleatória lida: " + fortune);
            String novaFortuna = "A única coisa que devemos temer é o próprio medo.";
            app.write(novaFortuna);
            System.out.println("Nova fortuna escrita com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
