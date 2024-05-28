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

    private void createStartMenu() {
        Label titleLabel = new Label("Word Search Game");
        titleLabel.setStyle("-fx-font-size: 24px;");

        Button newGameButtonNoDiagonals = new Button("Novo Jogo (Sem Diagonais)");
        Button newGameButtonWithDiagonals = new Button("Novo Jogo (Com Diagonais)");
        Button exitButton = new Button("Sair");

        newGameButtonNoDiagonals.setOnAction(event -> startNewGame(false));
        newGameButtonWithDiagonals.setOnAction(event -> startNewGame(true));
        exitButton.setOnAction(event -> primaryStage.close());

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(newGameButtonNoDiagonals, newGameButtonWithDiagonals, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(20);
        mainLayout.getChildren().addAll(titleLabel, buttonBox);
        mainLayout.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(mainLayout);

        initialScene = new Scene(root, 600, 400);
        initialScene.setFill(Color.TRANSPARENT);
        root.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-width: 2;");
    }

    private void startNewGame(boolean withDiagonals) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um ficheiro");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de texto", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            WSModel wsModel = new WSModel(selectedFile.getAbsolutePath(), withDiagonals);
            WSBoard wsBoard = new WSBoard(wsModel);

            wsModel.registerView();
            wsBoard.requestFocus();

            Button endGameButton = getEndButton(wsModel);

            Button newGameButtonNoDiagonals = new Button("Novo Jogo (Sem DIagonais)");
            newGameButtonNoDiagonals.setOnAction(event -> startNewGame(false));
            Button newGameButtonWithDiagonals = new Button("Novo Jogo (Com Diagonais)");
            newGameButtonWithDiagonals.setOnAction(event -> startNewGame(true));

            Label titleLabel = new Label("Word Search Game");

            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(titleLabel, newGameButtonNoDiagonals, newGameButtonWithDiagonals, endGameButton);
            buttonBox.setAlignment(Pos.CENTER);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(buttonBox);
            borderPane.setCenter(wsBoard);

            primaryStage.setScene(new Scene(borderPane));
            setFullScreen(primaryStage);
        } else {
            System.out.println("No file selected");
        }
    }

    private Button getEndButton(WSModel wsModel) {
        Button endGameButton = new Button("Terminar Jogo");
        endGameButton.setOnAction(event -> {
            String scoreMessage = wsModel.getScoreMessage();
            wsModel.writeScoreToFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(scoreMessage);
            alert.showAndWait();
            primaryStage.setScene(initialScene);
        });
        return endGameButton;
    }

    private void setFullScreen(Stage stage) {
        stage.setFullScreenExitHint(" ");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
