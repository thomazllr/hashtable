package service;

import entities.AnaliseDigitos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HtmlService {


    public static void gerarHomeHTML(List<String> paginasGeradas) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='pt-br'>");
        html.append("<head>");
        html.append("    <meta charset='UTF-8'>");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("    <title>Home - Análise de Dígitos - Método de Dispersão</title>");
        html.append("    <style>");
        html.append("        * { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; color: #333; line-height: 1.6; }");
        html.append("        .container { max-width: 1000px; margin: 0 auto; padding: 20px; }");
        html.append("        header { background-color: #4a6fa5; color: white; text-align: center; padding: 2rem; border-radius: 8px 8px 0 0; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        html.append("        h1 { font-weight: 600; margin-bottom: 10px; }");
        html.append("        .subtitle { font-weight: 300; margin-bottom: 20px; }");
        html.append("        .content { background-color: white; padding: 2rem; border-radius: 0 0 8px 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        html.append("        .analises-lista { list-style-type: none; }");
        html.append("        .analise-item { padding: 15px; border-bottom: 1px solid #eee; transition: all 0.3s ease; }");
        html.append("        .analise-item:hover { background-color: #f9f9f9; transform: translateX(5px); }");
        html.append("        .analise-item a { text-decoration: none; color: #4a6fa5; display: block; font-weight: 500; }");
        html.append("        .analise-item a:hover { color: #2c4162; }");
        html.append("        footer { text-align: center; margin-top: 20px; color: #777; font-size: 0.9rem; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class='container'>");
        html.append("        <header>");
        html.append("            <h1>Análise de Tabelas Hash</h1>");
        html.append("            <p class='subtitle'>Visualização de resultados e métricas comparativas</p>");
        html.append("        </header>");
        html.append("        <div class='content'>");
        html.append("            <ul class='analises-lista'>");

        for (String pagina : paginasGeradas) {
            // Extrair nome mais amigável do arquivo para exibição
            String displayName = pagina.replace(".html", "").replace("_", " ");
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

            html.append("            <li class='analise-item'>");
            html.append("                <a href='" + pagina + "'>" + displayName + "</a>");
            html.append("            </li>");
        }

        try (java.io.FileWriter writer = new java.io.FileWriter("D:\\uft\\hashtable\\home.html")) {
            writer.write(html.toString());
        }
    }

    public static void gerarArquivoHTML(List<String> chaves, int[][] matriz, List<Integer> melhoresColunas,
                                        List<AnaliseDigitos> resultado, int chavesInseridas, int colisoes,
                                        int tabelaHashSize, Map<String, Integer> indicesChaves,
                                        Map<String, String> chaveColisoes, List<Integer> melhoresColunasLista, String nome) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nome))) {
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
