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

    private Stage primaryStage;
    private Scene initialScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Criar cena inicial
        createStartMenu();

        // Configurar e exibir a janela principal
        primaryStage.setTitle("Word Search Game");
        primaryStage.setScene(initialScene);
        primaryStage.show();
    }

    private void createStartMenu() {
        // Criar botões para a cena inicial
        Button newGameButton = new Button("Novo Jogo");
        Button exitButton = new Button("Sair");

        // Adicionar ação ao botão "Novo Jogo"
        newGameButton.setOnAction(event -> startNewGame());
        // Adicionar ação ao botão "Sair"
        exitButton.setOnAction(event -> primaryStage.close());

        // Layout para os botões
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(newGameButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Adicionar botões à barra de topo
        BorderPane root = new BorderPane();
        root.setTop(buttonBox);

        // Adicionar botões à cena inicial
        initialScene = new Scene(root, 200, 50);
    }

    private void startNewGame() {
        // Abrir seletor de arquivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um ficheiro");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de texto", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            WSModel wsModel = new WSModel(selectedFile.getAbsolutePath());
            WSBoard wsBoard = new WSBoard(wsModel, primaryStage);

            wsModel.registerView();
            wsBoard.requestFocus(); // to remove focus from first button

            // Criar botões "Terminar Jogo" e "Novo Jogo"
            Button endGameButton = getEndButton(wsModel);

            Button newGameButton = new Button("Novo Jogo");
            newGameButton.setOnAction(event -> startNewGame());

            // Criar título
            Label titleLabel = new Label("Word Search Game");

            // Layout para o título e os botões "Terminar Jogo" e "Novo Jogo"
            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(titleLabel, newGameButton, endGameButton);
            buttonBox.setAlignment(Pos.CENTER);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(buttonBox); // Adicionar título e botões "Terminar Jogo" e "Novo Jogo" na barra de topo
            borderPane.setCenter(wsBoard); // Definir o WSBoard como o centro do BorderPane

            primaryStage.setScene(new Scene(borderPane));
            // Configurar fullscreen
            setFullScreen(primaryStage);
        } else {
            // Handle the case where no file was selected (optional)
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
            primaryStage.setScene(initialScene); // Voltar para a cena inicial
        });
        return endGameButton;
    }


    private void setFullScreen(Stage stage) {
        stage.setFullScreenExitHint(" "); // opcional: remova a mensagem de saída do modo de tela cheia
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);// opcional: desativa a tecla de saída do modo de tela cheia
    }

    /**
     * @param args not used
     */
    public static void main(String[] args) {
        launch(args);
    }
}
