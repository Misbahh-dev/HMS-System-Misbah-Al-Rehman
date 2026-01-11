package controller;

import model.Login;
import model.LoginRepository;
import view.LoginView;
import view.MainFrame;
import javax.swing.*;

public class LoginController {
    private LoginView view;
    private LoginRepository repository;
    private boolean isAuthenticated = false;
    private String currentUserRole;
    private Object currentUser;
    private String currentUserId; // ADDED: Store userId at class level
    
    // Store references to main controllers (will be set from Main.java)
    private PatientController patientController;
    private ClinicianController clinicianController;
    private AppointmentController appointmentController;
    private PrescriptionController prescriptionController;
    private ReferralController referralController;

    public LoginController(LoginView view, LoginRepository repository) {
        this.view = view;
        this.repository = repository;
        initController();
    }
    
    // NEW METHOD: Set main controllers (called from Main.java)
    public void setMainControllers(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc) {
        
        this.patientController = pc;
        this.clinicianController = cc;
        this.appointmentController = ac;
        this.prescriptionController = prc;
        this.referralController = rc;
    }

    private void initController() {
        view.setLoginListener(e -> performLogin());
    }

    private void performLogin() {
        String userId = view.getUserId();
        String password = view.getPassword();
        String selectedRole = view.getSelectedRole();

        if (userId.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            view.showMessage("Please fill all fields!", true);
            return;
        }

        Login user = repository.authenticate(userId, password);
        
        if (user != null) {
            if (user.getRole().equalsIgnoreCase(selectedRole)) {
                isAuthenticated = true;
                currentUserRole = user.getRole();
                currentUser = user.getUserObject();
                currentUserId = userId; // STORE userId at class level
                
                view.showMessage("Login successful! Welcome " + userId, false);
                
                // NEW: Close login window and open MainFrame
                SwingUtilities.invokeLater(() -> {
                    // Get the login window (JFrame) that contains this view
                    JFrame loginWindow = (JFrame) SwingUtilities.getWindowAncestor(view);
                    
                    // Show success message
                    JOptionPane.showMessageDialog(loginWindow,
                        "Welcome " + currentUserRole.toUpperCase() + "!\n" +
                        "User ID: " + currentUserId + "\n" + // CHANGED: userId to currentUserId
                        "Opening main application...",
                        "Login Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Close login window
                    if (loginWindow != null) {
                        loginWindow.dispose();
                    }
                    
                    // Open MainFrame
                    openMainApplication();
                });
                
            } else {
                view.showMessage("Role mismatch! Selected: " + selectedRole + ", Actual: " + user.getRole(), true);
            }
        } else {
            view.showMessage("Invalid credentials! Try ID as password.", true);
        }
    }

    // NEW METHOD: Open main application after successful login
    private void openMainApplication() {
        if (patientController == null || clinicianController == null || 
            appointmentController == null || prescriptionController == null || 
            referralController == null) {
            
            JOptionPane.showMessageDialog(null,
                "Error: Main controllers not set up properly.",
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create and show MainFrame with user role
        MainFrame mainFrame = new MainFrame(
            patientController,
            clinicianController,
            appointmentController,
            prescriptionController,
            referralController,
            currentUserRole  // Pass user role for access control
        );
        
        mainFrame.setVisible(true);
        
        // Optional: Show welcome message in main app
        JOptionPane.showMessageDialog(mainFrame,
            "Welcome to Healthcare Management System!\n" +
            "Role: " + currentUserRole.toUpperCase() + "\n" +
            "User ID: " + currentUserId + // CHANGED: userId to currentUserId
            "\n\nAccess Level: " + getAccessDescription(currentUserRole),
            "HMS Dashboard", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // NEW METHOD: Describe access level based on role
    private String getAccessDescription(String role) {
        switch (role.toLowerCase()) {
            case "patient":
                return "Can view and manage own appointments and prescriptions";
            case "clinician":
                return "Can manage patients, appointments, prescriptions, and referrals";
            case "staff":
                return "Full administrative access to all modules";
            case "admin":
                return "Full system administrator privileges";
            default:
                return "Standard user access";
        }
    }
    
    // Getter for the view (required)
    public LoginView getView() {
        return view;
    }
    
    // Getters for authentication state
    public boolean isAuthenticated() { 
        return isAuthenticated; 
    }
    
    public String getCurrentUserRole() { 
        return currentUserRole; 
    }
    
    public Object getCurrentUser() { 
        return currentUser; 
    }
    
    // Optional: Method to logout (if you add logout functionality later)
    public void logout() {
        isAuthenticated = false;
        currentUserRole = null;
        currentUser = null;
        currentUserId = null; // ADDED: Clear userId on logout
        view.clearFields();
        view.showMessage("Logged out successfully.", false);
    }
}