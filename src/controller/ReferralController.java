package controller;

import model.*;
import view.ReferralView;

import java.util.ArrayList;
import java.util.List;

public class ReferralController {

    // Core dependencies for managing referral data
    private final ReferralManager referralManager;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentRepository appointmentRepo;
    private final ReferralView view;
    
    // Current user context for filtering referrals
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
        this.currentClinicianId = null;

        this.view.setController(this);
        refreshReferrals();
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Returns the view component for UI display
    public ReferralView getView() {
        return view;
    }

    // Refreshes referrals filtered by specific clinician
    public void refreshReferrals(String clinicianId) {
        if (clinicianId != null && !clinicianId.isEmpty()) {
            view.showReferrals(getReferralsForClinician(clinicianId));
        } else {
            view.showReferrals(referralManager.getAllReferrals());
        }
    }

    // Refreshes all referrals (no filtering)
    public void refreshReferrals() {
        refreshReferrals(null);
    }

    // Returns referrals involving specific clinician
    public List<Referral> getReferralsForClinician(String clinicianId) {
        List<Referral> filtered = new ArrayList<>();
        for (Referral r : referralManager.getAllReferrals()) {
            // Include referrals where clinician is either sender or receiver
            if (clinicianId.equals(r.getReferringClinicianId()) || 
                clinicianId.equals(r.getReferredToClinicianId())) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    // Sets current clinician for filtered view
    public void setCurrentClinicianId(String clinicianId) {
        this.currentClinicianId = clinicianId;
        refreshReferrals(clinicianId);
    }
    
    // Returns current clinician ID for context
    public String getCurrentClinicianId() {
        return currentClinicianId;
    }
    
    // Configures view for staff users with read-only access
    public void setCurrentStaffId(String staffId) {
        this.currentClinicianId = null;
        view.setReadOnlyMode(true);
        refreshReferrals(null);
    }
    
    // Configures administrative view with full access
    public void setAdminView() {
        this.currentClinicianId = null;
        view.setReadOnlyMode(false);
        view.setTitle("Referral Management (Admin Mode)");
        view.showUpdateDeleteButtons();
        refreshReferrals(null);
    }
    
    // Checks if current view is staff/administrator
    public boolean isStaffView() {
        return currentClinicianId == null;
    }

    // Returns all patient IDs for dropdown population
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        for (Patient p : patientRepo.getAll()) {
            ids.add(p.getId());
        }
        return ids;
    }

    // Returns all clinician IDs for dropdown population
    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicianRepo.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }

    // Returns all facility IDs for dropdown population
    public List<String> getFacilityIds() {
        List<String> ids = new ArrayList<>();
        for (Facility f : facilityRepo.getAll()) {
            ids.add(f.getId());
        }
        return ids;
    }

    // Returns all appointment IDs for dropdown population
    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        for (Appointment a : appointmentRepo.getAll()) {
            ids.add(a.getId());
        }
        return ids;
    }

    // Generates next sequential referral ID
    public String getNextReferralId() {
        int max = 0;
        for (Referral r : referralManager.getAllReferrals()) {
            String id = r.getId();
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

    // Creates new referral and updates display
    public void addReferral(Referral r) {
        referralManager.createReferral(r);
        refreshReferrals();
    }
    
    // Updates existing referral information
    public void updateReferral(Referral r) {
        referralManager.updateReferral(r);
        refreshReferrals();
    }
    
    // Deletes referral by identifier
    public void deleteReferral(String id) {
        referralManager.deleteReferral(id);
        refreshReferrals();
    }
}