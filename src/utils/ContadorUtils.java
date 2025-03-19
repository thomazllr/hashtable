package utils;

import entities.AnaliseDigitos;
import entities.AnaliseDigitosPair;
import entities.Digitos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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


    public static void gerarArquivoHTML(List<String> chaves, int[][] matriz, List<Integer> melhoresColunas,
                                        List<AnaliseDigitos> resultado, int chavesInseridas, int colisoes,
                                        int tabelaHashSize, Map<String, Integer> indicesChaves,
                                        Map<String, String> chaveColisoes, List<Integer> melhoresColunasLista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("hash_analysis.html"))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang=\"pt-br\">\n");
            writer.write("<head>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.write("    <title>Análise de Tabela Hash</title>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
            writer.write("        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n");
            writer.write("        th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }\n");
            writer.write("        th { background-color: #f2f2f2; }\n");
            writer.write("        .melhor-coluna { background-color: #d4edda; color: #155724; }\n");
            writer.write("        .outra-coluna { background-color: #f8d7da; color: #721c24; }\n");
            writer.write("        .indice { font-weight: bold; padding-left: 5px; }\n");
            writer.write("        .container { display: flex; margin-bottom: 20px; }\n");
            writer.write("        .info-box { flex: 1; padding: 10px; margin: 5px; border: 1px solid #ddd; border-radius: 5px; }\n");
            writer.write("        .estatisticas { background-color: #e2f0fb; }\n");
            writer.write("        .colisao { background-color: #fff3cd; color: #856404; }\n");
            writer.write("        .inserido { background-color: #d4edda; color: #155724; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <h1>Análise de Tabela Hash</h1>\n");

            writer.write("    <div class=\"container\">\n");
            writer.write("        <div class=\"info-box estatisticas\">\n");
            writer.write("            <h3>Estatísticas</h3>\n");
            writer.write("            <p><strong>Total de chaves lidas:</strong> " + chaves.size() + "</p>\n");
            writer.write("            <p><strong>Tamanho da tabela hash:</strong> " + tabelaHashSize + "</p>\n");
            writer.write("            <p><strong>Chaves inseridas:</strong> " + chavesInseridas + "</p>\n");
            writer.write("            <p><strong>Colisões:</strong> " + colisoes + "</p>\n");
            writer.write("            <p><strong>Taxa de ocupação:</strong> " + String.format("%.2f", (chavesInseridas * 100.0 / tabelaHashSize)) + "%</p>\n");
            writer.write("        </div>\n");
            writer.write("        <div class=\"info-box\">\n");
            writer.write("            <h3>Melhores Colunas</h3>\n");
            writer.write("            <p>As melhores colunas selecionadas para formar o índice são: <strong>" +
                    melhoresColunas.stream().map(idx -> String.valueOf(idx + 1)).collect(Collectors.joining(", ")) +
                    "</strong></p>\n");
            writer.write("            <p><strong>Legenda:</strong></p>\n");
            writer.write("            <p><span class=\"melhor-coluna\" style=\"padding: 2px 8px;\">Verde</span> - Colunas selecionadas para o índice</p>\n");
            writer.write("            <p><span class=\"outra-coluna\" style=\"padding: 2px 8px;\">Vermelho</span> - Colunas não selecionadas</p>\n");
            writer.write("            <p><span class=\"inserido\" style=\"padding: 2px 8px;\">Verde</span> - Chave inserida com sucesso</p>\n");
            writer.write("            <p><span class=\"colisao\" style=\"padding: 2px 8px;\">Amarelo</span> - Chave que causou colisão (não inserida)</p>\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");


            writer.write("    <h2>Matriz de Chaves</h2>\n");
            writer.write("    <table>\n");

            writer.write("        <tr>\n");
            writer.write("            <th>Chave</th>\n");

            for (int i = 0; i < matriz[0].length; i++) {
                String classeColuna = melhoresColunas.contains(i) ? "melhor-coluna" : "outra-coluna";
                writer.write("            <th class=\"" + classeColuna + "\">Coluna " + "<span class=\"indice\">" + (i + 1) + "</span></th>\n");
            }

            writer.write("            <th>Índice</th>\n");
            writer.write("            <th>Status</th>\n");
            writer.write("        </tr>\n");

            for (int i = 0; i < chaves.size(); i++) {
                String chave = chaves.get(i);
                boolean foiInserida = indicesChaves.containsKey(chave);
                String classe = foiInserida ? "inserido" : "colisao";

                writer.write("        <tr class=\"" + classe + "\">\n");
                writer.write("            <td>" + chave + "</td>\n");

                for (int j = 0; j < matriz[i].length; j++) {
                    String classeColuna = melhoresColunas.contains(j) ? "melhor-coluna" : "outra-coluna";
                    writer.write("            <td class=\"" + classeColuna + "\">" + matriz[i][j] + "</td>\n");
                }

                // Calcular índice
                StringBuilder indexBuilder = new StringBuilder();
                for (Integer coluna : melhoresColunasLista) {
                    indexBuilder.append(chave.charAt(coluna));
                }
                int index = Integer.parseInt(indexBuilder.toString());

                writer.write("            <td>" + index + "</td>\n");

                String status = foiInserida ? "Inserido" : "Colisão";
                writer.write("            <td>" + status + "</td>\n");

                writer.write("        </tr>\n");
            }

            writer.write("    </table>\n");

            writer.write("    <h2>Análise de Variação por Coluna</h2>\n");
            writer.write("    <table>\n");
            writer.write("        <tr>\n");
            writer.write("            <th>Coluna</th>\n");
            writer.write("            <th>Variação</th>\n");
            writer.write("            <th>Status</th>\n");
            writer.write("        </tr>\n");

            for (int i = 0; i < resultado.size(); i++) {
                String classeColuna = melhoresColunas.contains(i) ? "melhor-coluna" : "outra-coluna";
                writer.write("        <tr class=\"" + classeColuna + "\">\n");
                writer.write("            <td>" + (i + 1) + "</td>\n");
                writer.write("            <td>" + String.format("%.1f", resultado.get(i).variacao()) + "</td>\n");
                writer.write("            <td>" + (melhoresColunas.contains(i) ? "Selecionada" : "Não selecionada") + "</td>\n");
                writer.write("        </tr>\n");
            }

            writer.write("    </table>\n");


            writer.write("    <h2>Chaves e Índices</h2>\n");
            writer.write("    <table>\n");
            writer.write("        <tr>\n");
            writer.write("            <th>Chave</th>\n");
            writer.write("            <th>Dígitos Usados</th>\n");
            writer.write("            <th>Índice Calculado</th>\n");
            writer.write("            <th>Status</th>\n");
            writer.write("        </tr>\n");

            for (String chave : chaves) {
                StringBuilder indexBuilder = new StringBuilder();
                for (Integer coluna : melhoresColunasLista) {
                    indexBuilder.append(chave.charAt(coluna));
                }
                int index = Integer.parseInt(indexBuilder.toString());

                boolean foiInserida = indicesChaves.containsKey(chave);
                String classe = foiInserida ? "inserido" : "colisao";
                String status = foiInserida ? "Inserido na posição " + index :
                        "Colisão com a chave " + chaveColisoes.get(chave);

                writer.write("        <tr class=\"" + classe + "\">\n");
                writer.write("            <td>" + chave + "</td>\n");
                writer.write("            <td>");

                for (Integer coluna : melhoresColunasLista) {
                    writer.write(chave.charAt(coluna) + " ");
                }

                writer.write("</td>\n");
                writer.write("            <td>" + index + "</td>\n");
                writer.write("            <td>" + status + "</td>\n");
                writer.write("        </tr>\n");
            }

            writer.write("    </table>\n");


            writer.write("    <h2>Tabela Hash (posições ocupadas)</h2>\n");
            writer.write("    <table>\n");
            writer.write("        <tr>\n");
            writer.write("            <th>Posição</th>\n");
            writer.write("            <th>Chave</th>\n");
            writer.write("        </tr>\n");

            Map<Integer, String> posicoesOcupadas = new TreeMap<>();
            for (Map.Entry<String, Integer> entry : indicesChaves.entrySet()) {
                posicoesOcupadas.put(entry.getValue(), entry.getKey());
            }

            for (Map.Entry<Integer, String> entry : posicoesOcupadas.entrySet()) {
                writer.write("        <tr>\n");
                writer.write("            <td>" + entry.getKey() + "</td>\n");
                writer.write("            <td>" + entry.getValue() + "</td>\n");
                writer.write("        </tr>\n");
            }

            writer.write("    </table>\n");

            writer.write("</body>\n");
            writer.write("</html>");
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo HTML: " + e.getMessage());
        }
    }

}
