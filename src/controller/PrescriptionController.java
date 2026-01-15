package controller;

import model.Prescription;
import model.PrescriptionRepository;
import model.PatientRepository;
import model.ClinicianRepository;
import model.AppointmentRepository;
import model.Patient;
import model.Clinician;
import model.Appointment;
import view.PrescriptionView;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PrescriptionController {

    private final PrescriptionRepository repository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionView view;
    private String currentPatientId; // For filtering prescriptions by patient
    private String currentClinicianId; // For filtering prescriptions by clinician
    private String currentStaffId;
    public PrescriptionController(PrescriptionRepository repository,
                                  PatientRepository patientRepository,
                                  ClinicianRepository clinicianRepository,
                                  AppointmentRepository appointmentRepository,
                                  PrescriptionView view) {

        this.repository = repository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.appointmentRepository = appointmentRepository;
        this.view = view;

        view.setController(this);

        // Initial setup - will be updated based on user role
        setupForUserRole();
    }
    
    // ============================================================
    // NEW METHOD: Setup based on user role (initially assume patient)
    // ============================================================
    private void setupForUserRole() {
        // Initially assume patient view (most restrictive)
        // This will be updated when setCurrentPatientId/setCurrentClinicianId is called
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        
        // Populate dropdowns with initial data
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );

        refreshView();
    }
    
    // ============================================================
    // Set current patient ID for filtering
    // ============================================================
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null; // Clear clinician ID if set
        
        // PATIENT VIEW: Read-only mode
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("My Prescriptions (View Only)");
        
        refreshView(); // Refresh to show filtered data
        
        // Update dropdowns with filtered data
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );
    }
    
    // ============================================================
    // Set current clinician ID for filtering
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null; // Clear patient ID if set
        
        // CLINICIAN VIEW: Edit mode
        view.setReadOnlyMode(false);
        view.showAddUpdateButtons();
        view.setTitle("Manage Prescriptions");
        
        refreshView(); // Refresh to show filtered data
    }
    
    // ============================================================
    // NEW: Set current staff ID for filtering (for staff)
    // ============================================================
    public void setCurrentStaffId(String staffId) {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentStaffId = staffId;
// Clear patient and clinician IDs
        
        // STAFF VIEW: Can view all prescriptions
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("All Prescriptions");
        
        refreshView(); // Refresh to show filtered data
        
        // Update dropdowns with full access
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );
    }
    
    // ============================================================
    // NEW: Method for staff to view all prescriptions (admin view)
    // ============================================================
    public void setAdminView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
         this.currentStaffId = null;
         view.setTitle("Prescriptions (Admin Mode)"); 
        refreshView();
    }

    public PrescriptionView getView() {
        return view;
    }

    // ============================================================
    // Show filtered prescriptions based on user role
    // ============================================================
  public void refreshView() {
    List<Prescription> prescriptionsToShow;
    
    if (currentPatientId != null && !currentPatientId.isEmpty()) {
        // Patient view: Show only this patient's prescriptions
        prescriptionsToShow = getPrescriptionsForPatient(currentPatientId);
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("My Prescriptions (View Only)");
        
    } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
        // Clinician view: Show prescriptions issued by this clinician
        prescriptionsToShow = getPrescriptionsByClinician(currentClinicianId);
        view.setReadOnlyMode(false);
        view.showAddUpdateButtons();
        view.setTitle("Manage Prescriptions");
        
    } else if (currentStaffId != null && !currentStaffId.isEmpty()) {
        // STAFF view: Show all prescriptions (READ-ONLY)
        prescriptionsToShow = repository.getAll();
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("All Prescriptions (View Only)");
        
    } else {
        // ADMIN view: Show all prescriptions (EDITABLE)
        prescriptionsToShow = repository.getAll();
        view.setReadOnlyMode(false);
        view.showAddUpdateButtons();
        view.setTitle("All Prescriptions");
    }
    
    view.showPrescriptions(prescriptionsToShow);
    
    // Only generate new ID for clinicians/staff/admin (not for patients)
    if (currentPatientId == null || currentPatientId.isEmpty()) {
        view.setNextId(repository.generateNewId());
    }
}

    // ============================================================
    // Filter patient IDs based on user role
    // ============================================================
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient can only see their own ID in dropdown
            ids.add(currentPatientId);
        } else {
            // Clinicians/staff/admin can see all patient IDs
            for (Patient p : patientRepository.getAll()) {
                ids.add(p.getId());
            }
        }
        return ids;
    }

    // ============================================================
    // Filter clinician IDs based on user role
    // ============================================================
    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician can only see their own ID in dropdown
            ids.add(currentClinicianId);
        } else {
            // Patients/staff/admin can see all clinician IDs
            for (Clinician c : clinicianRepository.getAll()) {
                ids.add(c.getId());
            }
        }
        return ids;
    }

    // ============================================================
    // Filter appointment IDs based on user role
    // ============================================================
    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient: Show only their own appointments
            for (Appointment a : appointmentRepository.getAll()) {
                if (a.getPatientId().equals(currentPatientId)) {
                    ids.add(a.getId());
                }
            }
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician: Show only appointments with this clinician
            for (Appointment a : appointmentRepository.getAll()) {
                if (a.getClinicianId().equals(currentClinicianId)) {
                    ids.add(a.getId());
                }
            }
        } else {
            // Staff/Admin: Show all appointments
            for (Appointment a : appointmentRepository.getAll()) {
                ids.add(a.getId());
            }
        }
        return ids;
    }

    // ============================================================
    // ADD PRESCRIPTION - PATIENTS CANNOT ADD
    // ============================================================
    public void addPrescription(Prescription p) {
        // Security: Patients cannot add prescriptions
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot issue prescriptions. Please contact your clinician.",
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Clinicians can only issue prescriptions under their name
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            if (!p.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only issue prescriptions under your name.",
                    "Invalid Action", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        repository.addAndAppend(p);
        refreshView();
        
        // Show success message for clinicians/staff/admin
        JOptionPane.showMessageDialog(view, 
            "Prescription added successfully!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // UPDATE PRESCRIPTION - PATIENTS CAN ONLY UPDATE STATUS
    // ============================================================
    public void updatePrescription(Prescription p) {
        // Find the original prescription to check permissions
        Prescription original = null;
        for (Prescription pres : repository.getAll()) {
            if (pres.getId().equals(p.getId())) {
                original = pres;
                break;
            }
        }
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Prescription not found.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT: Can only update status (e.g., mark as collected)
            if (!original.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own prescriptions.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients can only change status and collection date fields
            // Keep all other fields the same as original
            Prescription limitedUpdate = new Prescription(
                p.getId(),
                original.getPatientId(),          // Keep original patient
                original.getClinicianId(),        // Keep original clinician
                original.getAppointmentId(),      // Keep original appointment
                original.getPrescriptionDate(),   // Keep original date
                original.getMedication(),         // Keep original medication
                original.getDosage(),             // Keep original dosage
                original.getFrequency(),          // Keep original frequency
                original.getDurationDays(),       // Keep original duration
                original.getQuantity(),           // Keep original quantity
                original.getInstructions(),       // Keep original instructions
                original.getPharmacyName(),       // Keep original pharmacy
                p.getStatus(),                    // ONLY status can be updated by patient
                original.getIssueDate(),          // Keep original issue date
                p.getCollectionDate()             // Collection date can be updated
            );
            
            repository.update(limitedUpdate);
            JOptionPane.showMessageDialog(view, 
                "Prescription status updated successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // CLINICIAN: Can update their own prescriptions
            if (!original.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update prescriptions you issued.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Prescription updated successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // STAFF/ADMIN: Can update any prescription
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Prescription updated successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

   
    // ============================================================
    // DELETE PRESCRIPTION - PATIENTS CANNOT DELETE
    // ============================================================
    public void deleteById(String id) {
        // Find the prescription to check permissions
        Prescription prescriptionToDelete = null;
        for (Prescription p : repository.getAll()) {
            if (p.getId().equals(id)) {
                prescriptionToDelete = p;
                break;
            }
        }
        
        if (prescriptionToDelete == null) {
            JOptionPane.showMessageDialog(view, 
                "Prescription not found.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            refreshView();
            return;
        }
        
        // Security checks
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // PATIENT: Cannot delete prescriptions
            JOptionPane.showMessageDialog(view, 
                "Patients cannot delete prescriptions.",
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            refreshView();
            return;
        }
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // CLINICIAN: Can only delete their own prescriptions
            if (!prescriptionToDelete.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only delete prescriptions you issued.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                refreshView();
                return;
            }
        }
        
        // STAFF/ADMIN or authorized clinician can delete
        repository.removeById(id);
        JOptionPane.showMessageDialog(view, 
            "Prescription deleted successfully!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        refreshView();
    }
    
    // ============================================================
    // Get prescriptions for specific patient
    // ============================================================
    public List<Prescription> getPrescriptionsForPatient(String patientId) {
        List<Prescription> patientPrescriptions = new ArrayList<>();
        for (Prescription p : repository.getAll()) {
            if (p.getPatientId().equals(patientId)) {
                patientPrescriptions.add(p);
            }
        }
        return patientPrescriptions;
    }
    
    // ============================================================
    // Get prescriptions issued by specific clinician
    // ============================================================
    public List<Prescription> getPrescriptionsByClinician(String clinicianId) {
        List<Prescription> clinicianPrescriptions = new ArrayList<>();
        for (Prescription p : repository.getAll()) {
            if (p.getClinicianId().equals(clinicianId)) {
                clinicianPrescriptions.add(p);
            }
        }
        return clinicianPrescriptions;
    }
    
    // ============================================================
    // Get medication history for patient
    // ============================================================
    public List<String> getMedicationHistoryForPatient(String patientId) {
        List<String> medications = new ArrayList<>();
        for (Prescription p : repository.getAll()) {
            if (p.getPatientId().equals(patientId)) {
                String history = p.getMedication() + " - " + p.getDosage() + 
                               " (" + p.getPrescriptionDate() + ")";
                medications.add(history);
            }
        }
        return medications;
    }
    
    // ============================================================
    // NEW: Check if current user is a patient
    // ============================================================
    public boolean isPatientView() {
        return currentPatientId != null && !currentPatientId.isEmpty();
    }
    
    // ============================================================
    // NEW: Check if current user is a clinician
    // ============================================================
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
    
    // ============================================================
    // NEW: Check if current user is staff/admin
    // ============================================================
    public boolean isStaffView() {
        return currentPatientId == null && currentClinicianId == null;
    }
}