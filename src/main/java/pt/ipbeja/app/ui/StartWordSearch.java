    package pt.ipbeja.app.ui;

    import javafx.application.Application;
    import javafx.scene.Scene;
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

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecione um ficheiro");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de texto", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                WSModel WSModel = new WSModel(selectedFile.getAbsolutePath());
                WSBoard WSBoard = new WSBoard(WSModel);
                primaryStage.setScene(new Scene(WSBoard));

                WSModel.registerView(WSBoard);
                WSBoard.requestFocus(); // to remove focus from first button
                primaryStage.show();
            } else {
                // Handle the case where no file was selected (optional)
                System.out.println("No file selected");
                primaryStage.close();
            }
        }

        /**
         * @param args not used
         */
        public static void main(String[] args) {
            Application.launch(args);
        }
    }
