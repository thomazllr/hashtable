package gerador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class HashTableAnalysis {
    static final int NUM_KEYS = 1000000;
    static final int KEY_LENGTH = 9;
    static final int TABLE_SIZE = NUM_KEYS;

    static class DigitStats {
        int[] frequency = new int[10];

        void reset() {
            Arrays.fill(frequency, 0);
        }
    }

    public static void initDigitStats(DigitStats[] stats, int size) {
        for (int i = 0; i < size; i++) {
            stats[i] = new DigitStats();
        }
    }

    public static void analyzeDigits(String[] keys, int numKeys, DigitStats[] stats) {
        for (int i = 0; i < numKeys; i++) {
            for (int j = 0; j < KEY_LENGTH; j++) {
                char c = keys[i].charAt(j);
                if (Character.isDigit(c)) {
                    stats[j].frequency[c - '0']++;
                }
            }
        }
    }

    public static void printDigitStats(DigitStats[] stats, int size) {
        System.out.println("Analise de digitos:");
        for (int i = 0; i < size; i++) {
            System.out.print("Posicao " + (i + 1) + ": ");
            for (int j = 0; j < 10; j++) {
                System.out.print(j + ":" + stats[i].frequency[j] + " ");
            }
            System.out.println();
        }
    }

    public static void findBestDigits(DigitStats[] stats, int size, int[] bestPositions, int numBest) {
        float[] deviation = new float[size];

        // Calculando a variação para cada posição
        for (int i = 0; i < size; i++) {
            float mean = NUM_KEYS / 10.0f, sumSquaredDiff = 0;

            for (int j = 0; j < 10; j++) {
                float diff = stats[i].frequency[j] - mean;
                sumSquaredDiff += diff * diff;
            }

            deviation[i] = sumSquaredDiff;
        }

        // Inicializando o array de melhores posições
        for (int i = 0; i < numBest; i++) {
            bestPositions[i] = i;
        }

        // Selecionando as 5 melhores posições com a menor variação
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < numBest; j++) {
                if (deviation[i] < deviation[bestPositions[j]]) {
                    for (int k = numBest - 1; k > j; k--) {
                        bestPositions[k] = bestPositions[k - 1];
                    }
                    bestPositions[j] = i;
                    break;
                }
            }
        }

        System.out.print("\nMelhores posições para hash: ");
        for (int i = 0; i < numBest; i++) {
            System.out.print(bestPositions[i] + 1 + " ");
        }
        System.out.println();
    }

    public static int hashFunction(String key, int[] bestPositions, int numBest) {
        int hash = 0;
        for (int i = 0; i < numBest; i++) {
            int d = key.charAt(bestPositions[i]) - '0';
            hash += d;
        }
        return hash % TABLE_SIZE;
    }

    public static void insertKey(String[] hashTable, String key, int[] bestPositions, int numBest) {
        int index = hashFunction(key, bestPositions, numBest);
        int originalIndex = index;
        int attempts = 0;

        while (hashTable[index] != null) {
            index = (index + 1) % TABLE_SIZE;
            attempts++;

            // Para evitar loop infinito se a tabela estiver cheia
            if (attempts >= TABLE_SIZE || index == originalIndex) {
                System.out.println("Erro: Tabela hash cheia, não foi possível inserir a chave " + key);
                return;
            }
        }
        hashTable[index] = key;
    }

    public static void printHashTable(String[] hashTable) {
        System.out.println("\nTabela Hash (primeiros 50 elementos):");
        for (int i = 0; i < 50 && i < TABLE_SIZE; i++) {
            if (hashTable[i] != null) {
                System.out.println("[" + i + "]: " + hashTable[i]);
            }
        }
    }

    public static void main(String[] args) {
        String[] keys = new String[NUM_KEYS];
        DigitStats[] stats = new DigitStats[KEY_LENGTH];
        String[] hashTable = new String[TABLE_SIZE];
        Arrays.fill(hashTable, null);

        initDigitStats(stats, KEY_LENGTH);

        int validKeys = 0;

        // Tentativa de ler o arquivo
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\08184638132\\Downloads\\hashhhhh\\src\\gerador\\chaves.txt"))) {
            System.out.println("Lendo chaves do arquivo:");
            String line;

            while ((line = br.readLine()) != null && validKeys < NUM_KEYS) {
                if (line.length() != 9 || !line.matches("\\d{9}")) {
                    System.out.println("Chave " + (validKeys + 1) + " inválida: " + line);
                    continue;
                }

                keys[validKeys] = line;
                validKeys++;
            }

            if (validKeys < NUM_KEYS) {
                System.out.println("Aviso: Apenas " + validKeys + " chaves válidas foram lidas.");
            }

        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo chaves.txt: " + e.getMessage());
            System.out.println("Gerando chaves aleatórias em vez disso...");
        }

        // Se não conseguiu ler nenhuma chave do arquivo, gera chaves aleatórias
        if (validKeys == 0) {
            System.out.println("Gerando " + NUM_KEYS + " chaves aleatórias...");
            Random random = new Random();
            for (int i = 0; i < NUM_KEYS; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < KEY_LENGTH; j++) {
                    sb.append(random.nextInt(10));
                }
                keys[i] = sb.toString();
                validKeys++;
            }
        }

        analyzeDigits(keys, validKeys, stats);
        printDigitStats(stats, KEY_LENGTH);

        int[] bestPositions = new int[5];
        findBestDigits(stats, KEY_LENGTH, bestPositions, 5);

        for (int i = 0; i < validKeys; i++) {
            insertKey(hashTable, keys[i], bestPositions, 5);
        }

        printHashTable(hashTable);
    }
}
