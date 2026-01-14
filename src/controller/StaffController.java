package controller;

import model.Staff;
import model.StaffRepository;
import view.StaffView;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class StaffController {

    private final StaffRepository repository;
    private final StaffView view;
    private String currentUserId;
    private String currentStaffId; // For filtering staff data by staff

    public StaffController(StaffRepository repository, StaffView view) {
        this.repository = repository;
        this.view = view;
        this.view.setController(this);
        // Start with all buttons hidden and read-only
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - Please Login");
        refreshView();
    }
    
    // ============================================================
    // Set current staff ID for filtering (for staff)
    // ============================================================
    public void setCurrentStaffId(String staffId) {
        this.currentStaffId = staffId;
        
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF VIEW: Read-only mode except for own updates
            view.setReadOnlyMode(false); // Allow updates to own info
            view.hideAddDeleteButtons(); // Staff can't add/delete other staff
            view.showUpdateButton(); // But can update their own info
            view.setTitle("My Profile");
        } else {
            // ADMIN VIEW: Full access
            view.setReadOnlyMode(false);
            view.showAllButtons(); // Admin can add/update/delete all staff
            view.setTitle("Staff Management");
            view.setNextId(repository.generateNewId()); // Set next ID for adding
        }
        
        refreshView(); // Refresh to show filtered data
    }
    
    // ============================================================
    // Method for admin to view all staff
    // ============================================================
    public void setStaffView() {
        this.currentStaffId = null;
        refreshView();
    }
    
    public StaffView getView() {
        return view;
    }

    // ============================================================
    // Show filtered staff based on user role
    // ============================================================
    public void refreshView() {
        List<Staff> staffToShow;
        
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF VIEW: Show only the current staff's data
            Staff currentStaff = repository.findById(currentStaffId);
            staffToShow = new ArrayList<>();
            if (currentStaff != null) {
                staffToShow.add(currentStaff);
            }
        } else {
            // ADMIN VIEW: Show all staff
            staffToShow = repository.getAll();
        }
        
        view.showStaff(staffToShow);
    }

    // ============================================================
    // ADD STAFF - Only for admin
    // ============================================================
    public void addStaff(Staff s) {
        // If a staff is logged in, they shouldn't be able to add new staff
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Staff members cannot add new staff records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only admin can add new staff
        repository.addAndAppend(s);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
        
        JOptionPane.showMessageDialog(view, 
            "Staff added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // UPDATE STAFF - Allowed for staff (their own) and admin
    // ============================================================
    public void updateStaff(Staff s) {
        // Find the original staff to check permissions
        Staff original = repository.findById(s.getId());
        
        if (original == null) {
            JOptionPane.showMessageDialog(view, 
                "Staff not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check user permissions
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            // STAFF: Can only update their own record
            if (!s.getId().equals(currentStaffId)) {
                JOptionPane.showMessageDialog(view, 
                    "You can only update your own profile.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Staff can update their own info
            repository.update(s);
            JOptionPane.showMessageDialog(view, 
                "Your profile has been updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            // ADMIN: Can update any staff
            repository.update(s);
            JOptionPane.showMessageDialog(view, 
                "Staff updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        refreshView();
    }

    // ============================================================
    // DELETE STAFF - Only for admin, not for staff
    // ============================================================
    public void deleteStaff(Staff s) {
        // If a staff is logged in, they shouldn't be able to delete any staff
        if (currentStaffId != null && !currentStaffId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Staff members cannot delete staff records.", 
                "Access Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Only admin can delete staff
        repository.remove(s);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
        
        JOptionPane.showMessageDialog(view, 
            "Staff deleted successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public Staff findById(String id) {
        return repository.findById(id);
    }
    
    // ============================================================
    // Delete by ID
    // ============================================================
    public void deleteById(String id) {
        Staff staff = repository.findById(id);
        if (staff != null) {
            deleteStaff(staff);
        }
    }
    
    // ============================================================
    // Check if current user is a staff
    // ============================================================
    public boolean isStaffView() {
        return currentStaffId != null && !currentStaffId.isEmpty();
    }
    
    // ============================================================
    // Clear current user (for logout)
    // ============================================================
    public void clearCurrentUser() {
        this.currentUserId = null;
        this.currentStaffId = null; // Clear staff ID
        // Reset to default state
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - Please Login");
        view.setNextId("");
        refreshView();
    }
}