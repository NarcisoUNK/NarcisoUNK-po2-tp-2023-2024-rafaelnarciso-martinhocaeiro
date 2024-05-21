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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.ipbeja.app.model.WSModel;

import java.io.File;

/**
 * Start a game with a hardcoded board
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    @Override
    public void start(Stage primaryStage) {
        initializeGame(primaryStage);
    }

    private void initializeGame(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um ficheiro");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de texto", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            WSModel wsModel = new WSModel(selectedFile.getAbsolutePath());
            WSBoard wsBoard = new WSBoard(wsModel, primaryStage);

            wsModel.registerView(wsBoard);
            wsBoard.requestFocus(); // to remove focus from first button

            // Criar botão "Iniciar Novo Jogo"
            Button newGameButton = getNewGameButton(primaryStage);

            // Criar botão "Terminar Jogo"
            Button endGameButton = getEndGameButton(primaryStage, wsModel);

            // Adicionar os botões ao topo da janela
            HBox hbox = new HBox();
            hbox.getChildren().addAll(new Label("Word Search Game"), newGameButton, endGameButton);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(10);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(hbox); // Definir o HBox como o topo do BorderPane
            borderPane.setCenter(wsBoard); // Definir o WSBoard como o centro do BorderPane

            primaryStage.setScene(new Scene(borderPane));
            primaryStage.setTitle("Word Search Game"); // Definir o título da janela

            // Configurar fullscreen
            //setFullScreen(primaryStage);

            primaryStage.show();
        } else {
            // Handle the case where no file was selected (optional)
            System.out.println("No file selected");
            primaryStage.close();
        }
    }

    private void setFullScreen(Stage stage) {
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // opcional: remova a mensagem de saída do modo de tela cheia
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // opcional: desativa a tecla de saída do modo de tela cheia
    }


    private static Button getEndGameButton(Stage primaryStage, WSModel wsModel) {
        Button endGameButton = new Button("Terminar Jogo");
        endGameButton.setOnAction(event -> {
            String scoreMessage = wsModel.getScoreMessage();
            wsModel.writeScoreToFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(scoreMessage);
            alert.showAndWait();
            primaryStage.close(); // Fechar a janela quando o botão for clicado
        });
        return endGameButton;
    }

    private Button getNewGameButton(Stage primaryStage) {
        Button newGameButton = new Button("Iniciar Novo Jogo");
        newGameButton.setOnAction(event -> {
            // Reinicializar o jogo
            initializeGame(primaryStage);
        });
        return newGameButton;
    }

    /**
     * @param args not used
     */
    public static void main(String[] args) {
        launch(args);
    }
}
