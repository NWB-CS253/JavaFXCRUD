package application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;

/**
 * Starts the program
 */
public class Main extends Application {

    /**
     * Launches the program
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the program
     * @param stage
     * @throws IOException
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddressScene.fxml"));

        Parent root = (Parent) fxmlLoader.load();

        // Open the Address scene
        stage.setTitle("Server Address");
        stage.setScene(new Scene(root));
        stage.show();

    }
}
