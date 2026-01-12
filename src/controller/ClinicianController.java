package controller;

import model.Clinician;
import model.ClinicianRepository;
import view.ClinicianView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ClinicianController {

    public final ClinicianRepository repository;
    private final ClinicianView view;
    private String currentUserId; // ADDED: Store user ID for logout
    private String currentUserRole; // ADDED: Store user role for logout
    private String currentClinicianId; // For filtering clinician data
    
    // ============================================================
    // ADDED: Field to track next ID for adding new clinicians
    // ============================================================
    private String nextClinicianId;

    public ClinicianController(ClinicianRepository repo, ClinicianView view) {
        this.repository = repo;
        this.view = view;
        this.view.setController(this);
        // ============================================================
        // ADDED: Initialize next ID and setup initial view state
        // ============================================================
        this.nextClinicianId = repository.generateNewId();
        view.setNextId(nextClinicianId);
        setupForUserRole(); // Initialize UI based on user role
        refresh();
    }
    
    // ============================================================
    // ADDED: Setup view based on user role
    // ============================================================
    private void setupForUserRole() {
        // Initially assume clinician view (most restrictive)
        view.setReadOnlyMode(true);
        view.hideAddDeleteButtons();
        view.showUpdateButton();
        view.setTitle("My Profile");
    }
    
    // ============================================================
    // NEW METHOD: Set current clinician ID for filtering
    // Called by LoginController when clinician logs in
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        this.currentUserId = clinicianId; // ADDED: Store user ID
        this.currentUserRole = "CLINICIAN"; // ADDED: Store user role
        
        // ============================================================
        // ADDED: Update UI based on whether it's a clinician or staff/admin
        // ============================================================
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // CLINICIAN VIEW: Read-only mode except for own updates
            view.setReadOnlyMode(false); // Allow updates to own info
            view.hideAddDeleteButtons(); // Clinicians can't add/delete other clinicians
            view.showUpdateButton(); // But can update their own info
            view.setTitle("My Profile");
        } else {
            // STAFF/ADMIN VIEW: Full access
            view.setReadOnlyMode(false);
            view.showAllButtons(); // Show Add, Update, and Delete buttons
            view.setTitle("Clinician Management");
            view.setNextId(repository.generateNewId()); // Set next ID for adding
        }
        
        refresh(); // Refresh to potentially show filtered data
    }
    
    // ============================================================
    // ADDED: Unified method for LoginController (optional)
    // ============================================================
    public void setUserContext(String userId, String role) {
        if ("CLINICIAN".equals(role)) {
            setCurrentClinicianId(userId);
        } else {
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
    
    // ============================================================
    // ID GENERATOR
    // ============================================================
    public String generateId() {
        return repository.generateNewId();
    }

    // ============================================================
    // MODIFIED: Show filtered clinicians based on user role
    // ============================================================
    public void refresh() {
        List<Clinician> cliniciansToShow;
        
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // If a clinician is logged in, they only see their own profile
            Clinician currentClinician = repository.findById(currentClinicianId);
            cliniciansToShow = new ArrayList<>();
            if (currentClinician != null) {
                cliniciansToShow.add(currentClinician);
            }
        } else {
            // Staff/Admin/Patient view: Show all clinicians
            cliniciansToShow = repository.getAll();
        }
        
        view.showClinicians(cliniciansToShow);
    }

    // ============================================================
    // MODIFIED: Add security checks for clinician creation
    // ============================================================
    public void addClinician(Clinician c) {
        // Security check: Clinicians shouldn't be able to add other clinicians
        // Only staff/admin should be able to add clinicians
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // ============================================================
            // ADDED: Show error message for clinicians trying to add
            // ============================================================
            JOptionPane.showMessageDialog(view, 
                "Clinicians cannot add new clinician records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        repository.addAndAppend(c);
        refresh();
        
        // ============================================================
        // ADDED: Update next ID and show success message
        // ============================================================
        nextClinicianId = repository.generateNewId();
        view.setNextId(nextClinicianId);
        JOptionPane.showMessageDialog(view, 
            "Clinician added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ============================================================
    // ADDED: Update clinician method with proper permissions
    // ============================================================
    public void updateClinician(Clinician c) {
        // Find the original clinician to check permissions
        Clinician original = repository.findById(c.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Clinician not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions
        if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
            // CLINICIAN: Can only update their own record
            if (!c.getId().equals(currentClinicianId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Clinicians can update their own info
            repository.update(c);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // STAFF/ADMIN: Can update any clinician
            repository.update(c);
            JOptionPane.showMessageDialog(view, 
                "Clinician updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refresh();
    }

    // ============================================================
    // MODIFIED: Add security checks for clinician deletion
    // ============================================================
    public void deleteById(String id) {
        Clinician c = repository.findById(id);
        if (c != null) {
            // Security check based on user role
            boolean canDelete = true;
            
            if (currentClinicianId != null && !currentClinicianId.isEmpty()) {
                // A clinician is logged in
                if (c.getId().equals(currentClinicianId)) {
                    // Clinician trying to delete themselves
                    JOptionPane.showMessageDialog(view, 
                        "You cannot delete your own account.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    canDelete = false;
                } else {
                    // Clinician trying to delete another clinician
                    JOptionPane.showMessageDialog(view, 
                        "Clinicians cannot delete other clinician records.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    canDelete = false;
                }
            }
            
            // Staff/Admin can delete any clinician (no currentClinicianId set)
            
            if (canDelete) {
                repository.remove(c);
                refresh();
                // ============================================================
                // ADDED: Update next ID after deletion
                // ============================================================
                nextClinicianId = repository.generateNewId();
                view.setNextId(nextClinicianId);
                JOptionPane.showMessageDialog(view, 
                    "Clinician deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // ============================================================
    // NEW METHOD: Get current clinician (useful for other controllers)
    // ============================================================
    public Clinician getCurrentClinician() {
        if (currentClinicianId != null) {
            return repository.findById(currentClinicianId);
        }
        return null;
    }
    
    // ============================================================
    // NEW METHOD: Check if user is viewing their own data
    // ============================================================
    public boolean isViewingOwnData(String clinicianId) {
        return currentClinicianId != null && currentClinicianId.equals(clinicianId);
    }
    
    // ============================================================
    // ADDED: Check if current user is a clinician
    // ============================================================
    public boolean isClinicianView() {
        return currentClinicianId != null && !currentClinicianId.isEmpty();
    }
    
    // ============================================================
    // NEW METHOD: Clear current clinician ID (for logout)
    // ============================================================
    public void clearCurrentClinician() {
        this.currentClinicianId = null;
        // ============================================================
        // ADDED: Reset to initial state on logout
        // ============================================================
        setupForUserRole();
    }
    
    // ============================================================
    // NEW METHOD: Clear current user (for logout) - REQUIRED BY LoginController
    // ============================================================
    public void clearCurrentUser() {
        this.currentUserId = null;
        this.currentUserRole = null;
        this.currentClinicianId = null;
        setupForUserRole(); // Reset to default state
    }
    
    // ============================================================
    // NEW METHOD: Get clinicians by specialty (if needed)
    // ============================================================
    public List<Clinician> getCliniciansBySpecialty(String specialty) {
        List<Clinician> filtered = new ArrayList<>();
        for (Clinician c : repository.getAll()) {
            if (c.getSpeciality().equalsIgnoreCase(specialty)) {
                filtered.add(c);
            }
        }
        return filtered;
    }
    
    // ============================================================
    // ADDED: Get all clinician IDs for dropdowns in other views
    // ============================================================
    public List<String> getAllClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : repository.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }
}