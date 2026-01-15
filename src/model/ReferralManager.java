package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReferralManager {

    // Singleton pattern implementation
    private static ReferralManager instance;
    private static boolean initialized = false;  // Track initialization state

    // Repository dependencies for data access
    private final ReferralRepository referralRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final String referralTextPath;  // Text file output path
//Made By Misbah Al Rehman. SRN: 24173647
    // Private constructor for singleton pattern
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


    // Singleton access with initialization parameters (first-time setup)
    public static synchronized ReferralManager getInstance(
            ReferralRepository rr,
            PatientRepository pr,
            ClinicianRepository cr,
            FacilityRepository fr,
            String referralTextPath) {

        if (instance == null) {
            // First time initialization - create new instance
            instance = new ReferralManager(rr, pr, cr, fr, referralTextPath);
            initialized = true;
        } else if (!initialized) {
            // Fallback: recreate instance if not properly initialized
            instance = new ReferralManager(rr, pr, cr, fr, referralTextPath);
            initialized = true;
        }
        // Return existing instance if already initialized (ignore new parameters)
        
        return instance;
    }
    
    // Singleton access without parameters (post-initialization)
    public static synchronized ReferralManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "ReferralManager not initialized. Call getInstance(with parameters) first."
            );
        }
        return instance;
    }
    
    // Check initialization status
    public static synchronized boolean isInitialized() {
        return initialized;
    }

    // Creates new referral with text file generation
    public void createReferral(Referral r) {
        referralRepository.addAndAppend(r);
        writeReferralText(r);
    }

    // Returns all referral records
    public List<Referral> getAllReferrals() {
        return referralRepository.getAll();
    }

    // Updates existing referral record
    public void updateReferral(Referral r) {
        referralRepository.update(r);
        // Regenerate text file representation
        writeReferralText(r);
    }

    // Deletes referral by identifier with audit trail
    public void deleteReferral(String id) {
        // Find referral before deletion
        Referral referralToDelete = null;
        for (Referral r : referralRepository.getAll()) {
            if (r.getId().equals(id)) {
                referralToDelete = r;
                break;
            }
        }
        
        if (referralToDelete != null) {
            referralRepository.removeById(id);
            // Record deletion in text file
            writeDeletionNote(referralToDelete);
        }
    }

    // Records referral deletion in text file
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

    // Generates formatted text file representation of referral
    private void writeReferralText(Referral r) {

        // Retrieve related entity data for comprehensive report
        Patient patient = patientRepository.findById(r.getPatientId());
        Clinician referringClinician = clinicianRepository.findById(r.getReferringClinicianId());
        Clinician referredToClinician = clinicianRepository.findById(r.getReferredToClinicianId());
        Facility referringFacility = facilityRepository.findById(r.getReferringFacilityId());
        Facility referredToFacility = facilityRepository.findById(r.getReferredToFacilityId());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(referralTextPath, true))) {

            // Report header section
            bw.write("==============================================");
            bw.newLine();
            bw.write("            REFERRAL SUMMARY REPORT           ");
            bw.newLine();
            bw.write("==============================================");
            bw.newLine();

            bw.write("Referral ID: " + r.getId());
            bw.newLine();

            // Patient information
            if (patient != null) {
                bw.write("Patient: " + patient.getName() + " (NHS: " + patient.getNhsNumber() + ")");
                bw.newLine();
            }

            // Referring clinician details
            if (referringClinician != null) {
                bw.write("Referring Clinician: " 
                    + referringClinician.getFullName()
                    + " (" + referringClinician.getTitle()
                    + " - " + referringClinician.getSpeciality() + ")");
                bw.newLine();
            }

            // Destination clinician details
            if (referredToClinician != null) {
                bw.write("Referred To: " 
                    + referredToClinician.getFullName()
                    + " (" + referredToClinician.getTitle()
                    + " - " + referredToClinician.getSpeciality() + ")");
                bw.newLine();
            }

            // Facility information
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

            // Referral details and classification
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

            // Clinical documentation
            bw.write("Clinical Summary:");
            bw.newLine();
            bw.write(r.getClinicalSummary());
            bw.newLine();

            // Administrative notes
            bw.write("Notes:");
            bw.newLine();
            bw.write(r.getNotes());
            bw.newLine();

            // System timestamps
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