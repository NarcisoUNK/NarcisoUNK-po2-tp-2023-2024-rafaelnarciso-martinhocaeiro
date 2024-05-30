package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.ui.JavaFXInitializer;
import pt.ipbeja.app.model.RegularCell;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WSModelTest class.
 * Tests all the main functions in the game.
 *
 * @version 30/05/2024
 *  * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
class WSModelTest {

    private static final String path = "src/main/java/pt/ipbeja/app/words.txt";

    /**
     * Initializes JavaFX before all tests.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.initialize();
    }

    /**
     * Tests if a word can be found in the model.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testWordFound() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        String word = words.get(0);
        Position firstPosition = new Position(0, 0);
        Position lastPosition = getLastPosition(firstPosition, word.length() - 1);

        assertTrue(model.isFirstAndLastOfWord(firstPosition, lastPosition));
    }

    /**
     * Tests if a word with wildcard can be found in the model.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testWordWithWildcardFound() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        for (String word : words) {
            int expectedPoints = calculatePoints(word);
            assertEquals(word + " = " + expectedPoints + " pontos", model.wordFound(word, 0, 0, true, false, 0));
        }
    }

    /**
     * Tests if all words were found in the model.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testAllWordsWereFound() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        for (String word : words) {
            model.wordFound(word, 0, 0, true, false, 0);
        }

        assertTrue(model.allWordsWereFound());
    }

    /**
     * Gets the last position of a word based on its length.
     *
     * @param firstPosition the starting position of the word
     * @param wordLength the length of the word
     * @return the last position of the word
     */
    private Position getLastPosition(Position firstPosition, int wordLength) {
        return new Position(firstPosition.line(), firstPosition.col() + wordLength);
    }

    /**
     * Tests if a diagonal is valid.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testIsDiagonalValid() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(1, 1);

        assertTrue(model.isDiagonalValid(firstPosition, lastPosition));
    }

    /**
     * Tests if a line is valid.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testIsLineValid() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isLineValid(firstPosition, lastPosition));
    }

    /**
     * Tests if a column is valid.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testIsColumnValid() throws IOException {
        WSModel model = new WSModel(path, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(3, 0);

        assertTrue(model.isColumnValid(firstPosition, lastPosition));
    }

    /**
     * Reads words from a file.
     *
     * @return a list of words from the file
     * @throws IOException if an I/O error occurs
     */
    private List<String> readWordsFromFile() throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WSModelTest.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }
        return words;
    }

    /**
     * Sets up the grid in the model from a file.
     *
     * @param model the WSModel to set up
     * @return a list of words set up in the grid
     * @throws IOException if an I/O error occurs
     */
    private List<String> setupGridFromFile(WSModel model) throws IOException {
        List<String> words = readWordsFromFile();
        int row = 0;
        for (String word : words) {
            for (int col = 0; col < word.length(); col++) {
                char letter = word.charAt(col);
                model.setCell(row, col, new RegularCell(letter));
            }
            row++;
        }
        return words;
    }

    /**
     * Calculates the points for a given word.
     *
     * @param word the word to calculate points for
     * @return the points for the word
     */
    private int calculatePoints(String word) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int baseScore = calculateBaseScore(letter);
            score += baseScore;
        }
        return score;
    }

    /**
     * Calculates the base score for a given letter.
     *
     * @param letter the letter to calculate the base score for
     * @return the base score for the letter
     */
    private int calculateBaseScore(char letter) {
        return switch (Character.toUpperCase(letter)) {
            case 'A' -> 1;
            case 'E' -> 2;
            case 'I' -> 3;
            case 'O' -> 4;
            case 'U' -> 5;
            default -> 0;
        };
    }

    /**
     * Registers an empty view with the model.
     *
     * @param model the WSModel to register the view with
     */
    private void registerEmptyView(WSModel model) {
        model.registerView();
    }
}
