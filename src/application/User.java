package application;

/**
 * Represents a user as an object
 */
public class User {
    // Attributes
    private String userId;
    private String username;
    private String password;

    // Methods
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
