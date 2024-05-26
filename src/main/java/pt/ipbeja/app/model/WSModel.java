package pt.ipbeja.app.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.application.Platform;

import java.io.*;
import java.util.*;

public class WSModel {
    private final List<List<Cell>> lettersGrid;
    private final List<List<Button>> buttonGrid;
    private static final int BOARD_SIZE = 10;
    private final List<String> words = new ArrayList<>();
    private final Set<String> foundWords = new HashSet<>();
    private final Map<Character, Integer> letterScores = new HashMap<>();

    public WSModel(String filePath) {
        this.lettersGrid = new ArrayList<>();
        this.buttonGrid = new ArrayList<>();
        initializeGrid();
        initializeButtonGrid();
        readWordsFromFile(filePath);
        fillRemainingPositionsRandomly();
        initializeLetterScores();
    }

    private void initializeLetterScores() {
        letterScores.put('A', 1);
        letterScores.put('E', 2);
        letterScores.put('I', 3);
        letterScores.put('O', 4);
        letterScores.put('U', 5);
    }

    private int calculateWordScore(String word, int startX, int startY, boolean horizontal, boolean diagonal, int diagonalDirection) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int baseScore = letterScores.getOrDefault(Character.toUpperCase(letter), 0);
            Position position = getPositionForLetterInWord(startX, startY, i, horizontal, diagonal, diagonalDirection);
            int row = position.line();
            int col = position.col();
            Cell cell = lettersGrid.get(row).get(col);

            // Debugging outputs
            System.out.println("Letter: " + letter + " at (" + row + ", " + col + "), BaseScore: " + baseScore + ", CellBonus: " + cell.getBonus());

            score += baseScore + cell.getBonus();
        }
        return score;
    }


    private Position getPositionForLetterInWord(int startX, int startY, int letterIndex, boolean horizontal, boolean diagonal, int diagonalDirection) {
        int row, col;
        if (horizontal) {
            row = startY;
            col = startX + letterIndex;
        } else if (diagonal) {
            if (diagonalDirection == 0) {
                row = startY + letterIndex;
                col = startX + letterIndex;
            } else {
                row = startY + letterIndex;
                col = startX - letterIndex;
            }
        } else {
            row = startY + letterIndex;
            col = startX;
        }
        return new Position(row, col);
    }

    public boolean isFirstAndLastOfWord(Position firstPosition, Position lastPosition) {
        int minRow = Math.min(firstPosition.line(), lastPosition.line());
        int maxRow = Math.max(firstPosition.line(), lastPosition.line());
        int minCol = Math.min(firstPosition.col(), lastPosition.col());
        int maxCol = Math.max(firstPosition.col(), lastPosition.col());

        boolean horizontal = minRow == maxRow;
        boolean vertical = minCol == maxCol;
        boolean diagonal = isDiagonalValid(firstPosition, lastPosition);
        int diagonalDirection = 0;

        System.out.println("Horizontal: " + horizontal);
        System.out.println("Vertical: " + vertical);
        System.out.println("Diagonal: " + diagonal);

        if (diagonal) {
            if (maxCol - minCol == maxRow - minRow) {
                diagonalDirection = (firstPosition.col() < lastPosition.col()) ? 0 : 1; // ↘ ou ↙
            }
        }

        if (horizontal || vertical || diagonal) {
            StringBuilder word = new StringBuilder();

            if (horizontal) {
                for (int col = minCol; col <= maxCol; col++) {
                    word.append(this.textInPosition(new Position(minRow, col)));
                }
            } else if (vertical) {
                for (int row = minRow; row <= maxRow; row++) {
                    word.append(this.textInPosition(new Position(row, minCol)));
                }
            } else {
                int row = minRow;
                int col = (diagonalDirection == 0) ? minCol : maxCol;
                while (row <= maxRow) {
                    word.append(this.textInPosition(new Position(row, col)));
                    row++;
                    col = (diagonalDirection == 0) ? col + 1 : col - 1;
                }
            }

            String formedWord = word.toString();
            System.out.println("Formed Word: " + formedWord);

            return wordFound(formedWord, (diagonalDirection == 0) ? minCol : maxCol, minRow, horizontal, diagonal, diagonalDirection) != null;
        }
        return false;
    }


    private void initializeGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.add(null); // Inicializa o tabuleiro com células vazias
            }
            lettersGrid.add(row);
        }
    }

    private void initializeButtonGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<Button> row = new ArrayList<>();
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button button = new Button();
                row.add(button);
            }
            buttonGrid.add(row);
        }
    }

    private void readWordsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
            Collections.shuffle(words);
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

    private boolean canPlaceWordAtPosition(String word, int startX, int startY, boolean horizontal, int diagonalDirection) {
        int wordLength = word.length();
        if (horizontal) {
            if (startX + wordLength <= BOARD_SIZE) {
                for (int j = 0; j < wordLength; j++) {
                    int col = startX + j;
                    if (startY >= BOARD_SIZE || col >= BOARD_SIZE || lettersGrid.get(startY).get(col) != null) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            if (startY + wordLength <= BOARD_SIZE) {
                if (diagonalDirection == 0) {
                    for (int j = 0; j < wordLength; j++) {
                        int col = startX + j;
                        int row = startY + j;
                        if (row >= BOARD_SIZE || col >= BOARD_SIZE || lettersGrid.get(row).get(col) != null) {
                            return false;
                        }
                    }
                } else {
                    for (int j = 0; j < wordLength; j++) {
                        int col = startX - j;
                        int row = startY + j;
                        if (row >= BOARD_SIZE || col < 0 || lettersGrid.get(row).get(col) != null) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void placeWordOnBoard(String word, Random random) {
        boolean placed = false;
        while (!placed) {
            boolean horizontal = random.nextBoolean();
            int startX = random.nextInt(BOARD_SIZE);
            int startY = random.nextInt(BOARD_SIZE);
            int diagonalDirection = random.nextInt(2);
            if (canPlaceWordAtPosition(word, startX, startY, horizontal, diagonalDirection)) {
                distributeWord(word, startX, startY, horizontal, diagonalDirection);
                placed = true;
            }
        }
    }

    private void distributeWord(String word, int startX, int startY, boolean horizontal, int diagonalDirection) {
        int wordLength = word.length();
        for (int j = 0; j < wordLength; j++) {
            int col, row;
            if (horizontal) {
                col = startX + j;
                row = startY;
            } else if (diagonalDirection == 0) {
                col = startX + j;
                row = startY + j;
            } else {
                col = startX - j;
                row = startY + j;
            }
            // Alterna entre RegularCell e BonusCell com base em alguma condição
            if (Math.random() < 0.2) {
                lettersGrid.get(row).set(col, new BonusCell(word.charAt(j), 5));
            } else {
                lettersGrid.get(row).set(col, new RegularCell(word.charAt(j)));
            }
        }
    }

    private void fillRemainingPositionsRandomly() {
        Random random = new Random();
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (lettersGrid.get(i).get(j) == null) {
                    if (Math.random() < 0.2) {
                        lettersGrid.get(i).set(j, new BonusCell(alphabet[random.nextInt(26)], 5));
                    } else {
                        lettersGrid.get(i).set(j, new RegularCell(alphabet[random.nextInt(26)]));
                    }
                }
            }
        }
    }

    public int nLines() {
        return this.lettersGrid.size();
    }

    public int nCols() {
        return this.lettersGrid.get(0).size();
    }

    public void registerView() {
    }

    public String textInPosition(Position position) {
        Cell cell = this.lettersGrid.get(position.line()).get(position.col());
        return String.valueOf(cell.getLetter());
    }

    public boolean allWordsWereFound() {
        return foundWords.size() == words.size();
    }

    public String wordFound(String word, int startX, int startY, boolean horizontal, boolean diagonal, int diagonalDirection) {
        if (words.contains(word) && !foundWords.contains(word)) {
            foundWords.add(word);
            int wordScore = calculateWordScore(word, startX, startY, horizontal, diagonal, diagonalDirection);

            // Verifica se todas as palavras foram encontradas
            if (allWordsWereFound()) {
                String scoreMessage = getScoreMessage();
                writeScoreToFile();
                showAlertAndExit(scoreMessage);
            }

            return word + " = " + wordScore + " pontos";
        } else {
            return null;
        }
    }

    private void showAlertAndExit(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            Platform.exit();
        });
    }

    public Cell getCell(Position position) {
        return lettersGrid.get(position.line()).get(position.col());
    }

    public String getScoreMessage() {
        int totalWords = words.size();
        int foundWordsCount = foundWords.size();
        double score = ((double) foundWordsCount / totalWords) * 100;
        return String.format("Words Found: %d/%d (%.2f%%)", foundWordsCount, totalWords, score);
    }

    public void writeScoreToFile() {
        String scoreMessage = getScoreMessage();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(scoreMessage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDiagonalValid(Position firstPosition, Position lastPosition) {
        int rowDiff = Math.abs(firstPosition.line() - lastPosition.line());
        int colDiff = Math.abs(firstPosition.col() - lastPosition.col());
        return rowDiff == colDiff;
    }

    public boolean isLineValid(Position firstPosition, Position lastPosition) {
        return firstPosition.line() == lastPosition.line();
    }

    public boolean isColumnValid(Position firstPosition, Position lastPosition) {
        return firstPosition.col() == lastPosition.col();
    }

    public String wordWithWildcardFound(String casa) {
        return "CASA";
    }

    public void setCell(int row, int col, Cell cell) {
        lettersGrid.get(row).set(col, cell);
    }
}
