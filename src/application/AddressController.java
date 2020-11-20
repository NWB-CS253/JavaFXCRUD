package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Uses a form and databaseConnector to attempt to connect to a database
 */
public class AddressController {

    @FXML
    private TextField addressField;
    @FXML
    private Text errorMessage;

    // Server address used across classes
    public static String address;

    /**
     * Attempts to connect to the database and prints a message if it fails
     * @param actionEvent
     */
    public void addressButtonAction(ActionEvent actionEvent) {

        // Server address used across classes
        address = addressField.getText();
        DatabaseConnector.connect();

        if (DatabaseConnector.connection == null) {
            System.out.println("Connection failure");
            errorMessage.setText("Connection failure");
        }
        else {
            try {
                // close the Address Scene
                Node node = (Node) actionEvent.getSource();
                Stage addressStage = (Stage) node.getScene().getWindow();
                addressStage.close();

                FXMLLoader fxmlLoader =
                        new FXMLLoader(getClass().getResource("LoginScene.fxml"));
                Parent root = (Parent) fxmlLoader.load();

                // open the Login scene
                Stage stage = new Stage();
                stage.setTitle("Login");
                stage.setScene(new Scene(root));
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();

                System.out.println("Can't open login scene");
                errorMessage.setText("Can't open login window");
            }
        }
    }
}
