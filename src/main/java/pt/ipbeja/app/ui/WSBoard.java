package pt.ipbeja.app.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pt.ipbeja.app.model.MessageToUI;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.WSModel;
import pt.ipbeja.app.model.WSView;

import java.util.ArrayList;
import java.util.List;

/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @author anonymized
 * @version 2024/04/14
 */
public class WSBoard extends GridPane implements WSView {
    private final WSModel wsModel;
    private static final int SQUARE_SIZE = 80;
    private Button firstButtonClicked;
    private Button secondButtonClicked;

    /**
     * Create a board with letters
     */
    public WSBoard(WSModel wsModel) {
        this.wsModel = wsModel;
        this.buildGUI();
    }

    /**
     * Build the interface
     */
    private void buildGUI() {
        assert (this.wsModel != null);

        // create one label for each position
        for (int line = 0; line < this.wsModel.nLines(); line++) {
            for (int col = 0; col < this.wsModel.nCols(); col++) {
                Button button = createButton(line, col);
                this.add(button, col, line); // add button to GridPane
            }
        }
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
            secondButtonClicked = button;

            // Get the positions of the buttons
            Position firstPosition = getPositionOfButton(firstButtonClicked);
            Position secondPosition = getPositionOfButton(secondButtonClicked);

            // Check if the positions correspond to the beginning and end of a word
            if (wsModel.isFirstAndLastOfWord(firstPosition, secondPosition)) {
                highlightWord(firstButtonClicked, secondButtonClicked);
            } else {
                // Reset the first button style to normal
                firstButtonClicked.setStyle("");
            }

            // Reset the first and second button references
            firstButtonClicked = null;
            secondButtonClicked = null;
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

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                Button button = getButton(row, col);
                button.setStyle("-fx-background-color: lightgreen");
            }
        }
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
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param line line of label in board
     * @param col  column of label in board
     * @return the button at line, col
     */
    public Button getButton(int line, int col) {
        return (Button) this.getChildren().get(line * wsModel.nCols() + col);
    }
}
