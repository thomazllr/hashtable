package hash;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static service.HtmlService.gerarArquivoHTML;
import static service.HtmlService.gerarHomeHTML;
import static utils.ContadorUtils.*;

public class HashTableAnalysis {
    public static void main(String[] args) throws IOException {
        File pastaRaiz = new File("D:\\uft\\hashtable\\");
        File[] arquivos = pastaRaiz.listFiles((dir, name) -> name.matches("chaves\\d+\\.txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo de chaves encontrado na pasta raiz.");
            return;
        }

        List<String> paginasGeradas = new ArrayList<>();

        for (File arquivo : arquivos) {
            String nomeArquivo = arquivo.getName();
            String chaves = nomeArquivo.replace("chaves", "").replace(".txt", "");
            String filePath = arquivo.getAbsolutePath();
            int tabelaHashSize = Integer.parseInt(chaves);

            if (tabelaHashSize < 10) {
                tabelaHashSize = 10;

            } else if (tabelaHashSize < 100) {
                tabelaHashSize = 100;
            }

            int digitosNecessarios = (int) Math.ceil(Math.log10(tabelaHashSize));
            String[] tabelaHash = new String[tabelaHashSize];
            Arrays.fill(tabelaHash, null);

            List<String> chaves2 = lerChavesDoArquivo(filePath);
            int[][] matriz = new int[chaves2.size()][chaves2.get(0).length()];
            for (int i = 0; i < chaves2.size(); i++) {
                for (int j = 0; j < chaves2.get(i).length(); j++) {
                    matriz[i][j] = Character.getNumericValue(chaves2.get(i).charAt(j));
                }
            }

            var resultado = contarPorColuna(matriz);
            List<Integer> melhoresColunas = encontrarMelhoresColunas(resultado, digitosNecessarios)
                    .stream()
                    .sorted(Comparator.comparingDouble(c -> resultado.get(c).variacao()))
                    .limit(digitosNecessarios)
                    .toList();

            int chavesInseridas = 0, colisoes = 0;
            Map<String, Integer> indicesChaves = new HashMap<>();
            Map<String, String> chaveColisoes = new HashMap<>();

            for (String chave : chaves2) {
                StringBuilder indexBuilder = new StringBuilder();
                for (Integer coluna : melhoresColunas) {
                    indexBuilder.append(chave.charAt(coluna));
                }
                int index = Integer.parseInt(indexBuilder.toString());

                if (tabelaHash[index] == null) {
                    tabelaHash[index] = chave;
                    indicesChaves.put(chave, index);
                    chavesInseridas++;
                } else {
                    colisoes++;
                    chaveColisoes.put(chave, tabelaHash[index]);
                }
            }

            String nomePagina = "chaves" + chaves + ".html";
            paginasGeradas.add(nomePagina);
            gerarArquivoHTML(chaves2, matriz, melhoresColunas, resultado, chavesInseridas, colisoes,
                    tabelaHashSize, indicesChaves, chaveColisoes, melhoresColunas, nomePagina);
        }

        gerarHomeHTML(paginasGeradas);
        System.out.println("\nGENERATED WITH SUCCESS!");
    }


}
