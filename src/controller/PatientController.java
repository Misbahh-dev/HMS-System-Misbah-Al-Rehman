package controller;

import model.Patient;
import model.PatientRepository;
import view.PatientView;
import java.util.List;
import java.util.ArrayList;

public class PatientController {

    private final PatientRepository repository;
    private final PatientView view;
    private String currentPatientId; // For filtering patient data

    public PatientController(PatientRepository repository, PatientView view) {
        this.repository = repository;
        this.view = view;
        this.view.setController(this);
        this.view.setNextId(repository.generateNewId()); // Set initial next ID
        setupForUserRole(); // Initialize based on user role
    }
    
    // ============================================================
    // NEW METHOD: Setup based on user role
    // ============================================================
    private void setupForUserRole() {
        // Initially assume patient view (most restrictive)
        // This will be updated when setCurrentPatientId is called
        view.setReadOnlyMode(true);
        view.hideAddDeleteButtons();
        view.showUpdateButton();
        view.setTitle("My Profile");
        
        refreshView();
    }
    
    // ============================================================
    // Set current patient ID for filtering
    // ============================================================
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT VIEW: Read-only mode except for own updates
            view.setReadOnlyMode(false); // Allow updates to own info
            view.hideAddDeleteButtons(); // Patients can't add/delete other patients
            view.showUpdateButton(); // But can update their own info
            view.setTitle("My Profile");
        } else {
            // CLINICIAN/STAFF/ADMIN VIEW: Full access
            view.setReadOnlyMode(false);
            view.showAllButtons();
            view.setTitle("Patient Management");
        }
        
        refreshView(); // Refresh to show filtered data
    }
    
    public PatientView getView() {
        return view;
    }

    // ============================================================
    // Show only current patient if ID is set
    // Otherwise show all patients (for clinicians/staff/admin)
    // ============================================================
    public void refreshView() {
        List<Patient> patientsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Show only the current patient's data
            Patient currentPatient = repository.findById(currentPatientId);
            patientsToShow = new ArrayList<>();
            if (currentPatient != null) {
                patientsToShow.add(currentPatient);
            }
        } else {
            // Show all patients (for clinicians, staff, admin)
            patientsToShow = repository.getAll();
        }
        
        view.showPatients(patientsToShow);
    }

    // ============================================================
    // ADD PATIENT - Only for clinicians/staff/admin
    // ============================================================
    public void addPatient(Patient p) {
        // If a patient is logged in, they shouldn't be able to add new patients
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patients can only view/update their own profile, not add new patients
            return;
        }
        
        // Only clinicians/staff/admin can add new patients
        repository.addAndAppend(p);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
    }

    // ============================================================
    // UPDATE PATIENT - Allowed for patients (their own) and staff
    // ============================================================
    public void updatePatient(Patient p) {
        // Find the original patient to check permissions
        Patient original = repository.findById(p.getId());
        
        if (original == null) {
            // Patient not found
            return;
        }
        
        // Check user permissions
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT: Can only update their own record
            if (!p.getId().equals(currentPatientId)) {
                // Patients can only update their own profile
                return;
            }
            
            // Patients can update their own info
            repository.update(p);
            
        } else {
            // STAFF/ADMIN/CLINICIAN: Can update any patient
            repository.update(p);
        }
        
        refreshView();
    }

    // ============================================================
    // DELETE PATIENT - Only for staff/admin, not for patients
    // ============================================================
    public void deletePatient(Patient p) {
        // If a patient is logged in, they shouldn't be able to delete any patients
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patients cannot delete profiles
            return;
        }
        
        // Only clinicians/staff/admin can delete patients
        repository.remove(p);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
    }

    public Patient findById(String id) {
        return repository.findById(id);
    }
    
    // ============================================================
    // Get current patient (useful for other controllers)
    // ============================================================
    public Patient getCurrentPatient() {
        if (currentPatientId != null) {
            return repository.findById(currentPatientId);
        }
        return null;
    }
    
    // ============================================================
    // NEW METHOD: Delete by ID
    // ============================================================
    public void deleteById(String id) {
        Patient patient = repository.findById(id);
        if (patient != null) {
            deletePatient(patient);
        }
    }
    
    // ============================================================
    // NEW METHOD: Get current patient ID
    // ============================================================
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // ============================================================
    // NEW METHOD: Check if current user is a patient
    // ============================================================
    public boolean isPatientView() {
        return currentPatientId != null && !currentPatientId.isEmpty();
    }
    
    // ============================================================
    // NEW METHOD: Clear current patient ID (for logout)
    // ============================================================
    public void clearCurrentPatient() {
        this.currentPatientId = null;
        setupForUserRole(); // Reset to default state
    }
}