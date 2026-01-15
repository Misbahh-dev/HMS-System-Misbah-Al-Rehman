package controller;

import model.*;
import view.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane; // ADDED: For user feedback messages

public class AppointmentController {

    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentView view;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String currentPatientId; // For filtering appointments by patient
    private String currentClinicianId; // For filtering appointments by clinician
    private String currentUserRole; // ADDED: Track user role for better access control

    public AppointmentController(AppointmentRepository repo,
                                 PatientRepository patientRepo,
                                 ClinicianRepository clinicianRepo,
                                 FacilityRepository facilityRepo,
                                 AppointmentView view) {

        this.repo = repo;
        this.patientRepo = patientRepo;
        this.clinicianRepo = clinicianRepo;
        this.facilityRepo = facilityRepo;
        this.view = view;

        view.setController(this);
        setupForUserRole(); // ADDED: Initialize based on user role
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }
    
    // ============================================================
    // ADDED: Setup based on user role
    // ============================================================
    private void setupForUserRole() {
        // Initially assume patient view (most restrictive)
        // This will be updated when setCurrentPatientId/setCurrentClinicianId is called
        view.setReadOnlyMode(true);
        view.hideAddDeleteButtons();
        view.setTitle("My Appointments");
        
        refreshAppointments();
    }
    
    // ============================================================
    // NEW METHOD: Set user context (called by LoginController)
    // ============================================================
    public void setUserContext(String userId, String role) {
        this.currentUserRole = role;
        
        if ("PATIENT".equals(role)) {
            setCurrentPatientId(userId);
        } else if ("CLINICIAN".equals(role)) {
            setCurrentClinicianId(userId);
        } else {
            // Staff/Admin: No filtering, show all
            setStaffView();
        }
    }
    
    // ============================================================
    // NEW METHOD: Setup staff view (no filtering)
    // ============================================================
    public void setStaffView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentUserRole = "STAFF";
        
        view.setReadOnlyMode(false);
        view.showAllButtons();
        view.setTitle("Appointment Management");
        refreshAppointments();
    }

    // ============================================================
    // Set current patient ID for filtering
    // ============================================================
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null; // Clear clinician ID
        this.currentUserRole = "PATIENT";
        
        // PATIENT VIEW: Read-only mode
        view.setReadOnlyMode(false); // Allow booking new appointments
        view.hideAddDeleteButtons(); // Hide delete button for safety
        view.setTitle("My Appointments");
        
        refreshAppointments(); // Refresh to show filtered data
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }
    
    // ============================================================
    // Set current clinician ID for filtering
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null; // Clear patient ID
        this.currentUserRole = "CLINICIAN";
        
        // CLINICIAN VIEW: Edit mode but only for their appointments
        view.setReadOnlyMode(false);
        view.hideAddDeleteButtons(); // Clinicians can manage their appointments
        view.setTitle("My Clinic Appointments");
        
        refreshAppointments(); // Refresh to show filtered data
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }

    public AppointmentView getView() {
        return view;
    }

    // ============================================================
    // Show filtered appointments based on user role
    // ============================================================
    public void refreshAppointments() {
        List<Appointment> appointmentsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient view: Show only this patient's appointments
            appointmentsToShow = getAppointmentsForPatient(currentPatientId);
            
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician view: Show appointments for this clinician
            appointmentsToShow = getAppointmentsForClinician(currentClinicianId);
            
        } else {
            // Staff/Admin view: Show all appointments
            appointmentsToShow = repo.getAll();
        }
        
        view.showAppointments(appointmentsToShow);
    }

    public String generateId() {
        return repo.generateNewId();
    }

    // ============================================================
    // Filter patient IDs based on user role
    // ============================================================
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient can only see their own ID in dropdown
            ids.add(currentPatientId);
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician: Can see all patient IDs (for booking appointments)
            ids = patientRepo.getAllIds();
        } else {
            // Staff/Admin: Can see all patient IDs
            ids = patientRepo.getAllIds();
        }
        return ids;
    }

    // ============================================================
    // MODIFIED: Filter clinician IDs based on user role
    // ============================================================
    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician can only see their own ID in dropdown
            ids.add(currentClinicianId);
        } else if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient: Can see all clinician IDs (for booking)
            ids = clinicianRepo.getAllIds();
        } else {
            // Staff/Admin: Can see all clinician IDs
            ids = clinicianRepo.getAllIds();
        }
        return ids;
    }

    public List<String> getFacilityIds() {
        return facilityRepo.getAllIds();
    }

    // ============================================================
    // ADD APPOINTMENT with role-based security
    // ============================================================
    public void addAppointment(Appointment a) {
        // Security checks based on user role
        if ("PATIENT".equals(currentUserRole)) {
            // Patient is logged in: Ensure appointment is for them
            if (!a.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only book appointments for yourself.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients can only book future appointments
            LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
            if (appointmentDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(view, 
                    "Cannot book appointments in the past.", 
                    "Invalid Date", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
        } else if ("CLINICIAN".equals(currentUserRole)) {
            // Clinician: Can only create appointments where they are the clinician
            if (!a.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only create appointments for yourself.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        repo.addAndAppend(a);
        refreshAppointments();
        
        JOptionPane.showMessageDialog(view, 
            "Appointment added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ============================================================
    // ADDED: UPDATE APPOINTMENT method
    // ============================================================
    public void updateAppointment(Appointment a) {
        // Find the original appointment to check permissions
        Appointment original = repo.findById(a.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Appointment not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions
        if ("PATIENT".equals(currentUserRole)) {
            // PATIENT: Can only update their own appointments
            if (!original.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own appointments.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients can update their appointments
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Your appointment has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else if ("CLINICIAN".equals(currentUserRole)) {
            // CLINICIAN: Can only update their own appointments
            if (!original.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update appointments you are assigned to.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Clinicians can update their appointments
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Appointment updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // STAFF/ADMIN: Can update any appointment
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Appointment updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshAppointments();
    }

    // ============================================================
    // DELETE APPOINTMENT with role-based security
    // ============================================================
    public void deleteById(String id) {
        Appointment a = repo.findById(id);
        if (a != null) {
            // Check user permissions
            if ("PATIENT".equals(currentUserRole)) {
                // Patient can only delete their own appointments
                if (!a.getPatientId().equals(currentPatientId)) {
                    JOptionPane.showMessageDialog(view, 
                        "You can only cancel your own appointments.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Patients can only cancel future appointments
                LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
                if (appointmentDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(view, 
                        "Cannot cancel past appointments.", 
                        "Invalid Action", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
            } else if ("CLINICIAN".equals(currentUserRole)) {
                // Clinician can only delete their own appointments
                if (!a.getClinicianId().equals(currentClinicianId)) {
                    JOptionPane.showMessageDialog(view, 
                        "You can only cancel appointments you are assigned to.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Ask for confirmation
            int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete appointment " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                repo.remove(a);
                JOptionPane.showMessageDialog(view, 
                    "Appointment deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
        refreshAppointments();
    }
    
    // ============================================================
    // Get appointments for specific patient
    // ============================================================
    public List<Appointment> getAppointmentsForPatient(String patientId) {
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment a : repo.getAll()) {
            if (a.getPatientId().equals(patientId)) {
                patientAppointments.add(a);
            }
        }
        return patientAppointments;
    }
    
    // ============================================================
    // Get appointments for specific clinician
    // ============================================================
    public List<Appointment> getAppointmentsForClinician(String clinicianId) {
        List<Appointment> clinicianAppointments = new ArrayList<>();
        for (Appointment a : repo.getAll()) {
            if (a.getClinicianId().equals(clinicianId)) {
                clinicianAppointments.add(a);
            }
        }
        return clinicianAppointments;
    }
    
    // ============================================================
    // NEW METHOD: Get current clinician ID
    // ============================================================
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // ============================================================
    // NEW METHOD: Get current patient ID
    // ============================================================
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // ============================================================
    // NEW METHOD: Get current user role
    // ============================================================
    public String getCurrentUserRole() {
        return currentUserRole;
    }
    
    // ============================================================
    // Clear current user IDs (for logout)
    // ============================================================
    public void clearCurrentUser() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentUserRole = null;
        setupForUserRole(); // Reset to default state
    }
}