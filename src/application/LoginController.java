package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles the login form
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text errorMessage;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    /**
     * Gets a user from the database
     * @return
     */
    private User getUser() {
        User user = new User();

        try {
            connection = DatabaseConnector.connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?");
            preparedStatement.setString(1, usernameField.getText());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user.setUserId(resultSet.getString(1));
                user.setUsername(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
            }
            resultSet.close();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            errorMessage.setText("Can't get user from database");
        }

        return user;
    }

    /**
     * Checks the password against the BCrypt hash of the password
     * @param user
     * @return
     */
    private Boolean checkPassword(User user) {
        if (BCrypt.checkpw(passwordField.getText(), user.getPassword())) {
            System.out.println("Password matches");
            return true;
        }
        else {
            System.out.println("Password does not match");
            return false;
        }
    }

    /**
     * Loads the create account form
     * @param actionEvent
     */
    public void createAccountButtonAction(ActionEvent actionEvent) {
        try {
            Node node = (Node) actionEvent.getSource();
            Stage loginStage = (Stage) node.getScene().getWindow();
            loginStage.close();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(getClass().getResource("AccountScene.fxml"));
            Parent root = (Parent)fxmlLoader.load();

            // Open the accounts scene
            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();;
            System.out.println("Can't open accounts scene");
            errorMessage.setText("Can't open accounts window");
        }
    }

    /**
     * Attempts to start the databaseScene
     * @param actionEvent
     */
    public void loginButtonAction(ActionEvent actionEvent) {
        User user = getUser();

        if (usernameField.getText().equals((user.getUsername()))
        && (!usernameField.getText().isEmpty())
        && (checkPassword(user))) {
            try {
                // Close the login scene
                Node node = (Node) actionEvent.getSource();
                Stage loginStage = (Stage) node.getScene().getWindow();
                loginStage.close();

                FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("DatabaseScene.fxml")));
                Parent root = (Parent) fxmlLoader.load();

                DatabaseController databaseController = fxmlLoader.<DatabaseController>getController();
                databaseController.setFK(user.getUserId());

                // open the database scene
                Stage stage = new Stage();
                stage.setTitle("Cigars database");
                stage.setScene(new Scene(root));
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can't open cigars database scene");
                errorMessage.setText("Can't open cigars database window");
            }
        }
        else {
            errorMessage.setText("Invalid username or password");
        }
    }
}
