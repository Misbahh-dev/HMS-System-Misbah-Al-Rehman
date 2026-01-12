package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianRepository {

    private final List<Clinician> clinicians = new ArrayList<>();
    private final String csvPath;

    public ClinicianRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicians) ids.add(c.getId());
        return ids;
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                Clinician c = new Clinician(
                        row[0],   // id
                        row[1],   // title
                        row[2],   // first
                        row[3],   // last
                        row[4],   // speciality
                        row[5],   // gmc
                        row[6],   // phone
                        row[7],   // email
                        row[8],   // workplace id
                        row[9],   // workplace type
                        row[10],  // employment
                        row[11]   // start date
                );
                clinicians.add(c);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians: " + ex.getMessage());
        }
    }

    // ============================================================
    // AUTO-ID: C001 → C002 → C003...
    // ============================================================
    public String generateNewId() {
        int max = 0;
        for (Clinician c : clinicians) {
            try {
                int n = Integer.parseInt(c.getId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }

    // ============================================================
    // ADD + APPEND TO CSV
    // ============================================================
    public void addAndAppend(Clinician c) {
        clinicians.add(c);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    c.getId(), c.getTitle(), c.getFirstName(), c.getLastName(),
                    c.getSpeciality(), c.getGmcNumber(), c.getPhone(), c.getEmail(),
                    c.getWorkplaceId(), c.getWorkplaceType(), c.getEmploymentStatus(),
                    c.getStartDate()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append clinician: " + ex.getMessage());
        }
    }
    
    // ============================================================
    // NEW METHOD: UPDATE CLINICIAN + UPDATE CSV
    // ============================================================
    public void update(Clinician updatedClinician) {
        // Find the clinician by ID and update their information
        for (int i = 0; i < clinicians.size(); i++) {
            Clinician clinician = clinicians.get(i);
            if (clinician.getId().equals(updatedClinician.getId())) {
                // Update the clinician in the list
                clinicians.set(i, updatedClinician);
                
                // Update the CSV file - rewrite entire file
                saveAllToCsv();
                return;
            }
        }
        
        System.err.println("Clinician not found for update: " + updatedClinician.getId());
    }

    public List<Clinician> getAll() {
        return clinicians;
    }

    // ============================================================
    // REMOVE
    // ============================================================
    public void remove(Clinician c) {
        clinicians.remove(c);
        // Update CSV after removal
        saveAllToCsv();
    }
    
    // ============================================================
    // NEW METHOD: REMOVE BY ID
    // ============================================================
    public void removeById(String id) {
        Clinician clinicianToRemove = null;
        for (Clinician c : clinicians) {
            if (c.getId().equals(id)) {
                clinicianToRemove = c;
                break;
            }
        }
        
        if (clinicianToRemove != null) {
            remove(clinicianToRemove);
        }
    }

    public Clinician findById(String id) {
        for (Clinician c : clinicians)
            if (c.getId().equals(id)) return c;
        return null;
    }
    
    // ============================================================
    // NEW METHOD: SAVE ALL CLINICIANS TO CSV
    // ============================================================
    private void saveAllToCsv() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add header row first (based on your CSV structure)
            allData.add(new String[]{
                "clinician_id", "title", "first_name", "last_name", 
                "speciality", "gmc_number", "phone_number", "email", 
                "workplace_id", "workplace_type", "employment_status", "start_date"
            });
            
            // Add all clinicians
            for (Clinician c : clinicians) {
                allData.add(new String[]{
                    c.getId(),
                    c.getTitle(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getSpeciality(),
                    c.getGmcNumber(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getWorkplaceId(),
                    c.getWorkplaceType(),
                    c.getEmploymentStatus(),
                    c.getStartDate()
                });
            }
            
            // Write to CSV using the writeCsv method
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to save clinicians to CSV: " + ex.getMessage());
        }
    }
}