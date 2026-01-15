package controller;

import model.Login;
import model.LoginRepository;
import view.LoginView;
import view.MainFrame;
import javax.swing.*;
//Made By Misbah Al Rehman. SRN: 24173647
public class LoginController {
    // UI component for login interactions
    private LoginView view;
    // Data repository for credential validation
    private LoginRepository repository;
    // User session tracking variables
    private boolean isAuthenticated = false;
    private String currentUserRole;
    private Object currentUser;
    private String currentUserId;
    
    // References to main application controllers
    private PatientController patientController;
    private ClinicianController clinicianController;
    private AppointmentController appointmentController;
    private PrescriptionController prescriptionController;
    private ReferralController referralController;
    private StaffController staffController;   
    
    // Initializes controller with view and repository
    public LoginController(LoginView view, LoginRepository repository) {
        this.view = view;
        this.repository = repository;
        initController();
    }
    
    // Receives main controllers from application startup
    public void setMainControllers(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc,
            StaffController sc) {
        
        this.patientController = pc;
        this.clinicianController = cc;
        this.appointmentController = ac;
        this.prescriptionController = prc;
        this.referralController = rc;
        this.staffController = sc;
    }

    // Sets up login button action listener
    private void initController() {
        view.setLoginListener(e -> performLogin());
    }

    // Validates credentials and initiates user session
    private void performLogin() {
        String userId = view.getUserId();
        String password = view.getPassword();
        String selectedRole = view.getSelectedRole();

        // Validate all form fields are completed
        if (userId.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            view.showMessage("Please fill all fields!", true);
            return;
        }

        // Authenticate user against repository data
        Login user = repository.authenticate(userId, password);
        
        if (user != null) {
            // Verify selected role matches actual user role
            if (user.getRole().equalsIgnoreCase(selectedRole)) {
                isAuthenticated = true;
                currentUserRole = user.getRole();
                currentUser = user.getUserObject();
                currentUserId = userId;
                
                view.showMessage("Login successful! Welcome " + userId, false);
                
                // Transition to main application interface
                SwingUtilities.invokeLater(() -> {
                    JFrame loginWindow = (JFrame) SwingUtilities.getWindowAncestor(view);
                    
                    JOptionPane.showMessageDialog(loginWindow,
                        "Welcome " + currentUserRole.toUpperCase() + "!\n" +
                        "User ID: " + currentUserId + "\n" +
                        "Opening main application...",
                        "Login Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Close login window and launch main application
                    if (loginWindow != null) {
                        loginWindow.dispose();
                    }
                    
                    openMainApplication();
                });
                
            } else {
                view.showMessage("Role mismatch! Selected: " + selectedRole + ", Actual: " + user.getRole(), true);
            }
        } else {
            view.showMessage("Invalid credentials! Try ID as password.", true);
        }
    }

    // Launches main application after successful authentication
    private void openMainApplication() {
        // Verify all controllers are properly initialized
        if (patientController == null || clinicianController == null || 
            appointmentController == null || prescriptionController == null || 
            referralController == null ||staffController == null) {
            
            JOptionPane.showMessageDialog(null,
                "Error: Main controllers not set up properly.",
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Configure controllers based on authenticated user role
        if ("patient".equals(currentUserRole)) {
            setupPatientView();
        } else if ("clinician".equals(currentUserRole)) {
            setupClinicianView();
        } else if ("staff".equals(currentUserRole)) {
            setupStaffView();
        } else if ("admin".equals(currentUserRole)) {
            setupAdminView();
        } else {
            // Fallback to staff view for unrecognized roles
            setupStaffView();
        }
        
        // Create and display main application window
        MainFrame mainFrame = new MainFrame(
            patientController,
            clinicianController,
            appointmentController,
            prescriptionController,
            referralController,
            staffController,    
            currentUserRole
        );
        
        mainFrame.setVisible(true);
        
        // Display welcome message with access details
        JOptionPane.showMessageDialog(mainFrame,
            "Welcome to Healthcare Management System!\n" +
            "Role: " + currentUserRole.toUpperCase() + "\n" +
            "User ID: " + currentUserId + "\n\n" +
            "Access Level: " + getAccessDescription(currentUserRole),
            "HMS Dashboard", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Configures patient-specific view and permissions
    private void setupPatientView() {
        // Patients can only access their own records
        patientController.setCurrentPatientId(currentUserId);
        appointmentController.setCurrentPatientId(currentUserId);
        prescriptionController.setCurrentPatientId(currentUserId);
        clinicianController.setCurrentClinicianId(null);
    }
    
    // Configures clinician-specific view and permissions
    private void setupClinicianView() {
        // Clinicians can access their assigned patients and records
        clinicianController.setCurrentClinicianId(currentUserId);
        appointmentController.setCurrentClinicianId(currentUserId);
        prescriptionController.setCurrentClinicianId(currentUserId);
        referralController.setCurrentClinicianId(currentUserId);
        patientController.setCurrentClinicianId(currentUserId);
    }
    
    // Configures staff view with read-only permissions
    private void setupStaffView() {
        // Staff can view all records but with limited edit rights
        patientController.setCurrentPatientId(null);
        clinicianController.setCurrentClinicianId(null);
        appointmentController.setStaffView();
        prescriptionController.setCurrentStaffId(currentUserId);
        referralController.setCurrentStaffId(currentUserId);
        staffController.setCurrentStaffId(currentUserId);
    }
    
    // Configures administrator view with full system access
    private void setupAdminView(){
        // Administrators have complete system control
        patientController.setCurrentPatientId(null);
        patientController.getView().setTitle("Patient Management (Admin Mode)");
        clinicianController.setCurrentClinicianId(null);
        clinicianController.getView().setTitle("Clinician Management (Admin Mode)");
        appointmentController.setCurrentPatientId(null);
        appointmentController.setCurrentClinicianId(null);
        appointmentController.setAdminView();
        prescriptionController.setAdminView();    
        referralController.setAdminView();     
        staffController.setAdminView();              
    }
    
    // Returns user-friendly access level description
    private String getAccessDescription(String role) {
        switch (role.toLowerCase()) {
            case "patient":
                return "Can view and manage own appointments and prescriptions";
            case "clinician":
                return "Can manage assigned patients, appointments, and prescriptions";
            case "staff":
                return "Access to view all modules";
            case "admin":
                return "Full system administrator privileges";
            default:
                return "Standard user access";
        }
    }
    
    // Returns current authenticated user identifier
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public LoginView getView() {
        return view;
    }
    
    public boolean isAuthenticated() { 
        return isAuthenticated; 
    }
    
    public String getCurrentUserRole() { 
        return currentUserRole; 
    }
    
    public Object getCurrentUser() { 
        return currentUser; 
    }
}