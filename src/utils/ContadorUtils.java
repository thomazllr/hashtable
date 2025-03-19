package utils;

import entities.AnaliseDigitos;
import entities.AnaliseDigitosPair;
import entities.Digitos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ContadorUtils {


    public static List<AnaliseDigitos> contarPorColuna(int[][] matriz) {
        if (matriz.length == 0) {
            return Collections.emptyList();
        }
        int colunas = matriz[0].length;
        List<Map<Integer, Long>> contagemPorColuna = new ArrayList<>();

        for (int i = 0; i < colunas; i++) {
            Map<Integer, Long> numbersKeys = new LinkedHashMap<>();
            for (int j = 0; j <= 9; j++) {
                numbersKeys.put(j, 0L);
            }
            contagemPorColuna.add(numbersKeys);
        }

        for (int[] linha : matriz) {
            for (int coluna = 0; coluna < linha.length; coluna++) {
                int numero = linha[coluna];
                if (contagemPorColuna.get(coluna).containsKey(numero)) {
                    contagemPorColuna.get(coluna).put(numero, contagemPorColuna.get(coluna).get(numero) + 1);
                }
            }
        }
        List<AnaliseDigitos> resultado = new ArrayList<>();

        for (var mapa : contagemPorColuna) {
            List<Digitos> lista = new ArrayList<>();
            long total = 0;

            for (var entry : mapa.entrySet()) {
                Digitos digitos = new Digitos(entry.getKey(), entry.getValue());
                total += entry.getValue();
                lista.add(digitos);
            }
            double frequenciaEsperada = total / 10.0;
            double variacao = 0;

            for (Map.Entry<Integer, Long> entry : mapa.entrySet()) {
                long quantidade = entry.getValue();

                // Transformando a quantidade para double
                double quantidadeDouble = (double) quantidade;

                // Calculando a diferença (frequência esperada ainda sendo double)
                double diferenca = Math.abs(quantidadeDouble - frequenciaEsperada);

                // Atualizando a variação
                variacao += diferenca;
            }

            resultado.add(new AnaliseDigitos(lista, variacao));
        }


            return resultado;
    }

    public static List<String> lerChavesDoArquivo(String caminhoArquivo) throws IOException {
        return Files.readAllLines(Paths.get(caminhoArquivo));
    }

    public static List<Integer> encontrarMelhoresColunas(List<AnaliseDigitos> resultado, int numDigitos) {
        List<Integer> melhoresColunas = new ArrayList<>();

        List<AnaliseDigitosPair> pares = new ArrayList<>();
        for (int i = 0; i < resultado.size(); i++) {
            pares.add(new AnaliseDigitosPair(i, resultado.get(i).variacao()));
        }

        pares.sort(Comparator.comparing(a -> a.variacao, Comparator.naturalOrder()));

        for (int i = 0; i < Math.min(numDigitos, pares.size()); i++) {
            melhoresColunas.add(pares.get(i).index);
        }

        return melhoresColunas;
    }

}
