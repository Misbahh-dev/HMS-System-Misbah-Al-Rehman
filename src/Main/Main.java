package Main;

import controller.*;
import model.*;
import view.*;
import javax.swing.SwingUtilities;

public class Main {

public static void main(String[] args) {

    SwingUtilities.invokeLater(() -> {

        // ================================
        // REPOSITORIES
        // ================================
        PatientRepository pr =
                new PatientRepository("src/data/patients.csv");

        ClinicianRepository cr =
                new ClinicianRepository("src/data/clinicians.csv");

        FacilityRepository fr =
                new FacilityRepository("src/data/facilities.csv");

        AppointmentRepository ar =
                new AppointmentRepository("src/data/appointments.csv");

        PrescriptionRepository pResR =
                new PrescriptionRepository("src/data/prescriptions.csv");

        ReferralRepository rR =
                new ReferralRepository("src/data/referrals.csv");

        StaffRepository sR = 
                new StaffRepository("src/data/staff.csv");

        //================================
        // LOGIN REPOSITORY
        // ================================
        LoginRepository logR = new LoginRepository(pr, cr, sR);

        // ================================
        // REFERRAL MANAGER (Singleton)
        // ================================
        ReferralManager rm = ReferralManager.getInstance(
                rR, pr, cr, fr,
                "src/data/referrals_output.txt"
        );

        // ================================
        // VIEWS
        // ================================
        PatientView pv = new PatientView();
        ClinicianView cv = new ClinicianView();
        AppointmentView av = new AppointmentView();
        PrescriptionView presV = new PrescriptionView();
        ReferralView rv = new ReferralView();
        StaffView sv = new StaffView();
        //==============================
        // LOGIN VIEW - ADDED
        //==============================
        LoginView logview = new LoginView();

        // ================================
        // CONTROLLERS (MATCHING YOUR CONSTRUCTORS)
        // ================================
        PatientController pc = new PatientController(pr, ar, pv);

        ClinicianController cc = new ClinicianController(cr, cv);

        StaffController sc = new StaffController(sR, sv); 

        AppointmentController ac = new AppointmentController(
                ar,   // AppointmentRepository
                pr,   // PatientRepository
                cr,   // ClinicianRepository
                fr,   // FacilityRepository
                av    // AppointmentView
        );

        PrescriptionController prc = new PrescriptionController(
                pResR,
                pr,
                cr,
                ar,
                presV
        );

        ReferralController rc = new ReferralController(
                rm,   // ReferralManager
                pr,   // PatientRepository
                cr,   // ClinicianRepository
                fr,   // FacilityRepository
                ar,   // AppointmentRepository
                rv    // ReferralView
        );


        //============================
        // LOGIN CONTROLLER - ADDED
        //===========================
        LoginController lc = new LoginController(
                logview,
                logR
        );

        // ============================================================
        // CHANGE 1: SET MAIN CONTROLLERS FOR LOGIN CONTROLLER
        // This allows LoginController to open MainFrame after login
        // ============================================================
        lc.setMainControllers(pc, cc, ac, prc, rc, sc);

        // ============================================================
        // CHANGE 2: CREATE LOGIN WINDOW INSTEAD OF DIRECT MAINFRAME
        // ============================================================
        javax.swing.JFrame loginWindow = new javax.swing.JFrame("Healthcare Management System - Login");
        loginWindow.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        loginWindow.setContentPane(logview);  // Add LoginView (JPanel) to JFrame
        loginWindow.pack();
        loginWindow.setLocationRelativeTo(null);
        loginWindow.setVisible(true);

        // ============================================================
        // CHANGE 3: REMOVE DIRECT MAINFRAME CREATION
        // MainFrame will be opened by LoginController after successful login
        // ============================================================
        // OLD CODE (COMMENTED OUT):
        // MainFrame frame = new MainFrame(pc, cc, ac, prc, rc);
        // frame.setVisible(true);
    });
}
}