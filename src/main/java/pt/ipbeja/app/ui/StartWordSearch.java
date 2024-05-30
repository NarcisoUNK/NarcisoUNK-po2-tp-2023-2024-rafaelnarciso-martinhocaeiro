package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.ipbeja.app.model.WSModel;

import java.io.File;

/**
 * StartWordSearch class.
 * The main UI for the word search application.
 *
 * @version 30/05/2024
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class StartWordSearch extends Application {

    private Stage primaryStage;
    private Scene initialScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createStartMenu();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(initialScene);
        primaryStage.show();
    }

    /**
     * Creates the start menu.
     */
    private void createStartMenu() {
        VBox buttonBox = createStartMenuButtonBox();
        VBox mainLayout = createMainLayout(buttonBox);
        BorderPane root = createRootPane(mainLayout);

        initialScene = new Scene(root, 400, 300);
        initialScene.setFill(Color.TRANSPARENT);
        styleRootPane(root);
    }

    /**
     * Creates the button box for the start menu.
     *
     * @return a VBox containing the start menu buttons
     */
    private VBox createStartMenuButtonBox() {
        Button newGameButtonNoDiagonals = new Button("Novo Jogo (Sem Diagonais)");
        Button newGameButtonWithDiagonals = new Button("Novo Jogo (Com Diagonais)");
        Button exitButton = new Button("Sair");

        newGameButtonNoDiagonals.setOnAction(event -> startNewGame(false));
        newGameButtonWithDiagonals.setOnAction(event -> startNewGame(true));
        exitButton.setOnAction(event -> primaryStage.close());

        VBox buttonBox = new VBox(20);
        buttonBox.getChildren().addAll(newGameButtonNoDiagonals, newGameButtonWithDiagonals, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }

    /**
     * Creates the main layout for the start menu.
     *
     * @param buttonBox the VBox containing the start menu buttons
     * @return a VBox containing the main layout
     */
    private VBox createMainLayout(VBox buttonBox) {
        Label titleLabel = new Label("Sopa de Letras");
        titleLabel.setStyle("-fx-font-size: 40px;");

        Label authorLabel = new Label("Feito por Martinho Caeiro (23917) e Rafael Narciso (24473)");
        authorLabel.setStyle("-fx-font-size: 10px;");

        VBox mainLayout = new VBox(20);
        mainLayout.getChildren().addAll(titleLabel, buttonBox, authorLabel);
        mainLayout.setAlignment(Pos.CENTER);

        return mainLayout;
    }

    /**
     * Creates the root pane for the start menu.
     *
     * @param mainLayout the VBox containing the main layout
     * @return a BorderPane containing the root pane
     */
    private BorderPane createRootPane(VBox mainLayout) {
        BorderPane root = new BorderPane();
        root.setCenter(mainLayout);
        return root;
    }

    /**
     * Styles the root pane.
     *
     * @param root the BorderPane to style
     */
    private void styleRootPane(BorderPane root) {
        root.setStyle("-fx-background-color: #B0B0B0; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-width: 2;");
    }

    /**
     * Starts a new game.
     *
     * @param withDiagonals true if the game should include diagonal words, false otherwise
     */
    private void startNewGame(boolean withDiagonals) {
        File selectedFile = selectFile();

        if (selectedFile != null) {
            WSModel wsModel = new WSModel(selectedFile.getAbsolutePath(), withDiagonals);
            WSBoard wsBoard = new WSBoard(wsModel);

            wsModel.registerView();
            wsBoard.requestFocus();

            HBox buttonBox = createGameButtonBox(wsModel);
            VBox centerBox = createCenterBox(wsBoard);
            BorderPane borderPane = createGameBorderPane(buttonBox, centerBox);

            primaryStage.setScene(new Scene(borderPane));
            setFullScreen(primaryStage);
        } else {
            System.out.println("Nenhum ficheiro selecionado");
        }
    }

    /**
     * Opens a file chooser to select a file.
     *
     * @return the selected file, or null if no file was selected
     */
    private File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um ficheiro");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de texto", "*.txt"));
        return fileChooser.showOpenDialog(primaryStage);
    }

    /**
     * Creates the button box for the game scene.
     *
     * @param wsModel the WSModel for the game
     * @return an HBox containing the game buttons
     */
    private HBox createGameButtonBox(WSModel wsModel) {
        Button endGameButton = getEndButton(wsModel);

        Button newGameButtonNoDiagonals = new Button("Novo Jogo (Sem Diagonais)");
        newGameButtonNoDiagonals.setOnAction(event -> startNewGame(false));
        Button newGameButtonWithDiagonals = new Button("Novo Jogo (Com Diagonais)");
        newGameButtonWithDiagonals.setOnAction(event -> startNewGame(true));

        Label titleLabel = new Label("Sopa de Letras");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(titleLabel, newGameButtonNoDiagonals, newGameButtonWithDiagonals, endGameButton);
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }

    /**
     * Creates the center box for the game scene.
     *
     * @param wsBoard the WSBoard for the game
     * @return a VBox containing the center box
     */
    private VBox createCenterBox(WSBoard wsBoard) {
        VBox centerBox = new VBox(wsBoard);
        centerBox.setAlignment(Pos.CENTER);
        return centerBox;
    }

    /**
     * Creates the border pane for the game scene.
     *
     * @param buttonBox the HBox containing the game buttons
     * @param centerBox the VBox containing the center box
     * @return a BorderPane containing the game layout
     */
    private BorderPane createGameBorderPane(HBox buttonBox, VBox centerBox) {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(buttonBox);
        borderPane.setCenter(centerBox);
        borderPane.setStyle("-fx-background-color: #B0B0B0;");
        return borderPane;
    }

    /**
     * Creates the end game button.
     *
     * @param wsModel the WSModel for the game
     * @return a Button to end the game
     */
    private Button getEndButton(WSModel wsModel) {
        Button endGameButton = new Button("Terminar Jogo");
        endGameButton.setOnAction(event -> {
            String scoreMessage = wsModel.getScoreMessage();
            wsModel.writeScoreToFile();
            showAlert(scoreMessage);
            primaryStage.close();
        });
        return endGameButton;
    }

    /**
     * Shows an alert with a message.
     *
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de Jogo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets the stage to full screen.
     *
     * @param stage the Stage to set to full screen
     */
    private void setFullScreen(Stage stage) {
        stage.setFullScreenExitHint(" ");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
