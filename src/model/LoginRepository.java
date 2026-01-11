package model;

import java.util.ArrayList;
import java.util.List;

public class LoginRepository {
    private List<Login> loginUsers; // Change to List<Login> if you kept Login.java
    private PatientRepository patientRepo;
    private ClinicianRepository clinicianRepo;
    private StaffRepository staffRepo;

    public LoginRepository(PatientRepository pr, ClinicianRepository cr, StaffRepository sr) {
        this.patientRepo = pr;
        this.clinicianRepo = cr;
        this.staffRepo = sr;
        this.loginUsers = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        // Load patients (use patient_id as password for simplicity)
        for (Patient p : patientRepo.getAll()) {
            loginUsers.add(new Login( // Change to new Login() if you kept Login.java
                p.getId(), 
                p.getId(), // Using ID as password
                "patient", 
                p
            ));
        }

        // Load clinicians
        for (Clinician c : clinicianRepo.getAll()) {
            loginUsers.add(new Login( // Change to new Login() if you kept Login.java
                c.getId(),
                c.getId(), // Using ID as password
                "clinician",
                c
            ));
        }

        // Load staff
        for (Staff s : staffRepo.getAll()) {
            loginUsers.add(new Login( // Change to new Login() if you kept Login.java
                s.getId(),
                s.getId(), // Using ID as password
                "staff",
                s
            ));
        }
        
        // Add a demo admin (optional)
        loginUsers.add(new Login("admin", "admin123", "admin", null)); // Change here too
    }

    public Login authenticate(String userId, String password) { // Change return type if needed
        for (Login user : loginUsers) { // Change type if needed
            if (user.getUserId().equals(userId) && user.authenticate(password)) {
                return user;
            }
        }
        return null;
    }

    public List<String> getAllUserIds() {
        List<String> ids = new ArrayList<>();
        for (Login user : loginUsers) { // Change type if needed
            ids.add(user.getUserId() + " (" + user.getRole() + ")");
        }
        return ids;
    }
}