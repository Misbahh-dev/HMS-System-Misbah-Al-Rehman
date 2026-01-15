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
//Made By Misbah Al Rehman. SRN: 24173647
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PrescriptionController {

    // Core data repositories for prescription management
    private final PrescriptionRepository repository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    // UI component for displaying prescription data
    private final PrescriptionView view;
    // User context for filtering and access control
    private String currentPatientId;
    private String currentClinicianId;
    private String currentStaffId;

    // Initializes controller with all required dependencies
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
        setupForUserRole();
    }
    
    // Configures initial UI state for security purposes
    private void setupForUserRole() {
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );

        refreshView();
    }
    
    // Configures view for patient users accessing own data
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null;
        
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("My Prescriptions (View Only)");
        
        refreshView();
        
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );
    }
    
    // Configures view for clinician users managing prescriptions
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null;
        
        view.setReadOnlyMode(false);
        view.showAddUpdateButtons();
        view.setTitle("Manage Prescriptions");
        
        refreshView();
    }
    
    // Configures view for staff users with read-only access
    public void setCurrentStaffId(String staffId) {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentStaffId = staffId;
        
        view.setReadOnlyMode(true);
        view.hideAddUpdateButtons();
        view.setTitle("All Prescriptions");
        
        refreshView();
        
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );
    }
    
    // Configures administrative view with full system access
    public void setAdminView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentStaffId = null;
        view.setTitle("Prescriptions (Admin Mode)"); 
        refreshView();
    }

    // Returns the view component for UI display
    public PrescriptionView getView() {
        return view;
    }

    // Updates prescription display based on user permissions
    public void refreshView() {
        List<Prescription> prescriptionsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            prescriptionsToShow = getPrescriptionsForPatient(currentPatientId);
            view.setReadOnlyMode(true);
            view.hideAddUpdateButtons();
            view.setTitle("My Prescriptions (View Only)");
            
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            prescriptionsToShow = getPrescriptionsByClinician(currentClinicianId);
            view.setReadOnlyMode(false);
            view.showAddUpdateButtons();
            view.setTitle("Manage Prescriptions");
            
        } else if (currentStaffId != null && !currentStaffId.isEmpty()) {
            prescriptionsToShow = repository.getAll();
            view.setReadOnlyMode(true);
            view.hideAddUpdateButtons();
            view.setTitle("All Prescriptions (View Only)");
            
        } else {
            prescriptionsToShow = repository.getAll();
            view.setReadOnlyMode(false);
            view.showAddUpdateButtons();
            view.setTitle("All Prescriptions");
        }
        
        view.showPrescriptions(prescriptionsToShow);
        
        if (currentPatientId == null || currentPatientId.isEmpty()) {
            view.setNextId(repository.generateNewId());
        }
    }

    // Returns patient IDs visible to current user
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            ids.add(currentPatientId);
        } else {
            for (Patient p : patientRepository.getAll()) {
                ids.add(p.getId());
            }
        }
        return ids;
    }

    // Returns clinician IDs visible to current user
    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            ids.add(currentClinicianId);
        } else {
            for (Clinician c : clinicianRepository.getAll()) {
                ids.add(c.getId());
            }
        }
        return ids;
    }

    // Returns appointment IDs visible to current user
    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            for (Appointment a : appointmentRepository.getAll()) {
                if (a.getPatientId().equals(currentPatientId)) {
                    ids.add(a.getId());
                }
            }
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            for (Appointment a : appointmentRepository.getAll()) {
                if (a.getClinicianId().equals(currentClinicianId)) {
                    ids.add(a.getId());
                }
            }
        } else {
            for (Appointment a : appointmentRepository.getAll()) {
                ids.add(a.getId());
            }
        }
        return ids;
    }

    // Adds new prescription with permission validation
    public void addPrescription(Prescription p) {
        // Prevent patients from creating prescriptions
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot issue prescriptions. Please contact your clinician.",
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ensure clinicians only issue prescriptions under their name
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
        
        JOptionPane.showMessageDialog(view, 
            "Prescription added successfully!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Updates prescription with role-based permission checks
    public void updatePrescription(Prescription p) {
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
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            if (!original.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own prescriptions.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients can only update status and collection date
            Prescription limitedUpdate = new Prescription(
                p.getId(),
                original.getPatientId(),
                original.getClinicianId(),
                original.getAppointmentId(),
                original.getPrescriptionDate(),
                original.getMedication(),
                original.getDosage(),
                original.getFrequency(),
                original.getDurationDays(),
                original.getQuantity(),
                original.getInstructions(),
                original.getPharmacyName(),
                p.getStatus(),
                original.getIssueDate(),
                p.getCollectionDate()
            );
            
            repository.update(limitedUpdate);
            JOptionPane.showMessageDialog(view, 
                "Prescription status updated successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
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
            repository.update(p);
            JOptionPane.showMessageDialog(view, 
                "Prescription updated successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

    // Deletes prescription with comprehensive permission checks
    public void deleteById(String id) {
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
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Patients cannot delete prescriptions.",
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            refreshView();
            return;
        }
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            if (!prescriptionToDelete.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only delete prescriptions you issued.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                refreshView();
                return;
            }
        }
        
        repository.removeById(id);
        JOptionPane.showMessageDialog(view, 
            "Prescription deleted successfully!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        refreshView();
    }
    
    // Retrieves all prescriptions for specific patient
    public List<Prescription> getPrescriptionsForPatient(String patientId) {
        List<Prescription> patientPrescriptions = new ArrayList<>();
        for (Prescription p : repository.getAll()) {
            if (p.getPatientId().equals(patientId)) {
                patientPrescriptions.add(p);
            }
        }
        return patientPrescriptions;
    }
    
    // Retrieves all prescriptions issued by specific clinician
    public List<Prescription> getPrescriptionsByClinician(String clinicianId) {
        List<Prescription> clinicianPrescriptions = new ArrayList<>();
        for (Prescription p : repository.getAll()) {
            if (p.getClinicianId().equals(clinicianId)) {
                clinicianPrescriptions.add(p);
            }
        }
        return clinicianPrescriptions;
    }
    
    // Returns medication history for specific patient
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
    
    // Checks if current view is patient-restricted
    public boolean isPatientView() {
        return currentPatientId != null && !currentPatientId.isEmpty();
    }
    
    // Checks if current view is clinician-restricted
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
    
    // Checks if current view is staff/administrator
    public boolean isStaffView() {
        return currentPatientId == null && currentClinicianId == null;
    }
}