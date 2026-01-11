package controller;

import model.*;
import view.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class AppointmentController {

    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentView view;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String currentPatientId; // ADDED: For filtering appointments by patient
    private String currentClinicianId; // ADDED: For filtering appointments by clinician

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
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }

    // ============================================================
    // NEW METHOD: Set current patient ID for filtering
    // Called by LoginController when patient logs in
    // ============================================================
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        refreshAppointments(); // Refresh to show filtered data
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds()); // Update dropdowns
    }
    
    // ============================================================
    // NEW METHOD: Set current clinician ID for filtering
    // Called by LoginController when clinician logs in
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        refreshAppointments(); // Refresh to show filtered data
    }

    public AppointmentView getView() {
        return view;
    }

    // ============================================================
    // MODIFIED: Show filtered appointments based on user role
    // ============================================================
    public void refreshAppointments() {
        List<Appointment> appointmentsToShow;
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient view: Show only this patient's appointments
            appointmentsToShow = new ArrayList<>();
            for (Appointment a : repo.getAll()) {
                if (a.getPatientId().equals(currentPatientId)) {
                    appointmentsToShow.add(a);
                }
            }
            
            // Optional: Update view title for patients
            // view.setTitle("My Appointments");
            
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician view: Show appointments for this clinician
            appointmentsToShow = new ArrayList<>();
            for (Appointment a : repo.getAll()) {
                if (a.getClinicianId().equals(currentClinicianId)) {
                    appointmentsToShow.add(a);
                }
            }
            
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
    // MODIFIED: Filter patient IDs based on user role
    // ============================================================
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient can only see their own ID in dropdown
            ids.add(currentPatientId);
        } else {
            // Clinicians/staff/admin can see all patient IDs
            ids = patientRepo.getAllIds();
        }
        return ids;
    }

    public List<String> getClinicianIds() {
        return clinicianRepo.getAllIds();
    }

    public List<String> getFacilityIds() {
        return facilityRepo.getAllIds();
    }

    // ============================================================
    // MODIFIED: Add security checks for appointment creation
    // ============================================================
    public void addAppointment(Appointment a) {
        // Security checks based on user role
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patient is logged in: Ensure appointment is for them
            if (!a.getPatientId().equals(currentPatientId)) {
                // Patient trying to book appointment for someone else
                // You could show error: "You can only book appointments for yourself"
                return;
            }
            
            // Optional: Patients can only book future appointments
            // LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
            // if (appointmentDate.isBefore(LocalDate.now())) {
            //     // Show error: "Cannot book appointments in the past"
            //     return;
            // }
        }
        
        // If clinician is logged in, could add clinician-specific rules
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician could be restricted to only book appointments for themselves
            // Or could have special privileges
        }
        
        repo.addAndAppend(a);
        refreshAppointments();
    }

    // ============================================================
    // MODIFIED: Add security checks for appointment deletion
    // ============================================================
    public void deleteById(String id) {
        Appointment a = repo.findById(id);
        if (a != null) {
            // Check if user has permission to delete this appointment
            boolean canDelete = true;
            
            if (currentPatientId != null && !currentPatientId.isEmpty()) {
                // Patient can only delete their own appointments
                canDelete = a.getPatientId().equals(currentPatientId);
                
                // Optional: Patients can only cancel future appointments
                // LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
                // if (appointmentDate.isBefore(LocalDate.now())) {
                //     canDelete = false; // Cannot delete past appointments
                // }
            }
            
            if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
                // Clinician can only delete their own appointments
                canDelete = a.getClinicianId().equals(currentClinicianId);
            }
            
            if (canDelete) {
                repo.remove(a);
            } else {
                // Optional: Show error message
                // "You don't have permission to delete this appointment"
            }
        }
        refreshAppointments();
    }
    
    // ============================================================
    // NEW METHOD: Get appointments for specific patient
    // Useful for other controllers or reports
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
    // NEW METHOD: Get appointments for specific clinician
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
    // NEW METHOD: Clear current user IDs (for logout)
    // ============================================================
    public void clearCurrentUser() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
    }
}