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
import javafx.stage.Stage;
import pt.ipbeja.app.model.*;

/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @version 2024/04/14
 */
public class WSBoard extends BorderPane implements WSView {
    private final WSModel wsModel;
    private static final int SQUARE_SIZE = 80;
    private Button firstButtonClicked;
    private final Stage stage; // Reference to the main stage
    private final TextArea movesTextArea; // Text area to display the moves
    private final Label bonusScoreLabel = new Label();
    /**
     * Create a board with letters
     */
    public WSBoard(WSModel wsModel, Stage stage) {
        this.wsModel = wsModel;
        this.stage = stage;
        this.movesTextArea = new TextArea();
        this.buildGUI();
    }

    /**
     * Build the interface
     */
    private void buildGUI() {
        assert (this.wsModel != null);

        GridPane gridPane = new GridPane();

        // create one label for each position
        for (int line = 0; line < this.wsModel.nLines(); line++) {
            for (int col = 0; col < this.wsModel.nCols(); col++) {
                Button button = createButton(line, col);
                gridPane.add(button, col, line); // add button to GridPane
            }
        }

        movesTextArea.setEditable(false);
        movesTextArea.setPrefWidth(300);

        VBox rightPane = new VBox();
        rightPane.getChildren().addAll(new Label("Jogadas Efetuadas"), movesTextArea, bonusScoreLabel);
        rightPane.setSpacing(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setAlignment(Pos.TOP_LEFT);

        this.setCenter(gridPane);
        this.setRight(rightPane);
        this.requestFocus();
    }



    /**
     * Creates a button with a specific line and column
     *
     * @param line the line of the button
     * @param col  the column of the button
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
     * Handles the button click event
     *
     * @param button the clicked button
     */
    private void handleButtonClick(Button button) {
        if (firstButtonClicked == null) {
            firstButtonClicked = button;
            firstButtonClicked.setStyle("-fx-background-color: yellow");
        } else if (button == firstButtonClicked) {
            firstButtonClicked.setStyle(""); // Reset the first button style to normal
            firstButtonClicked = null; // Reset the first button reference
        } else {

            // Get the positions of the buttons
            Position firstPosition = getPositionOfButton(firstButtonClicked);
            Position secondPosition = getPositionOfButton(button);

            // Check if the positions correspond to the beginning and end of a word
            if (wsModel.isFirstAndLastOfWord(firstPosition, secondPosition)) {
                highlightWord(firstButtonClicked, button);
            } else {
                // Reset the first button style to normal
                firstButtonClicked.setStyle("");
            }

            // Reset the first and second button references
            firstButtonClicked = null;
        }
    }

    /**
     * Get the position of a button
     *
     * @param button the button
     * @return the position of the button
     */
    private Position getPositionOfButton(Button button) {
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);
        return new Position(row, col);
    }

    /**
     * Highlights the word in the UI by changing the background color
     *
     * @param firstButton  the button of the first letter
     * @param secondButton the button of the last letter
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
        int bonusScore = 0;

        boolean isDiagonal = (maxRow - minRow) == (maxCol - minCol) || (maxRow - minRow) == (minCol - maxCol);
        int diagonalDirection = (firstPosition.col() < secondPosition.col()) ? 0 : 1;

        if (isDiagonal) {
            int rowIncrement = firstPosition.line() < secondPosition.line() ? 1 : -1;
            int colIncrement = (diagonalDirection == 0) ? 1 : -1;
            int row = firstPosition.line();
            int col = firstPosition.col();
            while (row != secondPosition.line() + rowIncrement && col != secondPosition.col() + colIncrement) {
                Button button = getButton(row, col);
                Cell cell = wsModel.getCell(new Position(row, col));
                if (cell instanceof BonusCell) {
                    bonusScore += ((BonusCell) cell).getBonus();
                }
                button.setStyle("-fx-background-color: lightgreen");
                wordBuilder.append(button.getText());
                positionsBuilder.append(String.format("(%d, %s) -> %s\n", row + 1, (char) ('A' + col), button.getText()));
                row += rowIncrement;
                col += colIncrement;
            }
        } else if (firstPosition.line() == secondPosition.line()) {
            for (int col = minCol; col <= maxCol; col++) {
                Button button = getButton(firstPosition.line(), col);
                Cell cell = wsModel.getCell(new Position(firstPosition.line(), col));
                if (cell instanceof BonusCell) {
                    bonusScore += ((BonusCell) cell).getBonus();
                }
                button.setStyle("-fx-background-color: lightgreen");
                wordBuilder.append(button.getText());
                positionsBuilder.append(String.format("(%d, %s) -> %s\n", firstPosition.line() + 1, (char) ('A' + col), button.getText()));
            }
        } else {
            for (int row = minRow; row <= maxRow; row++) {
                Button button = getButton(row, firstPosition.col());
                Cell cell = wsModel.getCell(new Position(row, firstPosition.col()));
                if (cell instanceof BonusCell) {
                    bonusScore += ((BonusCell) cell).getBonus();
                }
                button.setStyle("-fx-background-color: lightgreen");
                wordBuilder.append(button.getText());
                positionsBuilder.append(String.format("(%d, %s) -> %s\n", row + 1, (char) ('A' + firstPosition.col()), button.getText()));
            }
        }

        String foundWord = wordBuilder.toString();
        positionsBuilder.append(String.format("\"%s\" (%d, %s) to (%d, %s)\n",
                foundWord, firstPosition.line() + 1, (char) ('A' + firstPosition.col()),
                secondPosition.line() + 1, (char) ('A' + secondPosition.col())));

        movesTextArea.appendText(positionsBuilder.toString());

        // Scroll to the end of the TextArea
        movesTextArea.setScrollTop(Double.MAX_VALUE);

        // Update the bonusScoreLabel
        bonusScoreLabel.setText("Bónus: " + bonusScore);
    }






    /**
     * Simply updates the text for the buttons in the received positions
     *
     * @param messageToUI the WS model
     */
    @Override
    public void update(MessageToUI messageToUI) {
        for (Position p : messageToUI.positions()) {
            String s = this.wsModel.textInPosition(p);
            Button button = getButton(p.line(), p.col());
            button.setText(s);
            button.setStyle(""); // Reset button style
        }
        if (this.wsModel.allWordsWereFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(messageToUI.message());
            alert.showAndWait();
            System.exit(0);
        }
    }

    /**
     * Handles the end game button click event
     */
    private void handleEndGameButtonClick() {
        this.wsModel.writeScoreToFile();
        String scoreMessage = this.wsModel.getScoreMessage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Terminated");
        alert.setHeaderText(null);
        alert.setContentText(scoreMessage);
        alert.showAndWait();
        this.stage.close(); // Close the application window
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param line line of label in board
     * @param col  column of label in board
     * @return the button at line, col
     */
    public Button getButton(int line, int col) {
        GridPane gridPane = (GridPane) this.getCenter(); // Assuming buttons are added to the center of BorderPane
        return (Button) gridPane.getChildren().get(line * wsModel.nCols() + col);
    }
}
