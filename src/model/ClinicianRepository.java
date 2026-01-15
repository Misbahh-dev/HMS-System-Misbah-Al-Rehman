package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianRepository {

    // In-memory storage for clinician records
    private final List<Clinician> clinicians = new ArrayList<>();
    // File system path for CSV persistence
    private final String csvPath;

    // Constructor - loads data from CSV file on initialization
    public ClinicianRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    // Returns all clinician identifiers for reference purposes
    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicians) ids.add(c.getId());
        return ids;
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Loads clinician data from CSV file into memory
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                Clinician c = new Clinician(
                        row[0],   // id - unique clinician identifier
                        row[1],   // title - professional designation
                        row[2],   // first - given name
                        row[3],   // last - family name
                        row[4],   // speciality - medical specialty
                        row[5],   // gmc - registration number
                        row[6],   // phone - contact number
                        row[7],   // email - professional email
                        row[8],   // workplace id - facility identifier
                        row[9],   // workplace type - facility category
                        row[10],  // employment - current status
                        row[11]   // start date - employment commencement
                );
                clinicians.add(c);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians: " + ex.getMessage());
        }
    }

    // Generates next sequential clinician identifier
    public String generateNewId() {
        int max = 0;
        for (Clinician c : clinicians) {
            try {
                // Extract numeric portion from ID (e.g., "C001" → 1)
                int n = Integer.parseInt(c.getId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }

    // Adds clinician to memory and appends to CSV file
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
    
    // Updates existing clinician in memory and persists to CSV
    public void update(Clinician updatedClinician) {
        for (int i = 0; i < clinicians.size(); i++) {
            Clinician clinician = clinicians.get(i);
            if (clinician.getId().equals(updatedClinician.getId())) {
                // Replace clinician record in memory
                clinicians.set(i, updatedClinician);
                // Persist all changes to CSV file
                saveAllToCsv();
                return;
            }
        }
        System.err.println("Clinician not found for update: " + updatedClinician.getId());
    }

    // Returns all clinician records in the repository
    public List<Clinician> getAll() {
        return clinicians;
    }

    // Removes clinician from memory and updates CSV file
    public void remove(Clinician c) {
        clinicians.remove(c);
        saveAllToCsv();
    }
    
    // Removes clinician by identifier lookup
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

    // Retrieves clinician by unique identifier
    public Clinician findById(String id) {
        for (Clinician c : clinicians)
            if (c.getId().equals(id)) return c;
        return null;
    }
    
    // Writes all clinician records to CSV file (full persistence)
    private void saveAllToCsv() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add CSV header row with column definitions
            allData.add(new String[]{
                "clinician_id", "title", "first_name", "last_name", 
                "speciality", "gmc_number", "phone_number", "email", 
                "workplace_id", "workplace_type", "employment_status", "start_date"
            });
            
            // Convert all clinicians to CSV row format
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
            
            // Write complete dataset to CSV file
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to save clinicians to CSV: " + ex.getMessage());
        }
    }
}