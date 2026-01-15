package model;

public class Login {
    // User authentication credentials and context
    private String userId;        // Unique user identifier
    private String password;      // Authentication password (simplified to user ID)
    private String role;          // User role classification
    private Object userObject;    // Associated user entity object

    // Constructor with complete authentication details
    public Login(String userId, String password, String role, Object userObject) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.userObject = userObject;
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Accessor methods for authentication data
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Object getUserObject() { return userObject; }
    
    // Simplified authentication using ID as password
    public boolean authenticate(String inputPassword) {
        return password.equals(inputPassword);
    }
}