package controller;

import model.*;
import view.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class AppointmentController {
//Made By Misbah Al Rehman. SRN: 24173647
    // Core dependencies for managing appointment data
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentView view;
    
    // Formatter for consistent date handling
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // User context for filtering and permissions
    private String currentPatientId;
    private String currentClinicianId;
    private String currentUserRole;

    // Initializes controller with all required repositories
    public AppointmentController(AppointmentRepository repo,
                                 PatientRepository patientRepo,
                                 ClinicianRepository clinicianRepo,
                                 FacilityRepository facilityRepo,
                                 AppointmentView view) {

        // Set up repository connections for data access
        this.repo = repo;
        this.patientRepo = patientRepo;
        this.clinicianRepo = clinicianRepo;
        this.facilityRepo = facilityRepo;
        this.view = view;

        // Connect controller to view and initialize UI
        view.setController(this);
        setupForUserRole();
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }
    
    // Configures UI for default user role on startup
    private void setupForUserRole() {
        // Start with read-only patient view for security
        view.setReadOnlyMode(true);
        view.hideAddDeleteButtons();
        view.setTitle("My Appointments");
        refreshAppointments();
    }
    
    // Sets user identity and permissions after login
    public void setUserContext(String userId, String role) {
        this.currentUserRole = role;
        
        // Route to appropriate view based on user type
        if ("PATIENT".equals(role)) {
            setCurrentPatientId(userId);
        } else if ("CLINICIAN".equals(role)) {
            setCurrentClinicianId(userId);
        } else {
            // Staff or admin users get full access
            setStaffView();
        }
    }
    
    // Enables full system access for staff members
    public void setStaffView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentUserRole = "STAFF";
        
        // Grant edit permissions and show all controls
        view.setReadOnlyMode(false);
        view.showAllButtons();
        view.setTitle("Appointment Management");
        refreshAppointments();
    }
    
    // Administrative view with complete system control
    public void setAdminView() {
        this.currentPatientId = null;
        this.currentClinicianId = null;
        this.currentUserRole = "ADMIN";
        
        // Enable all administrative functions
        view.setReadOnlyMode(false);
        view.showAllButtons();
        view.setTitle("Appointment Management (Admin View)");
        refreshAppointments();
    }

    // Restricts view to only show appointments for this patient
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
        this.currentClinicianId = null;
        this.currentUserRole = "PATIENT";
        
        // Patients can book new appointments but not edit others
        view.setReadOnlyMode(false);
        view.hideUpdateButton();
        view.setTitle("My Appointments");
        
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }
    
    // Filters view to appointments assigned to this clinician
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentPatientId = null;
        this.currentUserRole = "CLINICIAN";
        
        // Clinicians can manage but not create/delete appointments
        view.setReadOnlyMode(false);
        view.hideAddDeleteButtons();
        view.setTitle("My Clinic Appointments");
        
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }

    // Returns the view component for UI display
    public AppointmentView getView() {
        return view;
    }

    // Updates appointment display based on user permissions
    public void refreshAppointments() {
        List<Appointment> appointmentsToShow;
        
        // Filter appointments based on user role
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            appointmentsToShow = getAppointmentsForPatient(currentPatientId);
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            appointmentsToShow = getAppointmentsForClinician(currentClinicianId);
        } else {
            // Staff/admin see all appointments
            appointmentsToShow = repo.getAll();
        }
        
        view.showAppointments(appointmentsToShow);
    }

    // Generates unique ID for new appointment records
    public String generateId() {
        return repo.generateNewId();
    }

    // Provides patient IDs appropriate for current user context
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patients only see their own ID
            ids.add(currentPatientId);
        } else if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinicians can see all patient IDs
            ids = patientRepo.getAllIds();
        } else {
            // Staff/admin can see all patient IDs
            ids = patientRepo.getAllIds();
        }
        return ids;
    }

    // Provides clinician IDs appropriate for current user context
    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinicians only see their own ID
            ids.add(currentClinicianId);
        } else if (currentPatientId != null && !currentPatientId.isEmpty()) {
            // Patients can see all clinician IDs
            ids = clinicianRepo.getAllIds();
        } else {
            // Staff/admin can see all clinician IDs
            ids = clinicianRepo.getAllIds();
        }
        return ids;
    }

    // Returns all facility IDs available in the system
    public List<String> getFacilityIds() {
        return facilityRepo.getAllIds();
    }

    // Adds new appointment with proper permission validation
    public void addAppointment(Appointment a) {
        // Patient-specific security checks
        if ("PATIENT".equals(currentUserRole)) {
            // Patients can only book appointments for themselves
            if (!a.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only book appointments for yourself.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Patients cannot book appointments in the past
            LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
            if (appointmentDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(view, 
                    "Cannot book appointments in the past.", 
                    "Invalid Date", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
        } else if ("CLINICIAN".equals(currentUserRole)) {
            // Clinicians can only create their own appointments
            if (!a.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only create appointments for yourself.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Save appointment to repository and update UI
        repo.addAndAppend(a);
        refreshAppointments();
        
        JOptionPane.showMessageDialog(view, 
            "Appointment added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Updates existing appointment with permission checks
    public void updateAppointment(Appointment a) {
        // Retrieve original appointment for permission validation
        Appointment original = repo.findById(a.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Appointment not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Patient-specific update permissions
        if ("PATIENT".equals(currentUserRole)) {
            if (!original.getPatientId().equals(currentPatientId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own appointments.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Your appointment has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else if ("CLINICIAN".equals(currentUserRole)) {
            // Clinician-specific update permissions
            if (!original.getClinicianId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update appointments you are assigned to.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Appointment updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // Staff/admin can update any appointment
            repo.update(a);
            JOptionPane.showMessageDialog(view, 
                "Appointment updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshAppointments();
    }

    // Deletes appointment with comprehensive permission validation
    public void deleteById(String id) {
        Appointment a = repo.findById(id);
        if (a != null) {
            // Patient-specific deletion rules
            if ("PATIENT".equals(currentUserRole)) {
                if (!a.getPatientId().equals(currentPatientId)) {
                    JOptionPane.showMessageDialog(view, 
                        "You can only cancel your own appointments.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Patients cannot cancel past appointments
                LocalDate appointmentDate = LocalDate.parse(a.getAppointmentDate(), fmt);
                if (appointmentDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(view, 
                        "Cannot cancel past appointments.", 
                        "Invalid Action", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
            } else if ("CLINICIAN".equals(currentUserRole)) {
                // Clinician-specific deletion rules
                if (!a.getClinicianId().equals(currentClinicianId)) {
                    JOptionPane.showMessageDialog(view, 
                        "You can only cancel appointments you are assigned to.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Confirm deletion with user to prevent accidental removal
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
    
    // Retrieves all appointments for a specific patient
    public List<Appointment> getAppointmentsForPatient(String patientId) {
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment a : repo.getAll()) {
            if (a.getPatientId().equals(patientId)) {
                patientAppointments.add(a);
            }
        }
        return patientAppointments;
    }
    
    // Retrieves all appointments for a specific clinician
    public List<Appointment> getAppointmentsForClinician(String clinicianId) {
        List<Appointment> clinicianAppointments = new ArrayList<>();
        for (Appointment a : repo.getAll()) {
            if (a.getClinicianId().equals(clinicianId)) {
                clinicianAppointments.add(a);
            }
        }
        return clinicianAppointments;
    }
    
    // Returns current clinician ID for permission checks
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // Returns current patient ID for permission checks
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // Returns current user role for permission checks
    public String getCurrentUserRole() {
        return currentUserRole;
    }
}