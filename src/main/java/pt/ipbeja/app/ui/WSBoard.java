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

public class WSBoard extends BorderPane implements WSView {
    private final WSModel wsModel;
    private static final int SQUARE_SIZE = 80;
    private Button firstButtonClicked;
    private final TextArea movesTextArea;
    private final Label bonusScoreLabel = new Label("Score: 0");  // Initialize with default text
    private final Label wordsListLabel;  // Label to display the list of words

    public WSBoard(WSModel wsModel) {
        this.wsModel = wsModel;
        this.movesTextArea = new TextArea();
        this.wordsListLabel = new Label(wsModel.generateWordsList());  // Initialize with the words list
        this.buildGUI();
    }

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

        VBox rightPane = new VBox();
        rightPane.getChildren().addAll(new Label("Jogadas Efetuadas"), movesTextArea, bonusScoreLabel);
        rightPane.setSpacing(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setAlignment(Pos.TOP_LEFT);

        VBox leftPane = new VBox();  // Add a VBox for the left pane
        leftPane.getChildren().addAll(wordsListLabel);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_LEFT);

        this.setCenter(gridPane);
        this.setRight(rightPane);
        this.setLeft(leftPane);  // Add the left pane to the border pane
        this.requestFocus();
    }

    private Button createButton(int line, int col) {
        String textForButton = this.wsModel.textInPosition(new Position(line, col));
        Button button = new Button(textForButton);
        button.setMinWidth(SQUARE_SIZE);
        button.setMinHeight(SQUARE_SIZE);
        button.setOnAction(event -> handleButtonClick(button));

        return button;
    }

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

    private Position getPositionOfButton(Button button) {
        int row = GridPane.getRowIndex(button);
        int col = GridPane.getColumnIndex(button);
        return new Position(row, col);
    }

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
            int rowIncrement = firstPosition.line() < secondPosition.line() ? 1 : -1;
            int colIncrement = (diagonalDirection == 0) ? 1 : -1;
            int row = firstPosition.line();
            int col = firstPosition.col();
            while (row != secondPosition.line() + rowIncrement && col != secondPosition.col() + colIncrement) {
                Button button = getButton(row, col);
                Cell cell = wsModel.getCell(new Position(row, col));
                if (cell instanceof BonusCell) {
                    button.setStyle("-fx-background-color: orange");
                } else {
                    button.setStyle("-fx-background-color: lightgreen");
                }
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
                    button.setStyle("-fx-background-color: orange");
                } else {
                    button.setStyle("-fx-background-color: lightgreen");
                }
                wordBuilder.append(button.getText());
                positionsBuilder.append(String.format("(%d, %s) -> %s\n", firstPosition.line() + 1, (char) ('A' + col), button.getText()));
            }
        } else {
            for (int row = minRow; row <= maxRow; row++) {
                Button button = getButton(row, firstPosition.col());
                Cell cell = wsModel.getCell(new Position(row, firstPosition.col()));
                if (cell instanceof BonusCell) {
                    button.setStyle("-fx-background-color: orange");
                } else {
                    button.setStyle("-fx-background-color: lightgreen");
                }
                wordBuilder.append(button.getText());
                positionsBuilder.append(String.format("(%d, %s) -> %s\n", row + 1, (char) ('A' + firstPosition.col()), button.getText()));
            }
        }

        String foundWord = wordBuilder.toString();
        positionsBuilder.append(String.format("\"%s\" (%d, %s) to (%d, %s)\n",
                foundWord, firstPosition.line() + 1, (char) ('A' + firstPosition.col()),
                secondPosition.line() + 1, (char) ('A' + secondPosition.col())));

        movesTextArea.appendText(positionsBuilder.toString());
        movesTextArea.setScrollTop(Double.MAX_VALUE);

        // Update the label text to remove the found word
        String currentLabelText = wordsListLabel.getText();
        String updatedLabelText = currentLabelText.replace(foundWord, "").trim();
        wordsListLabel.setText(updatedLabelText);

        // Update the score label
        bonusScoreLabel.setText("Score: " + wsModel.getTotalScore());

        firstButtonClicked = null;
    }



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
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(messageToUI.message());
            alert.showAndWait();
            System.exit(0);
        }
    }

    public Button getButton(int line, int col) {
        GridPane gridPane = (GridPane) this.getCenter(); // Assuming buttons are added to the center of BorderPane
        return (Button) gridPane.getChildren().get(line * wsModel.nCols() + col);
    }
}
