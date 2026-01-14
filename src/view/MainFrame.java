package view;

import controller.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabs;
    
    public MainFrame(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc,
            StaffController sc,
            String userRole) {

        super("Healthcare Management System - Logged in as: " + userRole);

        tabs = new JTabbedPane();
        
        // ============================================================
        // CHANGE: ADD TABS BASED ON USER ROLE (HIDE OTHERS)
        // ============================================================
        addTabsBasedOnRole(pc, cc, ac, prc, rc, sc, userRole);
        
        setContentPane(tabs);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Show access message
        showAccessMessage(userRole);
    }
    
    // ============================================================
    // NEW METHOD: ADD ONLY TABS USER CAN ACCESS
    // ============================================================
    private void addTabsBasedOnRole(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc,
            StaffController sc,
            String userRole) {
        
        if (userRole == null) {
            userRole = "guest"; // Default if no role
        }
        
        String role = userRole.toLowerCase();
        
        switch (role) {
            case "patient":
                // Patients can only see Appointments and Prescriptions
                tabs.addTab("My Appointments", ac.getView());
                tabs.addTab("My Prescriptions", prc.getView());
                tabs.addTab("My Profile", pc.getView());
                break;
                
            case "clinician":
                // Clinicians can manage everything except Clinician records
                tabs.addTab("Patients", pc.getView());
                tabs.addTab("Appointments", ac.getView());
                tabs.addTab("Prescriptions", prc.getView());
                tabs.addTab("Referrals", rc.getView());
                tabs.addTab("My Profile", cc.getView());
                break;
                
            case "staff":
                tabs.addTab("Patients", pc.getView());
                tabs.addTab("Clinicians", cc.getView());
                tabs.addTab("Appointments", ac.getView());
                tabs.addTab("Prescriptions", prc.getView());
                tabs.addTab("Referrals", rc.getView());
                tabs.addTab("My Profile", sc.getView());
                break;
                
                
            case "admin":
                // Staff and Admin can see everything
                tabs.addTab("Patients", pc.getView());
                tabs.addTab("Clinicians", cc.getView());
                tabs.addTab("Appointments", ac.getView());
                tabs.addTab("Prescriptions", prc.getView());
                tabs.addTab("Referrals", rc.getView());
                tabs.addTab("Staff", sc.getView());
                break;
                
            default:
                // Guest/Unknown: Show all tabs
                tabs.addTab("Patients", pc.getView());
                tabs.addTab("Clinicians", cc.getView());
                tabs.addTab("Appointments", ac.getView());
                tabs.addTab("Prescriptions", prc.getView());
                tabs.addTab("Referrals", rc.getView());
                break;
        }
    }
    
    // ============================================================
    // UPDATED METHOD: SHOW ACCESS MESSAGE
    // ============================================================
    private void showAccessMessage(String userRole) {
        String role = (userRole != null) ? userRole.toLowerCase() : "guest";
        String message = "";
        int tabCount = tabs.getTabCount();
        
        switch (role) {
            case "patient":
                message = "Patient Access: You can manage your appointments and prescriptions.";
                break;
            case "clinician":
                message = "Clinician Access: You can manage patients, appointments, prescriptions, and referrals.";
                break;
            case "staff":
                message = "Staff Access: Full administrative access to all modules.";
                break;
            case "admin":
                message = "Admin Access: Full system administrator privileges.";
                break;
            default:
                message = "Standard Access: All modules available.";
                break;
        }
        
        message += "\n\nAvailable tabs: " + tabCount;
        
        JOptionPane.showMessageDialog(this,
            message,
            "Access Level Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Optional: Method to programmatically switch tabs
    public void switchToTab(String tabName) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (tabs.getTitleAt(i).equals(tabName)) {
                tabs.setSelectedIndex(i);
                break;
            }
        }
    }
}