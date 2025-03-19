package hash;

import entities.AnaliseDigitos;
import entities.Digitos;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

        // Mapa para armazenar índices das chaves inseridas
        Map<String, Integer> indicesChaves = new HashMap<>();
        // Mapa para armazenar chaves que colidiram e com quem colidiram
        Map<String, String> chaveColisoes = new HashMap<>();

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
                indicesChaves.put(chave, index);  // Armazenar o índice da chave
                chavesInseridas++;
                System.out.println(" -> Inserido com sucesso");
            } else {
                colisoes++;
                chaveColisoes.put(chave, tabelaHash[index]);  // Armazenar com quem colidiu
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

        // Converter melhoresColunas para um Set para facilitar verificações
        Set<Integer> melhoresColunasSet = melhoresColunas.stream().collect(Collectors.toSet());

        // Gerar arquivo HTML
        gerarArquivoHTML(chaves2, matriz, melhoresColunas, resultado, chavesInseridas, colisoes,
                tabelaHashSize, indicesChaves, chaveColisoes, melhoresColunas);

        System.out.println("\nArquivo HTML gerado com sucesso: hash_analysis.html");
    }

    private static void gerarArquivoHTML(List<String> chaves, int[][] matriz, List<Integer> melhoresColunas,
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

            // Container de informações gerais
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


            // Tabela de chaves e colunas
            writer.write("    <h2>Matriz de Chaves</h2>\n");
            writer.write("    <table>\n");

            // Cabeçalho com índices de colunas
            writer.write("        <tr>\n");
            writer.write("            <th>Chave</th>\n");

            for (int i = 0; i < matriz[0].length; i++) {
                String classeColuna = melhoresColunas.contains(i) ? "melhor-coluna" : "outra-coluna";
                writer.write("            <th class=\"" + classeColuna + "\">Coluna "+ "<span class=\"indice\">" + (i + 1) + "</span></th>\n");
            }

            writer.write("            <th>Índice</th>\n");
            writer.write("            <th>Status</th>\n");
            writer.write("        </tr>\n");

            // Linhas da tabela
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

            // Tabela de variações por coluna
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



            // Tabela de chaves, índices e status
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


            // Tabela Hash (posições ocupadas)
            writer.write("    <h2>Tabela Hash (posições ocupadas)</h2>\n");
            writer.write("    <table>\n");
            writer.write("        <tr>\n");
            writer.write("            <th>Posição</th>\n");
            writer.write("            <th>Chave</th>\n");
            writer.write("        </tr>\n");

            // Mapear as posições ocupadas para ordenar
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