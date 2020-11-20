package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects the JavaFX program to the MariaDB database, using my account
 */
public class DatabaseConnector {
    // public so AddressController can check if null or not
    public static Connection connection = null;

    /**
     * Connect to database
     * @return
     */
    public static Connection connect() {
        // Server address from user input
        String address = AddressController.address;
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://" + address + ":3306/cigars?useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "bob", "123");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    /**
     * Disconnect from the database
     */
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
