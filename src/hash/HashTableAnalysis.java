package hash;

import entities.AnaliseDigitos;
import entities.Digitos;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static utils.ContadorUtils.*;

public class HashTableAnalysis {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Chaves que deseja ler: ");
        String chaves = sc.nextLine().trim();
        String file = "D:\\uft\\hashtable\\chaves" + chaves + ".txt";
        int tabelaHashSize = Integer.parseInt(chaves);

        if(tabelaHashSize < 10 )
            tabelaHashSize = 10;

        int numDigitosNecessarios;
        if (tabelaHashSize == 10) {
            numDigitosNecessarios = 1;
        } else if (tabelaHashSize <= 100) {
            numDigitosNecessarios = 2;
        } else if (tabelaHashSize <= 1000) {
            numDigitosNecessarios = 3;
        } else if (tabelaHashSize <= 10000) {
            numDigitosNecessarios = 4;
        } else if (tabelaHashSize <= 100000) {
            numDigitosNecessarios = 5;
        } else {
            numDigitosNecessarios = 6;  // Para casos maiores
        }


        String[] tabelaHash = new String[tabelaHashSize];
        for (int i = 0; i < tabelaHash.length; i++) {
            tabelaHash[i] = null;
        }

        System.out.println("Número de dígitos a serem usados: " + numDigitosNecessarios);
        System.out.println("Tamanho da tabela hash: " + tabelaHashSize);

        List<String> chaves2 = lerChavesDoArquivo(file);
        int[][] matriz = new int[chaves2.size()][chaves2.get(0).length()];
        for (int i = 0; i < chaves2.size(); i++) {
            for (int j = 0; j < chaves2.get(i).length(); j++) {
                matriz[i][j] = Character.getNumericValue(chaves2.get(i).charAt(j));
            }
        }

        var resultado = contarPorColuna(matriz);

        List<Integer> melhoresColunas = encontrarMelhoresColunas(resultado, numDigitosNecessarios);
        melhoresColunas = melhoresColunas.stream()
                .sorted(Comparator.comparingDouble(c -> resultado.get(c).variacao()))
                .limit(numDigitosNecessarios)
                .toList();

        System.out.println("\nTodas as chaves lidas do arquivo:");
        for (String chave : chaves2) {
            System.out.println(chave);
        }

        for (Integer coluna : melhoresColunas) {
            System.out.println("\nColuna " + (coluna + 1) + " associada com as seguintes chaves:");
            for (int i = 0; i < chaves2.size(); i++) {
                String chave = chaves2.get(i);
                char digitoColuna = chave.charAt(coluna);
                System.out.println("Chave: " + chave + " -> Coluna: " + (coluna + 1) + " -> Dígito: " + digitoColuna);
            }
        }

        int colunaIndex = 1;
        for (AnaliseDigitos analise : resultado) {
            System.out.println("\nDígito " + colunaIndex + ":");
            for (Digitos d : analise.digitos()) {
                System.out.println("Número: " + d.numero() + ", Quantidade: " + d.soma());
            }
            System.out.printf("Variação: %.1f\n", analise.variacao());
            colunaIndex++;
        }

        System.out.println("\nMelhores colunas selecionadas: " + melhoresColunas.stream().map(idx -> idx + 1).toList());

        int chavesInseridas = 0;
        int colisoes = 0;

        System.out.println("\nDetalhes da inserção na tabela hash:");
        for (String chave : chaves2) {
            StringBuilder indexBuilder = new StringBuilder();
            for (Integer coluna : melhoresColunas) {
                indexBuilder.append(chave.charAt(coluna));
            }

            int index = Integer.parseInt(indexBuilder.toString());

            System.out.print("Chave: " + chave + " -> Índice calculado: " + index);

            if (tabelaHash[index] == null) {
                tabelaHash[index] = chave;
                chavesInseridas++;
                System.out.println(" -> Inserido com sucesso");
            } else {
                colisoes++;
                System.out.println(" -> COLISÃO com a chave " + tabelaHash[index] + " (não inserido)");
            }
        }

        // Exibir a tabela hash final (só as posições ocupadas)
        System.out.println("\nTabela Hash final (apenas posições ocupadas):");
        for (int i = 0; i < tabelaHash.length; i++) {
            if (tabelaHash[i] != null) {
                System.out.println("Posição " + i + ": " + tabelaHash[i]);
            }
        }

        System.out.println("\nTotal de chaves inseridas: " + chavesInseridas);
        System.out.println("Total de colisões: " + colisoes);
//        System.out.println("Taxa de ocupação da tabela: " + String.format("%.2f", (chavesInseridas * 100.0 / tamanhoTabela)) + "%");
    }
}