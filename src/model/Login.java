package model;

public class Login {
    private String userId;
    private String password; // For simplicity, we'll use IDs as passwords
    private String role; // "patient", "clinician", "staff"
    private Object userObject; // The actual Patient/Clinician/Staff object

    public Login(String userId, String password, String role, Object userObject) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.userObject = userObject;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Object getUserObject() { return userObject; }
    
    // For simplicity, we'll use ID as password
    public boolean authenticate(String inputPassword) {
        return password.equals(inputPassword);
    }
}