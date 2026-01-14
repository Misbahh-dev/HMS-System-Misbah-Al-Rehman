package controller;

import model.*;
import view.ReferralView;

import java.util.ArrayList;
import java.util.List;

public class ReferralController {

    private final ReferralManager referralManager;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentRepository appointmentRepo;
    private final ReferralView view;
    
    // NEW FIELD: Current clinician ID for filtering
    private String currentClinicianId;

    public ReferralController(ReferralManager rm,
                              PatientRepository pr,
                              ClinicianRepository cr,
                              FacilityRepository fr,
                              AppointmentRepository ar,
                              ReferralView view) {

        this.referralManager = rm;
        this.patientRepo = pr;
        this.clinicianRepo = cr;
        this.facilityRepo = fr;
        this.appointmentRepo = ar;
        this.view = view;
        this.currentClinicianId = null; // Initialize as null

        // hook controller into view
        this.view.setController(this);

        refreshReferrals();
    }

    // ---------------------------------------------
    // VIEW HOOKS
    // ---------------------------------------------
    public ReferralView getView() {
        return view;
    }

    // MODIFIED: Added overloaded refresh method with clinician filter
    public void refreshReferrals(String clinicianId) {
        if (clinicianId != null && !clinicianId.isEmpty()) {
            // Show filtered referrals for this clinician
            view.showReferrals(getReferralsForClinician(clinicianId));
        } else {
            // Staff/Admin sees all referrals
            view.showReferrals(referralManager.getAllReferrals());
        }
    }

    // NEW: Overload for backward compatibility
    public void refreshReferrals() {
        refreshReferrals(null);
    }

    // ---------------------------------------------
    // NEW: Filter referrals for a specific clinician
    // ---------------------------------------------
    public List<Referral> getReferralsForClinician(String clinicianId) {
        List<Referral> filtered = new ArrayList<>();
        for (Referral r : referralManager.getAllReferrals()) {
            // Check if clinician appears in either "from clin" OR "to clin"
            if (clinicianId.equals(r.getReferringClinicianId()) || 
                clinicianId.equals(r.getReferredToClinicianId())) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    // ---------------------------------------------
    // NEW: Getter and Setter for current clinician ID
    // ---------------------------------------------
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        refreshReferrals(clinicianId);  // Refresh with filter
    }
    
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // ============================================================
    // NEW: Set current staff ID for filtering (for staff)
    // ============================================================
    public void setCurrentStaffId(String staffId) {
        this.currentClinicianId = null; // Clear clinician ID
        view.setReadOnlyMode(true);
        // STAFF VIEW: Can view all referrals
        refreshReferrals(null); // Show all referrals (no filtering)
    }
    
    // ============================================================
    // NEW: Method for staff to view all referrals (admin view)
    // ============================================================
    public void setStaffView() {
        this.currentClinicianId = null;
         view.setReadOnlyMode(false);
        refreshReferrals(null); // Show all referrals
    }
    
    // NEW: Clear current user (for logout)
    public void clearCurrentUser() {
        this.currentClinicianId = null;
        refreshReferrals(null);  // Show all referrals when logged out
    }
    
    // ============================================================
    // NEW: Check if current user is staff/admin
    // ============================================================
    public boolean isStaffView() {
        return currentClinicianId == null;
    }

    // ---------------------------------------------
    // COMBOBOX DATA
    // ---------------------------------------------
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        for (Patient p : patientRepo.getAll()) {
            ids.add(p.getId());
        }
        return ids;
    }

    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicianRepo.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }

    public List<String> getFacilityIds() {
        List<String> ids = new ArrayList<>();
        for (Facility f : facilityRepo.getAll()) {
            ids.add(f.getId());
        }
        return ids;
    }

    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        for (Appointment a : appointmentRepo.getAll()) {
            ids.add(a.getId());
        }
        return ids;
    }

    // ---------------------------------------------
    // AUTO ID GENERATOR
    // ---------------------------------------------
    public String getNextReferralId() {

        int max = 0;

        for (Referral r : referralManager.getAllReferrals()) {
            String id = r.getId();   // Example: "R012"
            if (id != null && id.startsWith("R")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }

        int next = max + 1;
        return String.format("R%03d", next);
    }

    // ---------------------------------------------
    // ADD REFERRAL
    // ---------------------------------------------
    public void addReferral(Referral r) {
        referralManager.createReferral(r);   // Saves CSV + writes text file
        refreshReferrals();
    }
}