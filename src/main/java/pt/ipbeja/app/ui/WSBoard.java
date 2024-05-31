package pt.ipbeja.app.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pt.ipbeja.app.model.*;

/**
 * WSBoard class
 * Represents the user interface for the game.
 *
 * @version 31/05/2024 (Final)
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class WSBoard extends BorderPane implements WSView {
    private final WSModel wsModel; //Game model
    private static final int SQUARE_SIZE = 80; //Size of the buttons
    private Button firstButtonClicked; //First button that is clicked
    private final TextArea movesTextArea; //Text area for all the game moves
    private final Label bonusScoreLabel = new Label("Pontuação: 0");  // Initialize with default text
    private final Label wordsListLabel;  // Label to display the list of words

    /**
     * Constructor for WSBoard.
     * @param wsModel the model of the word search game
     */
    public WSBoard(WSModel wsModel) {
        this.wsModel = wsModel;
        this.movesTextArea = new TextArea();
        this.wordsListLabel = new Label(wsModel.generateWordsList());  // Initialize with the words list
        this.buildGUI();
    }

    /**
     * Builds the graphical user interface.
     */
    private void buildGUI() {
        assert (this.wsModel != null);

        GridPane gridPane = new GridPane();

        for (int line = 0; line < this.wsModel.nLines(); line++) {
            for (int col = 0; col < this.wsModel.nCols(); col++) {
                Button button = createButton(line, col);
                gridPane.add(button, col, line);
            }
        }

        movesTextArea.setEditable(false);
        movesTextArea.setPrefWidth(300);

        VBox rightPane = new VBox(); // Add a VBox for the right pane
        rightPane.getChildren().addAll(new Label("Jogadas Efetuadas:"), movesTextArea, bonusScoreLabel);
        rightPane.setSpacing(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setAlignment(Pos.TOP_LEFT);

        VBox leftPane = new VBox();  // Add a VBox for the left pane
        leftPane.getChildren().addAll(wordsListLabel);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_LEFT);

        this.setCenter(gridPane);
        this.setRight(rightPane); // Add the right pane to the border pane
        this.setLeft(leftPane);  // Add the left pane to the border pane
        this.requestFocus();
    }

    /**
     * Creates a button for a specific grid cell.
     * @param line the row of the cell
     * @param col the column of the cell
     * @return the created button
     */
    private Button createButton(int line, int col) {
        String textForButton = this.wsModel.textInPosition(new Position(line, col));
        Button button = new Button(textForButton);
        button.setMinWidth(SQUARE_SIZE);
        button.setMinHeight(SQUARE_SIZE);
        button.setOnAction(event -> handleButtonClick(button));

        return button;
    }

    /**
     * Handles button click events.
     * @param button the button that was clicked
     */
    private void handleButtonClick(Button button) {
        if (firstButtonClicked == null) {
            firstButtonClicked = button;
            firstButtonClicked.setStyle("-fx-background-color: yellow");
        } else if (button == firstButtonClicked) {
            firstButtonClicked.setStyle("");
            firstButtonClicked = null;
        } else {
            Position firstPosition = getPositionOfButton(firstButtonClicked);
            Position secondPosition = getPositionOfButton(button);

            if (wsModel.isFirstAndLastOfWord(firstPosition, secondPosition)) {
                highlightWord(firstButtonClicked, button);
            } else {
                firstButtonClicked.setStyle("");
            }

            firstButtonClicked = null;
        }
    }

    /**
     * Gets the position of a button in the grid.
     * @param button the button
     * @return the position of the button
     */
    private Position getPositionOfButton(Button button) {
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);
        return new Position(row, col);
    }

    /**
     * Highlights the word found between two buttons.
     * @param firstButton the first button
     * @param secondButton the second button
     */
    private void highlightWord(Button firstButton, Button secondButton) {
        Position firstPosition = getPositionOfButton(firstButton);
        Position secondPosition = getPositionOfButton(secondButton);

        int minRow = Math.min(firstPosition.line(), secondPosition.line());
        int maxRow = Math.max(firstPosition.line(), secondPosition.line());
        int minCol = Math.min(firstPosition.col(), secondPosition.col());
        int maxCol = Math.max(firstPosition.col(), secondPosition.col());

        StringBuilder wordBuilder = new StringBuilder();
        StringBuilder positionsBuilder = new StringBuilder();

        boolean isDiagonal = (maxRow - minRow) == (maxCol - minCol) || (maxRow - minRow) == (minCol - maxCol);
        int diagonalDirection = (firstPosition.col() < secondPosition.col()) ? 0 : 1;

        if (isDiagonal) {
            highlightDiagonalWord(firstPosition, secondPosition, wordBuilder, positionsBuilder, diagonalDirection);
        } else if (firstPosition.line() == secondPosition.line()) {
            highlightHorizontalWord(firstPosition, minCol, maxCol, wordBuilder, positionsBuilder);
        } else {
            highlightVerticalWord(firstPosition, minRow, maxRow, wordBuilder, positionsBuilder);
        }

        String foundWord = wordBuilder.toString();
        appendToMovesTextArea(positionsBuilder, foundWord, firstPosition, secondPosition);

        updateWordsListLabel(foundWord);

        updateScoreLabel();
    }

    /**
     * Highlights a diagonal word.
     * @param firstPosition the starting position
     * @param secondPosition the ending position
     * @param wordBuilder a StringBuilder for the word
     * @param positionsBuilder a StringBuilder for the positions
     * @param diagonalDirection the direction of the diagonal (0 for ↘, 1 for ↙)
     */
    private void highlightDiagonalWord(Position firstPosition, Position secondPosition, StringBuilder wordBuilder, StringBuilder positionsBuilder, int diagonalDirection) {
        int rowIncrement = firstPosition.line() < secondPosition.line() ? 1 : -1;
        int colIncrement = (diagonalDirection == 0) ? 1 : -1;
        int row = firstPosition.line();
        int col = firstPosition.col();
        while (row != secondPosition.line() + rowIncrement && col != secondPosition.col() + colIncrement) {
            Button button = getButton(row, col);
            Cell cell = wsModel.getCell(new Position(row, col));
            highlightButton(button, cell);
            wordBuilder.append(button.getText());
            appendPositionInfo(positionsBuilder, row, col, button.getText());
            row += rowIncrement;
            col += colIncrement;
        }
    }

    /**
     * Highlights a horizontal word.
     * @param firstPosition the starting position
     * @param minCol the minimum column
     * @param maxCol the maximum column
     * @param wordBuilder a StringBuilder for the word
     * @param positionsBuilder a StringBuilder for the positions
     */
    private void highlightHorizontalWord(Position firstPosition, int minCol, int maxCol, StringBuilder wordBuilder, StringBuilder positionsBuilder) {
        for (int col = minCol; col <= maxCol; col++) {
            Button button = getButton(firstPosition.line(), col);
            Cell cell = wsModel.getCell(new Position(firstPosition.line(), col));
            highlightButton(button, cell);
            wordBuilder.append(button.getText());
            appendPositionInfo(positionsBuilder, firstPosition.line(), col, button.getText());
        }
    }

    /**
     * Highlights a vertical word.
     * @param firstPosition the starting position
     * @param minRow the minimum row
     * @param maxRow the maximum row
     * @param wordBuilder a StringBuilder for the word
     * @param positionsBuilder a StringBuilder for the positions
     */
    private void highlightVerticalWord(Position firstPosition, int minRow, int maxRow, StringBuilder wordBuilder, StringBuilder positionsBuilder) {
        for (int row = minRow; row <= maxRow; row++) {
            Button button = getButton(row, firstPosition.col());
            Cell cell = wsModel.getCell(new Position(row, firstPosition.col()));
            highlightButton(button, cell);
            wordBuilder.append(button.getText());
            appendPositionInfo(positionsBuilder, row, firstPosition.col(), button.getText());
        }
    }

    /**
     * Highlights a button.
     * @param button the button to highlight
     * @param cell the cell associated with the button
     */
    private void highlightButton(Button button, Cell cell) {
        if (cell.getBonus() > 0) {
            button.setStyle("-fx-background-color: orange");
        } else {
            button.setStyle("-fx-background-color: lightgreen");
        }
    }

    /**
     * Appends position information to the StringBuilder.
     * @param positionsBuilder the StringBuilder for the positions
     * @param row the row
     * @param col the column
     * @param buttonText the text of the button
     */
    private void appendPositionInfo(StringBuilder positionsBuilder, int row, int col, String buttonText) {
        positionsBuilder.append(String.format("(%d, %s) -> %s\n", row + 1, (char) ('A' + col), buttonText));
    }

    /**
     * Appends the found word and positions to the moves text area.
     * @param positionsBuilder the StringBuilder for the positions
     * @param foundWord the found word
     * @param firstPosition the starting position
     * @param secondPosition the ending position
     */
    private void appendToMovesTextArea(StringBuilder positionsBuilder, String foundWord, Position firstPosition, Position secondPosition) {
        positionsBuilder.append(String.format("\"%s\" (%d, %s) to (%d, %s)\n",
                foundWord, firstPosition.line() + 1, (char) ('A' + firstPosition.col()),
                secondPosition.line() + 1, (char) ('A' + secondPosition.col())));
        movesTextArea.appendText(positionsBuilder.toString());
        movesTextArea.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Updates the words list label.
     * @param foundWord the found word
     */
    private void updateWordsListLabel(String foundWord) {
        String currentLabelText = wordsListLabel.getText();
        String updatedLabelText = currentLabelText.replace(foundWord, "").trim();
        wordsListLabel.setText(updatedLabelText);
    }

    /**
     * Updates the score label.
     */
    private void updateScoreLabel() {
        bonusScoreLabel.setText("Pontuação: " + wsModel.getTotalScore());
    }

    /**
     * Updates the view with new data.
     * @param messageToUI the message to update the UI
     */
    @Override
    public void update(MessageToUI messageToUI) {
        for (Position p : messageToUI.positions()) {
            String s = this.wsModel.textInPosition(p);
            Button button = getButton(p.line(), p.col());
            button.setText(s);
            button.setStyle("");
        }
        if (this.wsModel.allWordsWereFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fim de Jogo");
            alert.setHeaderText(null);
            alert.setContentText(messageToUI.message());
            alert.showAndWait();
            System.exit(0);
        }
    }

    /**
     * Gets the button at a specific position in the grid.
     * @param line the row
     * @param col the column
     * @return the button at the specified position
     */
    public Button getButton(int line, int col) {
        GridPane gridPane = (GridPane) this.getCenter();
        return (Button) gridPane.getChildren().get(line * wsModel.nCols() + col);
    }
}
