package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReferralRepository {

    // In-memory storage for referral records
    private final List<Referral> referrals = new ArrayList<>();
    // File system path for CSV persistence
    private final String csvPath;

    // Constructor - loads data from CSV file on initialization
    public ReferralRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    // Loads referral data from CSV file into memory
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
//Made By Misbah Al Rehman. SRN: 24173647
                // Create Referral object with all 16 CSV columns
                Referral r = new Referral(
                        row[0],  // referral_id - unique identifier
                        row[1],  // patient_id - referred patient
                        row[2],  // referring_clinician - originating clinician
                        row[3],  // referred_to_clinician - destination clinician
                        row[4],  // referring_facility - originating facility
                        row[5],  // referred_to_facility - destination facility
                        row[6],  // referral_date - creation date
                        row[7],  // urgency_level - priority classification
                        row[8],  // referral_reason - primary rationale
                        row[9],  // clinical_summary - comprehensive details
                        row[10], // requested_service - required investigation
                        row[11], // status - current lifecycle state
                        row[12], // appointment_id - related appointment
                        row[13], // notes - additional information
                        row[14], // created_date - system timestamp
                        row[15]  // last_updated - modification timestamp
                );

                referrals.add(r);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load referrals: " + ex.getMessage());
        }
    }


    // Returns all referral records in the repository
    public List<Referral> getAll() {
        return referrals;
    }


    /**
     * Adds referral to memory and appends to CSV (all 16 columns)
     */
    public void addAndAppend(Referral r) {
        referrals.add(r);

        try {
            CsvUtils.appendLine(csvPath, new String[] {
                    r.getId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedService(),
                    r.getStatus(),
                    r.getAppointmentId(),
                    r.getNotes(),
                    r.getCreatedDate(),
                    r.getLastUpdated()
            });

        } catch (IOException ex) {
            System.err.println("Failed to append referral: " + ex.getMessage());
        }
    }

    // Updates existing referral in memory and persists to CSV
    public void update(Referral updatedReferral) {
        for (int i = 0; i < referrals.size(); i++) {
            Referral r = referrals.get(i);
            if (r.getId().equals(updatedReferral.getId())) {
                referrals.set(i, updatedReferral);
                saveAllToCsv();
                return;
            }
        }
        System.err.println("Referral not found for update: " + updatedReferral.getId());
    }

    // Deletes referral by identifier from memory and CSV
    public void removeById(String id) {
        Referral referralToRemove = null;
        for (Referral r : referrals) {
            if (r.getId().equals(id)) {
                referralToRemove = r;
                break;
            }
        }
        
        if (referralToRemove != null) {
            referrals.remove(referralToRemove);
            saveAllToCsv();
        }
    }

    // Writes all referral records to CSV file (full persistence)
    private void saveAllToCsv() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add CSV header row with column definitions
            allData.add(new String[]{
                "referral_id", "patient_id", "referring_clinician_id", "referred_to_clinician_id",
                "referring_facility_id", "referred_to_facility_id", "referral_date", "urgency_level",
                "referral_reason", "clinical_summary", "requested_investigations", "status",
                "appointment_id", "notes", "created_date", "last_updated"
            });
            
            // Convert all referrals to CSV row format
            for (Referral r : referrals) {
                allData.add(new String[]{
                    r.getId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedService(), // Note: CSV column name differs from field name
                    r.getStatus(),
                    r.getAppointmentId(),
                    r.getNotes(),
                    r.getCreatedDate(),
                    r.getLastUpdated()
                });
            }
            
            // Write complete dataset to CSV file
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to save referrals to CSV: " + ex.getMessage());
        }
    }
}