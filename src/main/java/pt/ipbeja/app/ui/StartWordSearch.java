package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ipbeja.app.model.WSModel;

/**
 * Start a game with a hardcoded board
 * @author anonymized
 * @version 2024/04/14
 */
public class StartWordSearch extends Application {

    @Override
    public void start(Stage primaryStage) {

        WSModel WSModel = new WSModel("C:/Users/Narciso/Desktop/po2/TrabPO2/src/main/java/pt/ipbeja/app/text");
        WSBoard WSBoard = new WSBoard(WSModel);
        primaryStage.setScene(new Scene(WSBoard));

        WSModel.registerView(WSBoard);
        WSBoard.requestFocus(); // to remove focus from first button
        primaryStage.show();
    }

    /**
     * @param args  not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
