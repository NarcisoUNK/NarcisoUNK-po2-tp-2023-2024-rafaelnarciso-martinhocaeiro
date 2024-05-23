package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.JavaFXInitializer;
import pt.ipbeja.app.model.RegularCell;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {

    private static final String BASE_PATH = "C:/Users/guiss/OneDrive/Ambiente de Trabalho/NarcisoUNK-po2-tp-2023-2024-rafaelnarciso-martinhocaeiro/src/main/resources/";

    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.initialize();
    }

    @Test
    void testWordFound() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('C'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('S'));
        model.setCell(0, 3, new RegularCell('A'));

        assertEquals("CASA = 4 pontos", model.wordFound("CASA", 0, 0, true, false, 0));
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('M'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('*'));
        model.setCell(0, 3, new RegularCell('A'));

        assertEquals("MALA", model.wordWithWildcardFound("MALA"));
    }

    @Test
    void testAllWordsWereFound() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('M'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('L'));
        model.setCell(0, 3, new RegularCell('A'));

        model.setCell(1, 0, new RegularCell('C'));
        model.setCell(1, 1, new RegularCell('A'));

        model.wordFound("MALA", 0, 0, true, false, 0);
        model.wordFound("CA", 1, 0, true, false, 0);

        assertTrue(model.allWordsWereFound());
    }

    @Test
    void testIsFirstAndLastOfWord() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('M'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('L'));
        model.setCell(0, 3, new RegularCell('A'));

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isFirstAndLastOfWord(firstPosition, lastPosition));
    }

    @Test
    void testIsDiagonalValid() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(1, 1);

        assertTrue(model.isDiagonalValid(firstPosition, lastPosition));
    }

    @Test
    void testIsLineValid() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isLineValid(firstPosition, lastPosition));
    }

    @Test
    void testIsColumnValid() {
        WSModel model = new WSModel(BASE_PATH + "words.txt");
        this.registerEmptyView(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(3, 0);

        assertTrue(model.isColumnValid(firstPosition, lastPosition));
    }

    private void registerEmptyView(WSModel model) {
        // Registering an empty view, no arguments needed
        model.registerView();
    }

}
