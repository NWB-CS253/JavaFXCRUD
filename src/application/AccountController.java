package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles the Account creation form
 */
public class AccountController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField retypePasswordField;

    @FXML
    private Text errorMessage;

    private Connection connection;
    private PreparedStatement preparedStatement;


    /**
     * Returns the user back to the login form
     * @param actionEvent is used to close the accountStage
     * @throws IOException
     */
    public void cancelButtonAction(ActionEvent actionEvent) throws IOException {
        // close the account scene
        Node node = (Node) actionEvent.getSource();
        Stage accountStage = (Stage) node.getScene().getWindow();
        accountStage.close();

        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("LoginScene.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();

    }

    /**
     * Attempts to create a new account
     * @param actionEvent
     * @throws IOException
     */
    public void createAccountButtonAction(ActionEvent actionEvent) throws IOException {
        if (passwordField.getText().equals(retypePasswordField.getText())
                && (!usernameField.getText().equals(""))
                && (!passwordField.getText().equals(""))){
            try {
                connection = DatabaseConnector.connect();
                String hashed = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());

                preparedStatement = connection.prepareStatement("INSERT INTO accounts (username, password) VALUES (?, ?);");
                preparedStatement.setString(1, usernameField.getText());
                preparedStatement.setString(2, hashed);
                preparedStatement.executeUpdate();
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            cancelButtonAction(actionEvent);
        }
        else {
            System.out.println("Failed to create a new account");
            errorMessage.setText("Failed to create a new account");
        }
    }
}
