package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.JavaFXInitializer;
import pt.ipbeja.app.model.RegularCell;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {

    private static final String BASE_PATH = "src/main/java/pt/ipbeja/app/words.txt";

    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.initialize();
    }

    @Test
    void testWordFound() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        String word = words.get(0);
        Position firstPosition = new Position(0, 0);
        Position lastPosition = getLastPosition(firstPosition, word.length() - 1);

        assertTrue(model.isFirstAndLastOfWord(firstPosition, lastPosition));
    }

    @Test
    void testWordWithWildcardFound() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        for (String word : words) {
            int expectedPoints = calculatePoints(word);
            assertEquals(word + " = " + expectedPoints + " pontos", model.wordFound(word, 0, 0, true, false, 0));
        }
    }


    @Test
    void testAllWordsWereFound() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        List<String> words = setupGridFromFile(model);

        for (String word : words) {
            model.wordFound(word, 0, 0, true, false, 0);
        }

        assertTrue(model.allWordsWereFound());
    }


    private Position getLastPosition(Position firstPosition, int wordLength) {
        return new Position(firstPosition.line(), firstPosition.col() + wordLength);
    }


    @Test
    void testIsDiagonalValid() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(1, 1);

        assertTrue(model.isDiagonalValid(firstPosition, lastPosition));
    }

    @Test
    void testIsLineValid() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isLineValid(firstPosition, lastPosition));
    }

    @Test
    void testIsColumnValid() throws IOException {
        WSModel model = new WSModel(BASE_PATH, false);
        registerEmptyView(model);
        setupGridFromFile(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(3, 0);

        assertTrue(model.isColumnValid(firstPosition, lastPosition));
    }

    private List<String> readWordsFromFile() throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WSModelTest.BASE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }
        return words;
    }

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

    private int calculatePoints(String word) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            int baseScore = calculateBaseScore(letter);
            score += baseScore;
        }
        return score;
    }

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

    private void registerEmptyView(WSModel model) {
        model.registerView();
    }
}
