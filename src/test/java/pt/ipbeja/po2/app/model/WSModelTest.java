package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.JavaFXInitializer;
import pt.ipbeja.app.model.RegularCell;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {

    private static final String BASE_PATH = "src/main/java/pt/ipbeja/app/words.txt";

    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.initialize();
    }

    @Test
    void testWordFound() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('C'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('S'));
        model.setCell(0, 3, new RegularCell('A'));

        assertEquals("CASA = 2 pontos", model.wordFound("CASA", 0, 0, true, false, 0));
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('C'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('*'));
        model.setCell(0, 3, new RegularCell('A'));

        assertEquals("CASA", model.wordWithWildcardFound("CASA"));
    }

    @Test
    void testAllWordsWereFound() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        // Add letters manually to the grid
        model.setCell(0, 0, new RegularCell('C'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('S'));
        model.setCell(0, 3, new RegularCell('A'));

        model.setCell(1, 0, new RegularCell('C'));
        model.setCell(1, 1, new RegularCell('A'));
        model.setCell(1, 2, new RegularCell('R'));
        model.setCell(1, 3, new RegularCell('R'));
        model.setCell(1, 4, new RegularCell('O'));

        model.wordFound("CASA", 0, 0, true, false, 0);
        model.wordFound("CARRO", 1, 0, true, false, 0);

        assertTrue(model.allWordsWereFound());
    }

    @Test
    void testIsFirstAndLastOfWord() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        // Adicionar letras manualmente à grade
        model.setCell(0, 0, new RegularCell('C'));
        model.setCell(0, 1, new RegularCell('A'));
        model.setCell(0, 2, new RegularCell('S'));
        model.setCell(0, 3, new RegularCell('A'));

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isFirstAndLastOfWord(firstPosition, lastPosition));
    }


    @Test
    void testIsDiagonalValid() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(1, 1);

        assertTrue(model.isDiagonalValid(firstPosition, lastPosition));
    }

    @Test
    void testIsLineValid() {
        WSModel model = new WSModel(BASE_PATH);
        this.registerEmptyView(model);

        Position firstPosition = new Position(0, 0);
        Position lastPosition = new Position(0, 3);

        assertTrue(model.isLineValid(firstPosition, lastPosition));
    }

    @Test
    void testIsColumnValid() {
        WSModel model = new WSModel(BASE_PATH);
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
