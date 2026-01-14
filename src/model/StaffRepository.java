package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    private final List<Staff> staff = new ArrayList<>();
    private final String csvPath;

    public StaffRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            // Skip header row
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            if (rows.isEmpty()) return;
            
            // Skip header
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

                Staff s = new Staff(id, firstName, lastName, phone, email,
                        position, department, facilityId, employmentStatus,
                        startDate, lineManager, accessLevel);
                staff.add(s);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load staff: " + ex.getMessage());
        }
    }

    public List<Staff> getAll() { 
        return new ArrayList<>(staff); 
    }
    
    public Staff findById(String id) {
        for (Staff s : staff) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
    
    // ============================================================
    // AUTO-ID GENERATOR  (ST001 → ST002 → ST003 → …)
    // ============================================================
    public String generateNewId() {
        int max = 0;
        for (Staff s : staff) {
            try {
                int num = Integer.parseInt(s.getId().substring(2));
                if (num > max) max = num;
            } catch (Exception ignore) {}
        }
        return String.format("ST%03d", max + 1);
    }
    
    // ============================================================
    // ADD STAFF + APPEND TO CSV
    // ============================================================
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
    
    // ============================================================
    // UPDATE STAFF + UPDATE CSV
    // ============================================================
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
    
    // ============================================================
    // REMOVE STAFF
    // ============================================================
    public void remove(Staff s) {
        staff.remove(s);
        updateCsvFile();
    }
    
    // ============================================================
    // PRIVATE HELPER: UPDATE CSV FILE
    // ============================================================
    private void updateCsvFile() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add header row
            allData.add(new String[]{
                "staff_id", "first_name", "last_name", "role", "department",
                "facility_id", "phone_number", "email", "employment_status",
                "start_date", "line_manager", "access_level"
            });
            
            // Add all staff
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
            
            // Write to CSV
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to update CSV file: " + ex.getMessage());
        }
    }
}