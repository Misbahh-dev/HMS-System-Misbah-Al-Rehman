package controller;

import model.Patient;
import model.PatientRepository;
import model.AppointmentRepository;
import view.PatientView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class PatientController {

    // Core data repositories for patient management
    private final PatientRepository repository;
    private final AppointmentRepository appointmentRepo;
    // UI component for displaying patient information
    private final PatientView view;
    // User context for filtering and access control
    private String currentPatientId;
    private String currentClinicianId;
//Made By Misbah Al Rehman. SRN: 24173647
    // Initializes controller with required dependencies
    public PatientController(PatientRepository repository, AppointmentRepository appointmentRepo, PatientView view) {
        this.repository = repository;
        this.appointmentRepo = appointmentRepo;
        this.view = view;
        this.view.setController(this);
        // Start with secure default settings
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Patient Management - Please Login");
        refreshView();
    }
    
    // Configures controller based on authenticated user role
    public void setUserRole(String userRole, String userId) {
        if ("PATIENT".equals(userRole)) {
            setCurrentPatientId(userId);
        } else if ("CLINICIAN".equals(userRole)) {
            setCurrentClinicianId(userId);
        } else {
            setCurrentPatientId(null);
        }
    }
    
    // Configures view for patient users accessing own data
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient view: Limited to own profile only
            view.setReadOnlyMode(false);
            view.hideAddDeleteButtons();
            view.showUpdateButton();
            view.setTitle("My Profile");
        } else {
            // Staff/admin view: Full patient management
            view.setReadOnlyMode(false);
            view.showAllButtons();
            view.setTitle("Patient Management");
            view.setNextId(repository.generateNewId());
        }
        
        refreshView();
    }
    
    // Configures view for clinicians accessing assigned patients
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null;
        
        // Clinician view: Can manage their assigned patients
        view.setReadOnlyMode(false);
        view.showAllButtons();
        view.setTitle("My Patients");
        view.setNextId(repository.generateNewId());
        
        refreshView();
    }
    
    // Configures unrestricted view for staff members
    public void setStaffView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        refreshView();
    }
    
    // Returns the view component for UI display
    public PatientView getView() {
        return view;
    }

    // Updates patient display based on user permissions
    public void refreshView() {
        List<Patient> patientsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Show only logged-in patient's profile
            Patient currentPatient = repository.findById(currentPatientId);
            patientsToShow = new ArrayList<>();
            if (currentPatient != null) {
                patientsToShow.add(currentPatient);
            }
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Show patients assigned to this clinician
            patientsToShow = repository.findByClinicianId(currentClinicianId, appointmentRepo);
        } else {
            // Staff/admin view: Show all patients
            patientsToShow = repository.getAll();
        }
        
        view.showPatients(patientsToShow);
    }

    // Adds new patient record with permission validation
    public void addPatient(Patient p) {
        // Prevent patients from creating other patient records
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot add new patient records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        repository.addAndAppend(p);
        refreshView();
        view.setNextId(repository.generateNewId());
        JOptionPane.showMessageDialog(view, 
            "Patient added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Updates patient information with appropriate permissions
    public void updatePatient(Patient p) {
        Patient original = repository.findById(p.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Patient not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patients can only update their own profiles
            if (!p.getId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // Staff/clinicians can update any patient record
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Patient updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

    // Deletes patient record with permission checks
    public void deletePatient(Patient p) {
        // Patients cannot delete any patient records
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot delete patient records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        repository.remove(p);
        refreshView();
        view.setNextId(repository.generateNewId());
        JOptionPane.showMessageDialog(view, 
            "Patient deleted successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Retrieves patient by unique identifier
    public Patient findById(String id) {
        return repository.findById(id);
    }
    
    // Deletes patient by ID after retrieving object
    public void deleteById(String id) {
        Patient patient = repository.findById(id);
        if (patient != null) {
            deletePatient(patient);
        }
    }
    
    // Returns currently logged-in patient object
    public Patient getCurrentPatient() {
        if (currentPatientId != null) {
            return repository.findById(currentPatientId);
        }
        return null;
    }
    
    // Returns current patient ID for context tracking
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // Returns current clinician ID for context tracking
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // Checks if current view is patient-restricted
    public boolean isPatientView() {
        return currentPatientId != null && !currentPatientId.isEmpty();
    }
    
    // Checks if current view is clinician-restricted
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
}