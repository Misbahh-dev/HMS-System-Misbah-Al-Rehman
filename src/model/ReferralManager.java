package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReferralManager {

    private static ReferralManager instance;
    private static boolean initialized = false;  // ADDED: Track initialization

    private final ReferralRepository referralRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final String referralTextPath;

    private ReferralManager(ReferralRepository rr,
                            PatientRepository pr,
                            ClinicianRepository cr,
                            FacilityRepository fr,
                            String referralTextPath) {

        this.referralRepository = rr;
        this.patientRepository = pr;
        this.clinicianRepository = cr;
        this.facilityRepository = fr;
        this.referralTextPath = referralTextPath;
    }


    // Singleton access - Now with initialization guard
    public static synchronized ReferralManager getInstance(
            ReferralRepository rr,
            PatientRepository pr,
            ClinicianRepository cr,
            FacilityRepository fr,
            String referralTextPath) {

        if (instance == null) {
            // First time initialization - create instance
            instance = new ReferralManager(rr, pr, cr, fr, referralTextPath);
            initialized = true;
        } else if (!initialized) {
            // This shouldn't happen, but just in case
            instance = new ReferralManager(rr, pr, cr, fr, referralTextPath);
            initialized = true;
        }
        // If already initialized, return existing instance (ignore new parameters)
        
        return instance;
    }
    
    // ============================================================
    // ADDED: Alternative getInstance() without parameters
    // (for getting the instance after it's been initialized)
    // ============================================================
    public static synchronized ReferralManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "ReferralManager not initialized. Call getInstance(with parameters) first."
            );
        }
        return instance;
    }
    
    // ============================================================
    // ADDED: Check if already initialized (optional)
    // ============================================================
    public static synchronized boolean isInitialized() {
        return initialized;
    }

    public void createReferral(Referral r) {
        referralRepository.addAndAppend(r);
        writeReferralText(r);
    }

    public List<Referral> getAllReferrals() {
        return referralRepository.getAll();
    }

    // ============================================================
    // ADDED: UPDATE REFERRAL METHOD
    // ============================================================
    public void updateReferral(Referral r) {
        referralRepository.update(r);
        // Also update the text file
        writeReferralText(r);
    }

    // ============================================================
    // ADDED: DELETE REFERRAL METHOD
    // ============================================================
    public void deleteReferral(String id) {
        // Find the referral first
        Referral referralToDelete = null;
        for (Referral r : referralRepository.getAll()) {
            if (r.getId().equals(id)) {
                referralToDelete = r;
                break;
            }
        }
        
        if (referralToDelete != null) {
            referralRepository.removeById(id);
            // Append deletion note to text file
            writeDeletionNote(referralToDelete);
        }
    }

    // ============================================================
    // ADDED: WRITE DELETION NOTE METHOD
    // ============================================================
    private void writeDeletionNote(Referral r) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(referralTextPath, true))) {
            bw.write("==============================================");
            bw.newLine();
            bw.write("            REFERRAL DELETED / CANCELLED      ");
            bw.newLine();
            bw.write("==============================================");
            bw.newLine();
            bw.write("Referral ID: " + r.getId());
            bw.newLine();
            bw.write("Patient ID: " + r.getPatientId());
            bw.newLine();
            bw.write("Reason for Referral: " + r.getReferralReason());
            bw.newLine();
            bw.write("Deleted Date: " + java.time.LocalDate.now().toString());
            bw.newLine();
            bw.write("----------------------------------------------");
            bw.newLine();
            bw.newLine();
        } catch (IOException ex) {
            System.err.println("Failed to write deletion note: " + ex.getMessage());
        }
    }

    //referaltext
    private void writeReferralText(Referral r) {

        Patient patient = patientRepository.findById(r.getPatientId());
        Clinician referringClinician = clinicianRepository.findById(r.getReferringClinicianId());
        Clinician referredToClinician = clinicianRepository.findById(r.getReferredToClinicianId());
        Facility referringFacility = facilityRepository.findById(r.getReferringFacilityId());
        Facility referredToFacility = facilityRepository.findById(r.getReferredToFacilityId());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(referralTextPath, true))) {

            bw.write("==============================================");
            bw.newLine();
            bw.write("            REFERRAL SUMMARY REPORT           ");
            bw.newLine();
            bw.write("==============================================");
            bw.newLine();

            bw.write("Referral ID: " + r.getId());
            bw.newLine();

            // Patient details
            if (patient != null) {
                bw.write("Patient: " + patient.getName() + " (NHS: " + patient.getNhsNumber() + ")");
                bw.newLine();
            }

           // Referring Clinician
if (referringClinician != null) {
    bw.write("Referring Clinician: " 
        + referringClinician.getFullName()
        + " (" + referringClinician.getTitle()
        + " - " + referringClinician.getSpeciality() + ")");
    bw.newLine();
}

// Referred-To Clinician
if (referredToClinician != null) {
    bw.write("Referred To: " 
        + referredToClinician.getFullName()
        + " (" + referredToClinician.getTitle()
        + " - " + referredToClinician.getSpeciality() + ")");
    bw.newLine();
}


            // Facilities
            if (referringFacility != null) {
                bw.write("Referring Facility: " + referringFacility.getName() +
                         " (" + referringFacility.getType() + ")");
                bw.newLine();
            }

            if (referredToFacility != null) {
                bw.write("Referred To Facility: " + referredToFacility.getName() +
                         " (" + referredToFacility.getType() + ")");
                bw.newLine();
            }

            // Dates, urgency, reason
            bw.write("Referral Date: " + r.getReferralDate());
            bw.newLine();

            bw.write("Urgency Level: " + r.getUrgencyLevel());
            bw.newLine();

            bw.write("Reason for Referral: " + r.getReferralReason());
            bw.newLine();

            bw.write("Requested Service: " + r.getRequestedService());
            bw.newLine();

            bw.write("Status: " + r.getStatus());
            bw.newLine();

            // Clinical Summary
            bw.write("Clinical Summary:");
            bw.newLine();
            bw.write(r.getClinicalSummary());
            bw.newLine();

            // Notes
            bw.write("Notes:");
            bw.newLine();
            bw.write(r.getNotes());
            bw.newLine();

            bw.write("Created Date: " + r.getCreatedDate());
            bw.newLine();

            bw.write("Last Updated: " + r.getLastUpdated());
            bw.newLine();

            bw.write("----------------------------------------------");
            bw.newLine();
            bw.newLine();

        } catch (IOException ex) {
            System.err.println("Failed to write referral text: " + ex.getMessage());
        }
    }
}