package model;

public class Staff extends Person {

    // Staff-specific attributes beyond base Person class
    private String position;       // Job title/role
    private String facilityId;     // Assigned facility identifier
    private String department;     // Organizational department
    private String employmentStatus; // Current employment status
    private String startDate;      // Employment commencement date
    private String lineManager;    // Reporting manager identifier
    private String accessLevel;    // System access permissions

    // Default constructor for object instantiation
    public Staff() { }

    // Basic constructor for essential staff attributes
    public Staff(String id, String name, String phone, String email,
                 String position, String facilityId) {
        super(id, name, phone, email);
        this.position = position;
        this.facilityId = facilityId;
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Comprehensive constructor with all staff attributes
    public Staff(String id, String firstName, String lastName, String phone, String email,
                 String position, String department, String facilityId, 
                 String employmentStatus, String startDate, String lineManager, String accessLevel) {
        super(id, firstName + " " + lastName, phone, email);
        this.position = position;
        this.department = department;
        this.facilityId = facilityId;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }

    // Getter and Setter methods for staff-specific data
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getLineManager() { return lineManager; }
    public void setLineManager(String lineManager) { this.lineManager = lineManager; }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    
    // Helper methods for name manipulation
    public String getFirstName() {
        String[] nameParts = getName().split(" ");
        return nameParts.length > 0 ? nameParts[0] : "";
    }
    
    public String getLastName() {
        String[] nameParts = getName().split(" ");
        return nameParts.length > 1 ? nameParts[1] : "";
    }
}