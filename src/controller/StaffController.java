package controller;

import model.Staff;
import model.StaffRepository;
import view.StaffView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class StaffController {

    // Core data repository for staff information management
    private final StaffRepository repository;
    // UI component for displaying and interacting with staff data
    private final StaffView view;
    // User context tracking for access control and filtering
    private String currentUserId;
    private String currentStaffId;

    // Constructor - initializes controller with required dependencies
    public StaffController(StaffRepository repository, StaffView view) {
        this.repository = repository;
        this.view = view;
        this.view.setController(this);
        // Configure secure default UI state (requires login)
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - Please Login");
        refreshView();
    }
    
    // Configures controller for staff user access (self-management)
    public void setCurrentStaffId(String staffId) {
        this.currentStaffId = staffId;
        
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF VIEW: Limited to managing own profile only
            view.setReadOnlyMode(false); // Allow editing own information
            view.hideAddDeleteButtons(); // Cannot add/delete other staff
            view.showUpdateButton();     // Can update own profile
            view.setTitle("My Profile");
        } else {
            // ADMIN VIEW: Full system access for staff management
            view.setReadOnlyMode(false);
            view.showAllButtons();       // Full CRUD capabilities
            view.setTitle("Staff Management");
            view.setNextId(repository.generateNewId()); // Prepare for new staff
        }
        
        refreshView(); // Update display with filtered data
    }
    
    // Configures view for staff members (legacy method)
    public void setStaffView() {
        this.currentStaffId = null;
        refreshView();
    }
    //Made By Misbah Al Rehman. SRN: 24173647
    // Returns the view component for UI integration
    public StaffView getView() {
        return view;
    }
    
    // Configures administrative view with complete system control
    public void setAdminView() {
        this.currentStaffId = null; // Clear staff ID for admin context
        
        // ADMIN VIEW: Full access to all staff management functions
        view.setReadOnlyMode(false);
        view.showAllButtons(); // Enable all CRUD operations
        view.setTitle("Staff Management (Admin Mode)");
        view.setNextId(repository.generateNewId()); // Generate next ID
        
        refreshView(); // Display all staff records
    }

    // Refreshes staff display based on user permissions
    public void refreshView() {
        List<Staff> staffToShow;
        
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF VIEW: Show only logged-in staff member's profile
            Staff currentStaff = repository.findById(currentStaffId);
            staffToShow = new ArrayList<>();
            if (currentStaff != null) {
                staffToShow.add(currentStaff);
            }
        } else {
            // ADMIN VIEW: Show complete staff directory
            staffToShow = repository.getAll();
        }
        
        view.showStaff(staffToShow);
    }

    // Creates new staff record with permission validation
    public void addStaff(Staff s) {
        // Security check: staff cannot create other staff accounts
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Staff members cannot add new staff records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only administrators can add new staff members
        repository.addAndAppend(s);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next available ID
        
        JOptionPane.showMessageDialog(view, 
            "Staff added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Updates staff information with appropriate permission checks
    public void updateStaff(Staff s) {
        // Retrieve original record for permission validation
        Staff original = repository.findById(s.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Staff not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions based on role
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF: Can only update their own profile
            if (!s.getId().equals(currentStaffId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Staff members can update their own information
            repository.update(s);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // ADMIN: Can update any staff record
            repository.update(s);
            JOptionPane.showMessageDialog(view, 
                "Staff updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

    // Deletes staff record with comprehensive permission checks
    public void deleteStaff(Staff s) {
        // Security check: staff cannot delete any staff records
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Staff members cannot delete staff records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only admins can delete staff members
        repository.remove(s);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next available ID
        
        JOptionPane.showMessageDialog(view, 
            "Staff deleted successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Retrieves staff member 
    public Staff findById(String id) {
        return repository.findById(id);
    }
    
    // Deletes staff member by ID 
    public void deleteById(String id) {
        Staff staff = repository.findById(id);
        if (staff != null) {
            deleteStaff(staff);
        }
    }
    
    // Checks if current view is staff-restricted (not admin)
    public boolean isStaffView() {
        return currentStaffId != null && !currentStaffId.isEmpty();
    }
}