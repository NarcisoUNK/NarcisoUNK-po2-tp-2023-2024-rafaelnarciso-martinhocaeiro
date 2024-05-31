package pt.ipbeja.app.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.application.Platform;

import java.io.*;
import java.util.*;

/**
 * WSModel class.
 * Represents the game logic model.
 *
 * @version 31/05/2024 (Final)
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class WSModel {
    private final List<List<Cell>> lettersGrid; // Grid to store the letters on the board
    private final List<List<Button>> buttonGrid; // Grid to store the buttons on the board
    private static final int BOARD_SIZE = 10; // Size of the board
    private int totalScore = 0; // Total score of the player
    private final List<String> words = new ArrayList<>(); // List of words to be found
    private final Set<String> foundWords = new HashSet<>(); // Set of words that have been found
    private final Map<Character, Integer> letterScores = new HashMap<>(); // Map of letter scores
    private final boolean withDiagonals; // Whether diagonals are allowed

    /**
     * Constructor for WSModel.
     *
     * @param filePath       the file path to read words from
     * @param withDiagonals  whether diagonals are allowed
     */
    public WSModel(String filePath, boolean withDiagonals) {
        this.lettersGrid = new ArrayList<>();
        this.buttonGrid = new ArrayList<>();
        this.withDiagonals = withDiagonals;
        initializeGrid();
        initializeButtonGrid();
        readWordsFromFile(filePath);
        fillRemainingPositionsRandomly();
        initializeLetterScores();
    }

    /**
     * Initializes the letter scores.
     */
    private void initializeLetterScores() {
        letterScores.put('A', 1);
        letterScores.put('E', 2);
        letterScores.put('I', 3);
        letterScores.put('O', 4);
        letterScores.put('U', 5);
    }

    /**
     * Gets the total score.
     *
     * @return the total score
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Adds to the total score.
     *
     * @param score the score to add
     */
    public void addToTotalScore(int score) {
        totalScore += score;
    }

    /**
     * Calculates the score for a word found with a wildcard.
     *
     * @param word              the word found
     * @param startX            the starting X position
     * @param startY            the starting Y position
     * @param horizontal        whether the word is horizontal
     * @param diagonal          whether the word is diagonal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return the score for the word
     */
    private int wordWithWildcardFound(String word, int startX, int startY, boolean horizontal, boolean diagonal, int diagonalDirection) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int baseScore = letterScores.getOrDefault(Character.toUpperCase(letter), 0);
            Position position = getPositionForLetterInWord(startX, startY, i, horizontal, diagonal, diagonalDirection);
            int row = position.line();
            int col = position.col();
            Cell cell = lettersGrid.get(row).get(col);
            score += baseScore + cell.getBonus();
        }
        return score;
    }

    /**
     * Gets the position for a letter in a word.
     *
     * @param startX            the starting X position
     * @param startY            the starting Y position
     * @param letterIndex       the index of the letter in the word
     * @param horizontal        whether the word is horizontal
     * @param diagonal          whether the word is diagonal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return the position of the letter in the word
     */
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

    /**
     * Checks if the first and last positions of a word are valid.
     *
     * @param firstPosition the first position
     * @param lastPosition  the last position
     * @return true if the first and last positions are valid, false otherwise
     */
    public boolean isFirstAndLastOfWord(Position firstPosition, Position lastPosition) {
        int minRow = Math.min(firstPosition.line(), lastPosition.line());
        int maxRow = Math.max(firstPosition.line(), lastPosition.line());
        int minCol = Math.min(firstPosition.col(), lastPosition.col());
        int maxCol = Math.max(firstPosition.col(), lastPosition.col());

        boolean horizontal = minRow == maxRow;
        boolean vertical = minCol == maxCol;
        boolean diagonal = isDiagonalValid(firstPosition, lastPosition);
        int diagonalDirection = 0;

        if (diagonal) {
            if (maxCol - minCol == maxRow - minRow) {
                diagonalDirection = (firstPosition.col() < lastPosition.col()) ? 0 : 1; // ↘ or ↙
            }
        }

        if (horizontal || vertical || diagonal) {
            String formedWord = buildWord(minRow, maxRow, minCol, maxCol, diagonalDirection);

            return isWordValid(formedWord, (diagonalDirection == 0) ? minCol : maxCol, minRow, horizontal, diagonal, diagonalDirection);
        }
        return false;
    }

    /**
     * Builds a word from the given positions.
     *
     * @param minRow            the minimum row
     * @param maxRow            the maximum row
     * @param minCol            the minimum column
     * @param maxCol            the maximum column
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return the formed word
     */
    private String buildWord(int minRow, int maxRow, int minCol, int maxCol, int diagonalDirection) {
        StringBuilder word = new StringBuilder();

        if (minRow == maxRow) { // Horizontal
            for (int col = minCol; col <= maxCol; col++) {
                word.append(this.textInPosition(new Position(minRow, col)));
            }
        } else if (minCol == maxCol) { // Vertical
            for (int row = minRow; row <= maxRow; row++) {
                word.append(this.textInPosition(new Position(row, minCol)));
            }
        } else { // Diagonal
            int row = minRow;
            int col = (diagonalDirection == 0) ? minCol : maxCol;
            while (row <= maxRow) {
                word.append(this.textInPosition(new Position(row, col)));
                row++;
                col = (diagonalDirection == 0) ? col + 1 : col - 1;
            }
        }

        return word.toString();
    }

    /**
     * Checks if a formed word is valid.
     *
     * @param formedWord        the formed word
     * @param col               the starting column
     * @param row               the starting row
     * @param horizontal        whether the word is horizontal
     * @param diagonal          whether the word is diagonal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return true if the word is valid, false otherwise
     */
    private boolean isWordValid(String formedWord, int col, int row, boolean horizontal, boolean diagonal, int diagonalDirection) {
        return wordFound(formedWord, col, row, horizontal, diagonal, diagonalDirection) != null;
    }

    /**
     * Initializes the letters grid with empty cells.
     */
    private void initializeGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.add(null); // Initialize the board with empty cells
            }
            lettersGrid.add(row);
        }
    }

    /**
     * Initializes the button grid with buttons.
     */
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

    /**
     * Reads words from a file and stores them in the words list.
     *
     * @param filePath the file path to read words from
     */
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

    /**
     * Distributes the words on the board.
     *
     * @param words the list of words to distribute
     */
    private void distributeWordsOnBoard(List<String> words) {
        Random random = new Random();
        for (String word : words) {
            placeWordOnBoard(word, random);
        }
    }

    /**
     * Checks if a word can be placed at a given position.
     *
     * @param word              the word to place
     * @param startX            the starting X position
     * @param startY            the starting Y position
     * @param horizontal        whether the word is horizontal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return true if the word can be placed, false otherwise
     */
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

    /**
     * Places a word on the board.
     *
     * @param word   the word to place
     * @param random the random number generator
     */
    private void placeWordOnBoard(String word, Random random) {
        boolean placed = false;
        while (!placed) {
            boolean horizontal = random.nextBoolean();
            int startX = random.nextInt(BOARD_SIZE);
            int startY = random.nextInt(BOARD_SIZE);
            int diagonalDirection = random.nextInt(2);
            if (withDiagonals) {
                if (canPlaceWordAtPosition(word, startX, startY, horizontal, diagonalDirection)) {
                    distributeWord(word, startX, startY, horizontal, diagonalDirection);
                    placed = true;
                }
            } else {
                if (canPlaceWordAtPosition(word, startX, startY, horizontal, -1)) {
                    distributeWord(word, startX, startY, horizontal, -1);
                    placed = true;
                }
            }
        }
    }

    /**
     * Distributes a word on the board.
     *
     * @param word              the word to distribute
     * @param startX            the starting X position
     * @param startY            the starting Y position
     * @param horizontal        whether the word is horizontal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     */
    private void distributeWord(String word, int startX, int startY, boolean horizontal, int diagonalDirection) {
        Random random = new Random();
        for (int j = 0; j < word.length(); j++) {
            char letter = word.charAt(j);
            Cell cell;
            if (random.nextDouble() < 0.2) {
                cell = new BonusCell(letter, 5); // Assign a BonusCell with a 20% probability
            } else {
                cell = new RegularCell(letter); // Assign a RegularCell with an 80% probability
            }

            if (horizontal) {
                lettersGrid.get(startY).set(startX + j, cell);
            } else {
                if (diagonalDirection == 0) {
                    lettersGrid.get(startY + j).set(startX + j, cell);
                } else if (diagonalDirection == 1) {
                    lettersGrid.get(startY + j).set(startX - j, cell);
                } else {
                    lettersGrid.get(startY + j).set(startX, cell);
                }
            }
        }
    }

    /**
     * Fills the remaining positions on the board randomly with letters.
     */
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

    /**
     * Gets the number of lines in the grid.
     *
     * @return the number of lines
     */
    public int nLines() {
        return this.lettersGrid.size();
    }

    /**
     * Gets the number of columns in the grid.
     *
     * @return the number of columns
     */
    public int nCols() {
        return this.lettersGrid.get(0).size();
    }

    /**
     * Registers the view.
     */
    public void registerView() {
    }

    /**
     * Gets the text in a given position.
     *
     * @param position the position
     * @return the text in the position
     */
    public String textInPosition(Position position) {
        Cell cell = this.lettersGrid.get(position.line()).get(position.col());
        return String.valueOf(cell.getLetter());
    }

    /**
     * Checks if all words were found.
     *
     * @return true if all words were found, false otherwise
     */
    public boolean allWordsWereFound() {
        return foundWords.size() == words.size();
    }

    /**
     * Marks a word as found and calculates its score.
     *
     * @param word              the word found
     * @param startX            the starting X position
     * @param startY            the starting Y position
     * @param horizontal        whether the word is horizontal
     * @param diagonal          whether the word is diagonal
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     * @return the word and its score if found, null otherwise
     */
    public String wordFound(String word, int startX, int startY, boolean horizontal, boolean diagonal, int diagonalDirection) {
        if (words.contains(word) && !foundWords.contains(word)) {
            foundWords.add(word);

            int wordScore = wordWithWildcardFound(word, startX, startY, horizontal, diagonal, diagonalDirection);
            addToTotalScore(wordScore);
            // Check if all words were found
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

    /**
     * Generates the list of words to be found.
     *
     * @return the list of words
     */
    public String generateWordsList() {
        StringBuilder wordsList = new StringBuilder("Palavras a encontrar:\n");
        for (String word : words) {
            wordsList.append(word).append("\n");
        }
        return wordsList.toString();
    }

    /**
     * Shows an alert and exits the application.
     *
     * @param message the message to show
     */
    private void showAlertAndExit(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fim de jogo");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            Platform.exit();
        });
    }

    /**
     * Gets the cell at a given position.
     *
     * @param position the position
     * @return the cell at the position
     */
    public Cell getCell(Position position) {
        return lettersGrid.get(position.line()).get(position.col());
    }

    /**
     * Gets the score message.
     *
     * @return the score message
     */
    public String getScoreMessage() {
        int totalWords = words.size();
        int foundWordsCount = foundWords.size();
        double score = ((double) foundWordsCount / totalWords) * 100;
        return String.format("Palavras encontradas: %d/%d (%.2f%%)", foundWordsCount, totalWords, score);
    }

    /**
     * Writes the score to a file.
     */
    public void writeScoreToFile() {
        String scoreMessage = getScoreMessage();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(scoreMessage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the diagonal is valid.
     *
     * @param firstPosition the first position
     * @param lastPosition  the last position
     * @return true if the diagonal is valid, false otherwise
     */
    public boolean isDiagonalValid(Position firstPosition, Position lastPosition) {
        int rowDiff = Math.abs(firstPosition.line() - lastPosition.line());
        int colDiff = Math.abs(firstPosition.col() - lastPosition.col());
        return rowDiff == colDiff;
    }

    /**
     * Checks if the line is valid.
     *
     * @param firstPosition the first position
     * @param lastPosition  the last position
     * @return true if the line is valid, false otherwise
     */
    public boolean isLineValid(Position firstPosition, Position lastPosition) {
        return firstPosition.line() == lastPosition.line();
    }

    /**
     * Checks if the column is valid.
     *
     * @param firstPosition the first position
     * @param lastPosition  the last position
     * @return true if the column is valid, false otherwise
     */
    public boolean isColumnValid(Position firstPosition, Position lastPosition) {
        return firstPosition.col() == lastPosition.col();
    }

    /**
     * Sets a cell at a given position.
     *
     * @param row  the row
     * @param col  the column
     * @param cell the cell
     */
    public void setCell(int row, int col, Cell cell) {
        lettersGrid.get(row).set(col, cell);
    }
}
