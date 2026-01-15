package controller;

import model.Patient;
import model.PatientRepository;
import model.AppointmentRepository; // ADDED: Import AppointmentRepository
import view.PatientView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class PatientController {

    private final PatientRepository repository;
    private final AppointmentRepository appointmentRepo; // ADDED: For clinician-patient relationships
    private final PatientView view;
    private String currentPatientId; // For filtering patient data by patient
    private String currentClinicianId; // ADDED: For filtering patients by clinician

    public PatientController(PatientRepository repository, AppointmentRepository appointmentRepo, PatientView view) { // MODIFIED: Added appointmentRepo
        this.repository = repository;
        this.appointmentRepo = appointmentRepo; // ADDED: Store appointment repository
        this.view = view;
        this.view.setController(this);
        // Start with all buttons hidden and read-only
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Patient Management - Please Login");
        refreshView();
    }
    
    // ============================================================
    // Method to set user role (called by LoginController)
    // ============================================================
    public void setUserRole(String userRole, String userId) {
        if ("PATIENT".equals(userRole)) {
            setCurrentPatientId(userId); // Patient sees only their data
        } else if ("CLINICIAN".equals(userRole)) {
            setCurrentClinicianId(userId); // ADDED: Clinician sees their patients
        } else {
            setCurrentPatientId(null); // Staff/Admin sees all data
        }
    }
    
    // ============================================================
    // Set current patient ID for filtering (for patients)
    // ============================================================
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null; // Clear clinician ID when setting patient ID
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT VIEW: Read-only mode except for own updates
            view.setReadOnlyMode(false); // Allow updates to own info
            view.hideAddDeleteButtons(); // Patients can't add/delete other patients
            view.showUpdateButton(); // But can update their own info
            view.setTitle("My Profile");
        } else {
            // CLINICIAN/STAFF/ADMIN VIEW: Full access
            view.setReadOnlyMode(false);
            view.showAllButtons(); // This should show Add, Update, and Delete
            view.setTitle("Patient Management");
            view.setNextId(repository.generateNewId()); // Set next ID for adding
        }
        
        refreshView(); // Refresh to show filtered data
    }
    
    // ============================================================
    // NEW METHOD: Set current clinician ID for filtering (for clinicians)
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null; // Clear patient ID when setting clinician ID
        
        // CLINICIAN VIEW: Can see and manage their assigned patients
        view.setReadOnlyMode(false);
        view.showAllButtons(); // Clinicians can manage their patients
        view.setTitle("My Patients");
        view.setNextId(repository.generateNewId()); // Set next ID for adding
        
        refreshView(); // Refresh to show filtered data
    }
    
    // ============================================================
    // Method for staff/admin to view all patients
    // ============================================================
    public void setStaffView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        refreshView();
    }
    
    public PatientView getView() {
        return view;
    }

    // ============================================================
    // Show filtered patients based on user role
    // ============================================================
    public void refreshView() {
        List<Patient> patientsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT VIEW: Show only the current patient's data
            Patient currentPatient = repository.findById(currentPatientId);
            patientsToShow = new ArrayList<>();
            if (currentPatient != null) {
                patientsToShow.add(currentPatient);
            }
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // CLINICIAN VIEW: Show patients assigned to this clinician via appointments
            patientsToShow = repository.findByClinicianId(currentClinicianId, appointmentRepo);
        } else {
            // STAFF/ADMIN VIEW: Show all patients
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
            JOptionPane.showMessageDialog(view, 
                "Patients cannot add new patient records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only clinicians/staff/admin can add new patients
        repository.addAndAppend(p);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
        
        JOptionPane.showMessageDialog(view, 
            "Patient added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // UPDATE PATIENT - Allowed for patients (their own) and staff
    // ============================================================
    public void updatePatient(Patient p) {
        // Find the original patient to check permissions
        Patient original = repository.findById(p.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Patient not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT: Can only update their own record
            if (!p.getId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients can update their own info
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // CLINICIAN/STAFF/ADMIN: Can update any patient
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Patient updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

    // ============================================================
    // DELETE PATIENT - Only for staff/admin, not for patients
    // ============================================================
    public void deletePatient(Patient p) {
        // If a patient is logged in, they shouldn't be able to delete any patients
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot delete patient records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only clinicians/staff/admin can delete patients
        repository.remove(p);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
        
        JOptionPane.showMessageDialog(view, 
            "Patient deleted successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public Patient findById(String id) {
        return repository.findById(id);
    }
    
    // ============================================================
    // Delete by ID
    // ============================================================
    public void deleteById(String id) {
        Patient patient = repository.findById(id);
        if (patient != null) {
            deletePatient(patient);
        }
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
    // Get current patient ID
    // ============================================================
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // ============================================================
    // NEW METHOD: Get current clinician ID
    // ============================================================
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // ============================================================
    // Check if current user is a patient
    // ============================================================
    public boolean isPatientView() {
        return currentPatientId != null && !currentPatientId.isEmpty();
    }
    
    // ============================================================
    // NEW METHOD: Check if current user is a clinician
    // ============================================================
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
    
}