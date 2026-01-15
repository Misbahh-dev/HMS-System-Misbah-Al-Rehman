package Main;

import controller.*;
import model.*;
import view.*;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Made By Misbah Al Rehman. SRN: 24173647
            // Initialize all data repositories
            PatientRepository pr = new PatientRepository("src/data/patients.csv");
            ClinicianRepository cr = new ClinicianRepository("src/data/clinicians.csv");
            FacilityRepository fr = new FacilityRepository("src/data/facilities.csv");
            AppointmentRepository ar = new AppointmentRepository("src/data/appointments.csv");
            PrescriptionRepository pResR = new PrescriptionRepository("src/data/prescriptions.csv");
            ReferralRepository rR = new ReferralRepository("src/data/referrals.csv");
            StaffRepository sR = new StaffRepository("src/data/staff.csv");
            
            // Login repository integrates user data
            LoginRepository logR = new LoginRepository(pr, cr, sR);
            
            // Singleton manager handles referral workflows
            ReferralManager rm = ReferralManager.getInstance(rR, pr, cr, fr, "src/data/referrals_output.txt");
            
            // Initialize all view components
            PatientView pv = new PatientView();
            ClinicianView cv = new ClinicianView();
            AppointmentView av = new AppointmentView();
            PrescriptionView presV = new PrescriptionView();
            ReferralView rv = new ReferralView();
            StaffView sv = new StaffView();
            LoginView logview = new LoginView();
            
            // Initialize controllers with their dependencies
            PatientController pc = new PatientController(pr, ar, pv);
            ClinicianController cc = new ClinicianController(cr, cv);
            StaffController sc = new StaffController(sR, sv);
            
            AppointmentController ac = new AppointmentController(ar, pr, cr, fr, av);
            PrescriptionController prc = new PrescriptionController(pResR, pr, cr, ar, presV);
            
            ReferralController rc = new ReferralController(rm, pr, cr, fr, ar, rv);
            LoginController lc = new LoginController(logview, logR);
            
            // Connect login controller to main controllers
            lc.setMainControllers(pc, cc, ac, prc, rc, sc);
            
            // Create and display login window
            javax.swing.JFrame loginWindow = new javax.swing.JFrame("Healthcare Management System - Login");
            loginWindow.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            loginWindow.setContentPane(logview);
            loginWindow.pack();
            loginWindow.setLocationRelativeTo(null);
            loginWindow.setVisible(true);
        });
    }
}