package view;

import controller.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabs;
    
    // ============================================================
    // CHANGE 1: ADDED USER ROLE PARAMETER
    // ============================================================
    public MainFrame(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc,
            String userRole) {  // ADDED: User role for access control

        // ============================================================
        // CHANGE 2: UPDATED WINDOW TITLE WITH USER ROLE
        // ============================================================
        super("Healthcare Management System - Logged in as: " + userRole);

        tabs = new JTabbedPane();
        
        // ============================================================
        // CHANGE 3: ADDED ALL TABS (SAME AS BEFORE)
        // ============================================================
        tabs.addTab("Patients", pc.getView());
        tabs.addTab("Clinicians", cc.getView());
        tabs.addTab("Appointments", ac.getView());
        tabs.addTab("Prescriptions", prc.getView());
        tabs.addTab("Referrals", rc.getView());
        
        setContentPane(tabs);
        
        // ============================================================
        // CHANGE 4: SET LARGER DEFAULT SIZE FOR BETTER VIEWING
        // ============================================================
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // ============================================================
        // CHANGE 5: APPLY ROLE-BASED ACCESS CONTROL
        // ============================================================
        applyRoleBasedAccess(userRole);
    }
    
    // ============================================================
    // NEW METHOD: APPLY ROLE-BASED ACCESS CONTROL
    // ============================================================
    private void applyRoleBasedAccess(String userRole) {
        if (userRole == null) {
            return; // No restrictions if role is null
        }
        
        // Convert role to lowercase for case-insensitive comparison
        String role = userRole.toLowerCase();
        
        // ============================================================
        // ROLE-BASED ACCESS RULES:
        // ============================================================
        switch (role) {
            case "patient":
                // Patients can only view Appointments and Prescriptions
                enableOnlyTabs(new String[]{"Appointments", "Prescriptions"});
                showAccessMessage("Patient Access: You can view and manage your appointments and prescriptions.");
                break;
                
            case "clinician":
                // Clinicians can manage everything except Clinician records
                enableOnlyTabs(new String[]{"Patients", "Appointments", "Prescriptions", "Referrals"});
                showAccessMessage("Clinician Access: You can manage patients, appointments, prescriptions, and referrals.");
                break;
                
            case "staff":
                // Staff have full access (all tabs enabled)
                enableAllTabs();
                showAccessMessage("Staff Access: Full administrative access to all modules.");
                break;
                
            case "admin":
                // Admin have full access (all tabs enabled)
                enableAllTabs();
                showAccessMessage("Admin Access: Full system administrator privileges.");
                break;
                
            default:
                // Default: enable all tabs for unknown roles
                enableAllTabs();
                showAccessMessage("Standard Access: All modules available.");
                break;
        }
    }
    
    // ============================================================
    // NEW METHOD: ENABLE ONLY SPECIFIC TABS
    // ============================================================
    private void enableOnlyTabs(String[] enabledTabNames) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            String tabTitle = tabs.getTitleAt(i);
            boolean enabled = false;
            
            // Check if this tab is in the enabled list
            for (String enabledTab : enabledTabNames) {
                if (tabTitle.equals(enabledTab)) {
                    enabled = true;
                    break;
                }
            }
            
            tabs.setEnabledAt(i, enabled);
        }
    }
    
    // ============================================================
    // NEW METHOD: ENABLE ALL TABS
    // ============================================================
    private void enableAllTabs() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, true);
        }
    }
    
    // ============================================================
    // NEW METHOD: SHOW ACCESS MESSAGE TO USER
    // ============================================================
    private void showAccessMessage(String message) {
        JOptionPane.showMessageDialog(this,
            message + "\n\nEnabled tabs are marked available.",
            "Access Level Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ============================================================
    // OPTIONAL: METHOD TO PROGRAMMATICALLY SWITCH TABS
    // ============================================================
    public void switchToTab(String tabName) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (tabs.getTitleAt(i).equals(tabName)) {
                tabs.setSelectedIndex(i);
                break;
            }
        }
    }
}