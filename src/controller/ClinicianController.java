package controller;

import model.Clinician;
import model.ClinicianRepository;
import view.ClinicianView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ClinicianController {
//Made By Misbah Al Rehman. SRN: 24173647
    // Data repository for clinician information
    public final ClinicianRepository repository;
    // UI component for displaying clinician data
    private final ClinicianView view;
    
    // User context tracking for access control
    private String currentUserId;    
    private String currentUserRole;  
    private String currentClinicianId; 
    
    // Next available ID for new clinician records
    private String nextClinicianId;

    public ClinicianController(ClinicianRepository repo, ClinicianView view) {
        this.repository = repo;
        this.view = view;
        this.view.setController(this);
        
        // Initialize next ID and configure default UI state
        this.nextClinicianId = repository.generateNewId();
        view.setNextId(nextClinicianId);
        setupForUserRole();
        refresh();
    }
    
    // Configures initial UI state for security purposes
    private void setupForUserRole() {
        view.setReadOnlyMode(true);
        view.hideAddDeleteButtons();
        view.showUpdateButton();
        view.setTitle("My Profile");
    }
    
    // Configures controller for logged-in clinician access
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentUserId = clinicianId;
        this.currentUserRole = "CLINICIAN";
        
        // Set appropriate UI permissions based on user type
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinician view: Limited access to own data only
            view.setReadOnlyMode(false);
            view.hideAddDeleteButtons();
            view.showUpdateButton();
            view.setTitle("My Profile");
        } else {
            // Staff/admin view: Full system access
            view.setReadOnlyMode(false);
            view.showAllButtons();
            view.setTitle("Clinician Management");
            view.setNextId(repository.generateNewId());
        }
        
        refresh();
    }
    
    // Sets user context from login system
    public void setUserContext(String userId, String role) {
        if ("CLINICIAN".equals(role)) {
            setCurrentClinicianId(userId);
        } else {
            // Staff/admin users get full access
            this.currentUserId = null;
            this.currentUserRole = role;
            this.currentClinicianId = null;
            view.setReadOnlyMode(false);
            view.showAllButtons();
            view.setTitle("Clinician Management");
            view.setNextId(repository.generateNewId());
            refresh();
        }
    }
    
    public ClinicianView getView() {
        return view;
    }
    
    // Generates next unique clinician identifier
    public String generateId() {
        return repository.generateNewId();
    }

    // Refreshes clinician display based on user permissions
    public void refresh() {
        List<Clinician> cliniciansToShow;
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Show only logged-in clinician's profile
            Clinician currentClinician = repository.findById(currentClinicianId);
            cliniciansToShow = new ArrayList<>();
            if (currentClinician != null) {
                cliniciansToShow.add(currentClinician);
            }
        } else {
            // Show all clinicians for staff/admin view
            cliniciansToShow = repository.getAll();
        }
        
        view.showClinicians(cliniciansToShow);
    }

    // Adds new clinician with appropriate permission checks
    public void addClinician(Clinician c) {
        // Prevent clinicians from creating other clinician accounts
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Clinicians cannot add new clinician records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        repository.addAndAppend(c);
        refresh();
        
        // Update next available ID after successful addition
        nextClinicianId = repository.generateNewId();
        view.setNextId(nextClinicianId);
        JOptionPane.showMessageDialog(view, 
            "Clinician added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Updates clinician information with permission validation
    public void updateClinician(Clinician c) {
        Clinician original = repository.findById(c.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Clinician not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Apply role-based update permissions
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // Clinicians can only update their own profiles
            if (!c.getId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            repository.update(c);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // Staff/admin can update any clinician record
            repository.update(c);
            JOptionPane.showMessageDialog(view, 
                "Clinician updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refresh();
    }

    // Deletes clinician with comprehensive permission checks
    public void deleteById(String id) {
        Clinician c = repository.findById(id);
        if (c != null) {
            boolean canDelete = true;
            
            if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
                // Clinicians cannot delete any clinician records
                if (c.getId().equals(currentClinicianId)) {
                    JOptionPane.showMessageDialog(view, 
                        "You cannot delete your own account.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    canDelete = false;
                } else {
                    JOptionPane.showMessageDialog(view, 
                        "Clinicians cannot delete other clinician records.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    canDelete = false;
                }
            }
            
            if (canDelete) {
                repository.remove(c);
                refresh();
                nextClinicianId = repository.generateNewId();
                view.setNextId(nextClinicianId);
                JOptionPane.showMessageDialog(view, 
                    "Clinician deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // Returns currently logged-in clinician object
    public Clinician getCurrentClinician() {
        if (currentClinicianId != null) {
            return repository.findById(currentClinicianId);
        }
        return null;
    }
    
    // Checks if user is viewing their own clinician data
    public boolean isViewingOwnData(String clinicianId) {
        return currentClinicianId != null && currentClinicianId.equals(clinicianId);
    }
    
    // Determines if current view is clinician-restricted
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
    
    // Clears current clinician context for logout
    public void clearCurrentClinician() {
        this.currentClinicianId = null;
        setupForUserRole();
    }
    
    // Filters clinicians by medical specialty
    public List<Clinician> getCliniciansBySpecialty(String specialty) {
        List<Clinician> filtered = new ArrayList<>();
        for (Clinician c : repository.getAll()) {
            if (c.getSpeciality().equalsIgnoreCase(specialty)) {
                filtered.add(c);
            }
        }
        return filtered;
    }
    
    // Returns all clinician IDs for selection purposes
    public List<String> getAllClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : repository.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }
}