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
    private String currentUserId; // For filtering staff data

    public StaffController(StaffRepository repository, StaffView view) {
        this.repository = repository;
        this.view = view;
        this.view.setController(this);
        // Start with all buttons hidden and read-only
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - please login");
        refreshView();
    }
    
    // ============================================================
    // Method to set user role (called by LoginController)
    // ============================================================
 public void setUserRole(String userRole, String userId) {
    this.currentUserId = userId;
    
    if   ( "staff".equals(userRole) || "admin".equals(userRole)) {
        // Staff/Admin can view and manage all staff
        view.setReadOnlyMode(false);
        view.showAllButtons();
        view.setTitle("Staff Management"); // UPDATES THE TITLE!
        view.setNextId(repository.generateNewId());
    } else {
        // Patients/Clinicians typically don't have access to staff management
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - Access Denied");
    }
     
    refreshView();
}
    
    // ============================================================
    // Show all staff (no filtering for now)
    // ============================================================
    public void refreshView() {
        List<Staff> staffToShow = repository.getAll();
        view.showStaff(staffToShow);
    }

    // ============================================================
    // ADD STAFF - Only for staff/admin
    // ============================================================
    public void addStaff(Staff s) {
        repository.addAndAppend(s);
        refreshView();
        view.setNextId(repository.generateNewId()); // Update next ID
        
        JOptionPane.showMessageDialog(view, 
            "Staff added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // UPDATE STAFF - Only for staff/admin
    // ============================================================
    public void updateStaff(Staff s) {
        repository.update(s);
        refreshView();
        
        JOptionPane.showMessageDialog(view, 
            "Staff updated successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // DELETE STAFF - Only for staff/admin
    // ============================================================
    public void deleteStaff(Staff s) {
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
    // Clear current user (for logout)
    // ============================================================
    public void clearCurrentUser() {
        this.currentUserId = null;
        // Reset to default state
        view.setReadOnlyMode(true);
        view.hideAllButtons();
        view.setTitle("Staff Management - Please Login");
        view.setNextId("");
        refreshView();
    }
    
    public StaffView getView() {
        return view;
    }
}