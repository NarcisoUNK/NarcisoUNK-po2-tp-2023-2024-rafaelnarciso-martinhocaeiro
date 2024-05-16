package pt.ipbeja.app.model;

import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Game model
 * @author anonymized
 * @version 2024/04/14
 */
public class WSModel {

    private final List<List<String>> lettersGrid;
    private WSView wsView;
    private final int BOARD_SIZE = 10; // Tamanho do tabuleiro


    public WSModel(String filePath) {
        this.lettersGrid = new ArrayList<>();
        initializeGrid();
        readWordsFromFile(filePath);
        fillRemainingPositionsRandomly();
    }

    private void initializeGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.add(null); // Inicializa o tabuleiro com espaços vazios
            }
            lettersGrid.add(row);
        }
    }

    private void readWordsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> words = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
            Collections.shuffle(words); // Embaralha as palavras
            distributeWordsOnBoard(words);
            fillRemainingPositionsRandomly();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void distributeWordsOnBoard(List<String> words) {
        Random random = new Random();
        for (String word : words) {
            placeWordOnBoard(word, random);
        }
    }

    private void placeWordOnBoard(String word, Random random) {
        boolean placed = false;
        while (!placed) {
            boolean horizontal = random.nextBoolean(); // Determina aleatoriamente a orientação da palavra
            int startX = random.nextInt(BOARD_SIZE); // Início da distribuição de palavras na horizontal
            int startY = random.nextInt(BOARD_SIZE); // Início da distribuição de palavras na vertical
            int wordLength = word.length();
            if (canPlaceWordAtPosition(word, startX, startY, horizontal)) {
                distributeWord(word, startX, startY, horizontal);
                placed = true;
            }
        }
    }

    private boolean canPlaceWordAtPosition(String word, int startX, int startY, boolean horizontal) {
        int wordLength = word.length();
        if ((horizontal && startX + wordLength <= BOARD_SIZE) || (!horizontal && startY + wordLength <= BOARD_SIZE)) {
            for (int j = 0; j < wordLength; j++) {
                int row = horizontal ? startY : startY + j;
                int col = horizontal ? startX + j : startX;
                if (lettersGrid.get(row).get(col) != null) {
                    return false; // Há sobreposição com outra palavra
                }
            }
            return true; // Não há sobreposição, a palavra pode ser colocada nesta posição
        }
        return false; // A palavra não cabe na posição atual do tabuleiro
    }

    private void distributeWord(String word, int startX, int startY, boolean horizontal) {
        int wordLength = word.length();
        for (int j = 0; j < wordLength; j++) {
            int row = horizontal ? startY : startY + j;
            int col = horizontal ? startX + j : startX;
            lettersGrid.get(row).set(col, String.valueOf(word.charAt(j)));
        }
    }








    private void fillRemainingPositionsRandomly() {
        Random random = new Random();
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (lettersGrid.get(i).get(j) == null) {
                    lettersGrid.get(i).set(j, String.valueOf(alphabet[random.nextInt(26)]));
                }
            }
        }
    }







    public int nLines() { return this.lettersGrid.size(); }
    public int nCols() { return this.lettersGrid.get(0).size(); }

    public void registerView(WSView wsView) {
        this.wsView = wsView;
    }

    public String textInPosition(Position position) {
        return this.lettersGrid.get(position.line()).get(position.col());
    }

    public boolean allWordsWereFound() {
        // Implementação do método para verificar se todas as palavras foram encontradas
        // Deve retornar true se todas as palavras foram encontradas, caso contrário, false
        return false; // Modifique conforme necessário
    }

    public String wordFound(String word) {
        // Implementação do método para verificar se a palavra foi encontrada
        // Deve retornar a palavra encontrada ou null se não foi encontrada
        return null; // Modifique conforme necessário
    }

    public String wordWithWildcardFound(String word) {
        // Implementação do método para verificar se a palavra com coringa foi encontrada
        // Deve retornar a palavra encontrada ou null se não foi encontrada
        return null; // Modifique conforme necessário
    }



    public boolean isFirstAndLastOfWord(Position firstPosition, Position lastPosition) {
        int minRow = Math.min(firstPosition.line(), lastPosition.line());
        int maxRow = Math.max(firstPosition.line(), lastPosition.line());
        int minCol = Math.min(firstPosition.col(), lastPosition.col());
        int maxCol = Math.max(firstPosition.col(), lastPosition.col());

        // Verifica se as letras clicadas estão na mesma linha ou coluna
        if (minRow == maxRow || minCol == maxCol) {
            for (int row = minRow; row <= maxRow; row++) {
                for (int col = minCol; col <= maxCol; col++) {
                    if (textInPosition(new Position(row, col)) == null) {
                        return false; // Se houver espaços em branco entre as letras, não é o início e o fim de uma palavra
                    }
                }
            }
            return true;
        }

        return false;
    }




}
