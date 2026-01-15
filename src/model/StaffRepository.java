package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    // In-memory storage for staff records
    private final List<Staff> staff = new ArrayList<>();
    // File system path for CSV persistence
    private final String csvPath;

    // Constructor - loads data from CSV file on initialization
    public StaffRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Loads staff data from CSV file into memory
    private void load() {
        try {
            // Read CSV data with header row skipping
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            if (rows.isEmpty()) return;
            
            // Skip header row (index 0) and process data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String id = row[0];
                String firstName = row[1];
                String lastName = row[2];
                String position = row[3];
                String department = row[4];
                String facilityId = row[5];
                String phone = row[6];
                String email = row[7];
                String employmentStatus = row[8];
                String startDate = row[9];
                String lineManager = row[10];
                String accessLevel = row[11];

                // Create Staff object with all attributes
                Staff s = new Staff(id, firstName, lastName, phone, email,
                        position, department, facilityId, employmentStatus,
                        startDate, lineManager, accessLevel);
                staff.add(s);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load staff: " + ex.getMessage());
        }
    }

    // Returns all staff records in the repository
    public List<Staff> getAll() { 
        return new ArrayList<>(staff); 
    }
    
    // Retrieves staff by unique identifier
    public Staff findById(String id) {
        for (Staff s : staff) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
    
    // Generates next sequential staff identifier
    public String generateNewId() {
        int max = 0;
        for (Staff s : staff) {
            try {
                // Extract numeric portion from ID (e.g., "ST001" → 1)
                int num = Integer.parseInt(s.getId().substring(2));
                if (num > max) max = num;
            } catch (Exception ignore) {}
        }
        return String.format("ST%03d", max + 1);
    }
    
    // Adds staff to memory and appends to CSV file
    public void addAndAppend(Staff s) {
        staff.add(s);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    s.getId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getPosition(),
                    s.getDepartment(),
                    s.getFacilityId(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getEmploymentStatus(),
                    s.getStartDate(),
                    s.getLineManager(),
                    s.getAccessLevel()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append staff: " + ex.getMessage());
        }
    }
    
    // Updates existing staff in memory and persists to CSV
    public void update(Staff updatedStaff) {
        for (int i = 0; i < staff.size(); i++) {
            Staff s = staff.get(i);
            if (s.getId().equals(updatedStaff.getId())) {
                staff.set(i, updatedStaff);
                updateCsvFile();
                return;
            }
        }
        System.err.println("Staff not found for update: " + updatedStaff.getId());
    }
    
    // Removes staff from memory and updates CSV file
    public void remove(Staff s) {
        staff.remove(s);
        updateCsvFile();
    }
    
    // Writes all staff records to CSV file (full persistence)
    private void updateCsvFile() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add CSV header row with column definitions
            allData.add(new String[]{
                "staff_id", "first_name", "last_name", "role", "department",
                "facility_id", "phone_number", "email", "employment_status",
                "start_date", "line_manager", "access_level"
            });
            
            // Convert all staff to CSV row format
            for (Staff s : staff) {
                allData.add(new String[]{
                    s.getId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getPosition(),
                    s.getDepartment(),
                    s.getFacilityId(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getEmploymentStatus(),
                    s.getStartDate(),
                    s.getLineManager(),
                    s.getAccessLevel()
                });
            }
            
            // Write complete dataset to CSV file
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to update CSV file: " + ex.getMessage());
        }
    }
}