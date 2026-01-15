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
    private String currentUserId; // Store userId at class level
    
    // Store references to main controllers (will be set from Main.java)
    private PatientController patientController;
    private ClinicianController clinicianController;
    private AppointmentController appointmentController;
    private PrescriptionController prescriptionController;
    private ReferralController referralController;
    private StaffController staffController;   
    
    public LoginController(LoginView view, LoginRepository repository) {
        this.view = view;
        this.repository = repository;
        initController();
    }
    
    // Set main controllers (called from Main.java)
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
                currentUserId = userId; // Store userId at class level
                
                view.showMessage("Login successful! Welcome " + userId, false);
                
                // Close login window and open MainFrame
                SwingUtilities.invokeLater(() -> {
                    // Get the login window (JFrame) that contains this view
                    JFrame loginWindow = (JFrame) SwingUtilities.getWindowAncestor(view);
                    
                    // Show success message
                    JOptionPane.showMessageDialog(loginWindow,
                        "Welcome " + currentUserRole.toUpperCase() + "!\n" +
                        "User ID: " + currentUserId + "\n" +
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

    // Open main application after successful login
    private void openMainApplication() {
        if (patientController == null || clinicianController == null || 
            appointmentController == null || prescriptionController == null || 
            referralController == null ||staffController == null) {
            
            JOptionPane.showMessageDialog(null,
                "Error: Main controllers not set up properly.",
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // ============================================================
        // SETUP CONTROLLERS BASED ON USER ROLE
        // ============================================================
  if ("patient".equals(currentUserRole)) {
    setupPatientView();
} else if ("clinician".equals(currentUserRole)) {
    setupClinicianView();
} else if ("staff".equals(currentUserRole)) {
    setupStaffView(); // Regular staff (read-only)
} else if ("admin".equals(currentUserRole)) {
    setupAdminView(); // Admin (full edit access)
} else {
    // Default to staff view for unknown roles
    setupStaffView();
}
        // Create and show MainFrame with user role
        MainFrame mainFrame = new MainFrame(
            patientController,
            clinicianController,
            appointmentController,
            prescriptionController,
            referralController,
            staffController,    
            currentUserRole  // Pass user role for access control
        );
        
        mainFrame.setVisible(true);
        
        // Show welcome message in main app
        JOptionPane.showMessageDialog(mainFrame,
            "Welcome to Healthcare Management System!\n" +
            "Role: " + currentUserRole.toUpperCase() + "\n" +
            "User ID: " + currentUserId + "\n\n" +
            "Access Level: " + getAccessDescription(currentUserRole),
            "HMS Dashboard", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ============================================================
    // SETUP PATIENT VIEW
    // ============================================================
    private void setupPatientView() {
        // For patients, filter to show only their own data
        patientController.setCurrentPatientId(currentUserId);
        appointmentController.setCurrentPatientId(currentUserId);
        prescriptionController.setCurrentPatientId(currentUserId);
        
        // Patients typically don't need clinician or referral access
        clinicianController.setCurrentClinicianId(null);
    }
    
    // ============================================================
    // SETUP CLINICIAN VIEW
    // ============================================================
    private void setupClinicianView() {
        // For clinicians, filter to show only their related data
        clinicianController.setCurrentClinicianId(currentUserId);
        appointmentController.setCurrentClinicianId(currentUserId);
        prescriptionController.setCurrentClinicianId(currentUserId);
        
        // KEY: Set current clinician ID in referral controller
        referralController.setCurrentClinicianId(currentUserId);  // ADD THIS LINE
        
        // Clinicians see only THEIR PATIENTS (based on appointments)
        patientController.setCurrentClinicianId(currentUserId);
    }
    
    // ============================================================
    // SETUP STAFF/ADMIN VIEW
    // ============================================================
    private void setupStaffView() {
        // Staff/Admin see all data (no filtering)
        patientController.setCurrentPatientId(null);
        clinicianController.setCurrentClinicianId(null);
        appointmentController.setStaffView();
       prescriptionController.setCurrentStaffId(currentUserId);  // Shows ALL prescriptions
        referralController.setCurrentStaffId(currentUserId);      // Shows ALL referrals
        staffController.setCurrentStaffId(currentUserId);
        
    }
    
    private void setupAdminView (){
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
    
    
  
    
    // Describe access level based on role
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
    
    // ============================================================
    // GETTER FOR CURRENT USER ID
    // ============================================================
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    // Getter for the view
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
    
}