package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controls the form that handles database I/O
 */
public class DatabaseController {
    @FXML private TextField searchTextField;
    @FXML private ToggleGroup toggleGroup;
    @FXML private RadioButton insertIntoRadioBtn;
    @FXML private RadioButton updateRadioBtn;
    @FXML private RadioButton deleteRadioBtn;

    @FXML private TableView<Cigar> tableView;
    @FXML private TableColumn<Cigar, String> cigarIdColumn;
    @FXML private TableColumn<Cigar, String> userIdColumn;
    @FXML private TableColumn<Cigar, String> brandColumn;
    @FXML private TableColumn<Cigar, String> priceColumn;

    @FXML
    private TextField cigarIdTextField;

    @FXML
    private TextField userIdTextField;

    @FXML
    private TextField brandTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button insertIntoBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private Text errorMessage;

    // From loginController, the PK of the user that is logged in
    private String userId;

    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;


    public void setFK(String userId) {
        this.userId = userId;
    }

    /**
     * Allows the insert button to function correctly and uses SQL in order to execute the action (if possible)
     *
     */
    @FXML
    public void insertBtnAction(ActionEvent actionEvent) {
        if (brandTextField.getText() != null && !brandTextField.getText().isEmpty()
        && priceTextField.getText() != null && !priceTextField.getText().isEmpty()) {
            Cigar cigar = new Cigar(null, null, null, null);

            cigar.setBrand(brandTextField.getText());
            cigar.setPrice(priceTextField.getText());

            try {
                PreparedStatement preparedStatement =
                        DatabaseConnector.connect().prepareStatement("INSERT INTO cigars VALUES(?,?,?,?)");

                preparedStatement.setString(1, null);
                preparedStatement.setString(2, userId);
                preparedStatement.setString(3, cigar.getBrand());
                preparedStatement.setString(4, cigar.getPrice());
                preparedStatement.executeUpdate();
                preparedStatement.close();

                DatabaseConnector.disconnect();
                initialize();

                errorMessage.setText("");


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            errorMessage.setText("Please complete all fields");
        }
    }

    /**
     * Initialize the tableview and fill it with data
     */
    @FXML
    private void initialize() {
        // Init the tableview with four columns
        cigarIdColumn.setCellValueFactory(cellData -> cellData.getValue().cigarIdProperty());
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        setVisibleItems();

        Platform.runLater(() -> {
            // Fill the tableview with data from observableList
            try {
                System.out.println("User ID is: " + userId);
                tableView.setItems(getCigarData());
                buildCigar();
                sortFilterTableView();
                observableRadioButtonChanges();
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    /**
     * Sets the insert button as visible by default
     */
    private void setVisibleItems() {
        Toggle toggle = toggleGroup.getSelectedToggle();
        System.out.println("Radio button selected: " + toggle);

        if (toggle == null) {
            // set what the user sees and doesn't see
            insertIntoBtn.setVisible(true);
            insertIntoBtn.setManaged(true);

            updateBtn.setVisible(false);
            updateBtn.setManaged(false);

            deleteBtn.setVisible(false);
            deleteBtn.setManaged(false);

            insertIntoRadioBtn.setSelected(true);
        }
    }

    /**
     * Sets the visible buttons/text-fields to match the radio button selections
     */
    private void observableRadioButtonChanges() {
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            // Cast object to Radio Button
            RadioButton radioButton = (RadioButton) newValue.getToggleGroup().getSelectedToggle();

            if (radioButton.getText().contains("Insert Into")) {
                insertIntoBtn.setVisible(true);
                insertIntoBtn.setManaged(true);

                updateBtn.setVisible(false);
                updateBtn.setManaged(false);

                deleteBtn.setVisible(false);
                deleteBtn.setManaged(false);

                brandTextField.setVisible(true);
                brandTextField.setManaged(true);

                priceTextField.setVisible(true);
                priceTextField.setManaged(true);
            }
            else if (radioButton.getText().contains("Update")) {
                insertIntoBtn.setVisible(false);
                insertIntoBtn.setManaged(false);

                updateBtn.setVisible(true);
                updateBtn.setManaged(true);

                deleteBtn.setVisible(false);
                deleteBtn.setManaged(false);

                brandTextField.setVisible(true);
                brandTextField.setManaged(true);

                priceTextField.setVisible(true);
                priceTextField.setManaged(true);
            }
            else {
                insertIntoBtn.setVisible(false);
                insertIntoBtn.setManaged(false);

                updateBtn.setVisible(false);
                updateBtn.setManaged(false);

                deleteBtn.setVisible(true);
                deleteBtn.setManaged(true);

                brandTextField.setVisible(false);
                brandTextField.setManaged(false);

                priceTextField.setVisible(false);
                priceTextField.setManaged(false);
            }
        });
    }

    /**
     * Ensures that the tableview only has matches to the searchTextField's value
     * @throws SQLException
     */
    private void sortFilterTableView() throws SQLException {
        FilteredList<Cigar> filteredList = new FilteredList<>(getCigarData(), p -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(cigar -> {
                // if search text is empty display all cigars
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (cigar.getBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (cigar.getPrice().toLowerCase().contains((lowerCaseFilter))) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Cigar> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    /**
     * Builds a cigar inside the tableview from a cigar object
     */
    private void buildCigar() {
        tableView.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getClickCount() > 0) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    Cigar cigar = tableView.getSelectionModel().getSelectedItem();
                    cigarIdTextField.setText(cigar.getCigarId());
                    userIdTextField.setText(cigar.getUserId());
                    brandTextField.setText(cigar.getBrand());
                    priceTextField.setText(cigar.getPrice());
                }
            }
        });
    }

    /**
     *
     * @return an observable array list of cigars from an SQL query
     * @throws SQLException
     */
    private ObservableList<Cigar> getCigarData() throws SQLException {
        connection = DatabaseConnector.connect();
        preparedStatement = connection.prepareStatement("SELECT * FROM cigars WHERE account_id = ?");
        preparedStatement.setString(1, userId);
        resultSet = preparedStatement.executeQuery();

        ObservableList<Cigar> cigarList = FXCollections.observableArrayList();

        while(resultSet.next()) {
            String cigarId = resultSet.getString(1);
            String userId = resultSet.getString(2);
            String brand = resultSet.getString(3);
            String price = resultSet.getString(4);

            // Construct Cigar objects
            Cigar cigar = new Cigar(cigarId, userId, brand, price);

            // Add the cigar to the list
            cigarList.add(cigar);
        }

        DatabaseConnector.disconnect();

        return cigarList;
    }

    /**
     * Updates the SQL database using the form's input fields for data
     * @param actionEvent is never used
     */
    public void updateBtnAction(ActionEvent actionEvent) {
        if (brandTextField.getText() != null && !brandTextField.getText().isEmpty()
        && priceTextField.getText() != null && !priceTextField.getText().isEmpty()){
            Cigar cigar = new Cigar(null, null, null, null);

            cigar.setCigarId(cigarIdTextField.getText());
            cigar.setUserId(userIdTextField.getText());
            cigar.setBrand(brandTextField.getText());
            cigar.setPrice(priceTextField.getText());

            try {
                PreparedStatement preparedStatement =
                        DatabaseConnector.connect().prepareStatement("UPDATE cigars SET brand = ?, price = ? WHERE cigar_id = ?");
                preparedStatement.setString(1, cigar.getBrand());
                preparedStatement.setString(2, cigar.getPrice());
                preparedStatement.setString(3, cigar.getCigarId());
                preparedStatement.executeUpdate();
                preparedStatement.close();

                DatabaseConnector.disconnect();
                initialize();

                errorMessage.setText("");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            errorMessage.setText("Please complete all fields");
        }
    }

    /**
     * Deletes the selected Cigar from the database using SQL
     * @param actionEvent
     */
    public void deleteBtnAction(ActionEvent actionEvent) {
        if (cigarIdTextField.getText() != null && !cigarIdTextField.getText().isEmpty()) {
            Cigar cigar = new Cigar(null, null, null, null);

            cigar.setCigarId(cigarIdTextField.getText());
            cigar.setUserId(userIdTextField.getText());
            cigar.setBrand(brandTextField.getText());
            cigar.setPrice(priceTextField.getText());

            try {

                PreparedStatement preparedStatement =
                        DatabaseConnector.connect().prepareStatement("DELETE FROM cigars WHERE cigar_id = ?");
                preparedStatement.setString(1, cigar.getCigarId());
                preparedStatement.executeUpdate();
                preparedStatement.close();

                DatabaseConnector.disconnect();
                initialize();

                errorMessage.setText("");

                brandTextField.setText("");
                priceTextField.setText("");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            errorMessage.setText("Please select a cigar from the table");
        }
    }
}
