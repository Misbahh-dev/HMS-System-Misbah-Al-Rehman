package controller;

import model.Clinician;
import model.ClinicianRepository;
import view.ClinicianView;
import java.util.List;
import java.util.ArrayList;

public class ClinicianController {

    public final ClinicianRepository repository;
    private final ClinicianView view;
    private String currentClinicianId; // ADDED: For filtering clinician data

    public ClinicianController(ClinicianRepository repo, ClinicianView view) {
        this.repository = repo;
        this.view = view;
        this.view.setController(this);
        refresh();
    }
    
    // ============================================================
    // NEW METHOD: Set current clinician ID for filtering
    // Called by LoginController when clinician logs in
    // ============================================================
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        refresh(); // Refresh to potentially show filtered data
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
            // If a clinician is logged in, they might only see their own data
            // OR they could see all clinicians (depending on your design choice)
            
            // OPTION 1: Clinicians can only see their own profile
            Clinician currentClinician = repository.findById(currentClinicianId);
            cliniciansToShow = new ArrayList<>();
            if (currentClinician != null) {
                cliniciansToShow.add(currentClinician);
            }
            
            // OPTION 2: Clinicians can see all clinicians (comment out Option 1, uncomment below)
            // cliniciansToShow = repository.getAll();
            
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
            // A clinician is logged in - they shouldn't add new clinicians
            // You could show error: "Only administrators can add new clinicians"
            return;
        }
        
        repository.addAndAppend(c);
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
                    // Clinician trying to delete themselves - maybe allow this
                    // Or show warning: "You cannot delete your own account"
                    canDelete = false;
                } else {
                    // Clinician trying to delete another clinician - not allowed
                    canDelete = false;
                }
            }
            
            // Staff/Admin can delete any clinician (no currentClinicianId set)
            
            if (canDelete) {
                repository.remove(c);
            } else {
                // Optional: Show error message
                // "You don't have permission to delete this clinician"
            }
        }
        refresh();
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
    // NEW METHOD: Clear current clinician ID (for logout)
    // ============================================================
    public void clearCurrentClinician() {
        this.currentClinicianId = null;
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
}