package model;

import java.util.ArrayList;
import java.util.List;

public class LoginRepository {
    // Collection of all system user login credentials
    private List<Login> loginUsers;
    // Reference repositories for user data access
    private PatientRepository patientRepo;
    private ClinicianRepository clinicianRepo;
    private StaffRepository staffRepo;
//Made By Misbah Al Rehman. SRN: 24173647
    // Constructor - initializes repository with user data sources
    public LoginRepository(PatientRepository pr, ClinicianRepository cr, StaffRepository sr) {
        this.patientRepo = pr;
        this.clinicianRepo = cr;
        this.staffRepo = sr;
        this.loginUsers = new ArrayList<>();
        loadUsers();
    }

    // Populates login repository from all user data sources
    private void loadUsers() {
        // Load all patients as system users (using ID as password)
        for (Patient p : patientRepo.getAll()) {
            loginUsers.add(new Login(
                p.getId(), 
                p.getId(), // Simplified authentication: ID as password
                "patient", 
                p
            ));
        }

        // Load all clinicians as system users
        for (Clinician c : clinicianRepo.getAll()) {
            loginUsers.add(new Login(
                c.getId(),
                c.getId(), // Simplified authentication: ID as password
                "clinician",
                c
            ));
        }

        // Load all staff members as system users
        for (Staff s : staffRepo.getAll()) {
            loginUsers.add(new Login(
                s.getId(),
                s.getId(), // Simplified authentication: ID as password
                "staff",
                s
            ));
        }
        
        // Add demo administrator account (optional system access)
        loginUsers.add(new Login("admin", "admin123", "admin", null));
    }

    // Validates user credentials against stored authentication data
    public Login authenticate(String userId, String password) {
        for (Login user : loginUsers) {
            if (user.getUserId().equals(userId) && user.authenticate(password)) {
                return user;
            }
        }
        return null;
    }

    // Returns all user identifiers with role information
    public List<String> getAllUserIds() {
        List<String> ids = new ArrayList<>();
        for (Login user : loginUsers) {
            ids.add(user.getUserId() + " (" + user.getRole() + ")");
        }
        return ids;
    }
}