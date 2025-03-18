package hash;

import entities.AnaliseDigitos;
import entities.Digitos;

import java.io.IOException;
import java.util.List;

import static utils.ContadorUtils.contarPorColuna;
import static utils.ContadorUtils.lerChavesDoArquivo;

public class HashTableAnalysis {

    public static void main(String[] args) throws IOException {

        String file = "D:\\uft\\hashtable\\chavesTeste.txt";

        List<String> chaves = lerChavesDoArquivo(file);

        assert chaves != null;
        int[][] matriz = new int[chaves.size()][chaves.getFirst().length()];

        for (int i = 0; i < chaves.size(); i++) {
            for (int j = 0; j < chaves.get(i).length(); j++) {
                matriz[i][j] = Character.getNumericValue(chaves.get(i).charAt(j));
            }
        }

        var resultado = contarPorColuna(matriz);

        int colunaIndex = 1;
        for (AnaliseDigitos analise : resultado) {
            System.out.println("Dígito " + colunaIndex + ":");
            for (Digitos d : analise.digitos()) {
                System.out.println("Número: " + d.numero() + ", Quantidade: " + d.soma());
            }
            System.out.printf("Variação: %.2f" , analise.variacao());
            System.out.println();
            colunaIndex++;
        }
    }

}
