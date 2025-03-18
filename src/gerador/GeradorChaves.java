package gerador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class GeradorChaves {

    public static Set<String> gerarListaChaves(int quantidade) {
        Set<String> chaves = new HashSet<>();
        Random random = new Random();

        while (chaves.size() < quantidade) {
            int chave = 100_000_000 + random.nextInt(900_000_000);
            chaves.add(String.valueOf(chave));
        }

        return chaves;
    }

    public static void salvarEmArquivo(Set<String> chaves, String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (String chave : chaves) {
                writer.write(chave);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite a quantidade de chaves a serem geradas: ");
        int quantidade = scanner.nextInt();
        scanner.close();

        Set<String> listaChaves = gerarListaChaves(quantidade);
        String nomeArquivo = "chaves" + quantidade + ".txt";
        salvarEmArquivo(listaChaves, nomeArquivo);

        System.out.println(quantidade + " chaves geradas e salvas em '" + nomeArquivo + "'.");
    }
}
