package model;

public class Clinician {

    // Core clinician identification and personal information
    private String id;               // Unique clinician identifier
    private String firstName;        // Clinician's given name
    private String lastName;         // Clinician's family name
    private String title;            // Professional title (Dr., Mr., Ms., etc.)
    private String speciality;       // Medical specialty/area of expertise
    private String gmcNumber;        // General Medical Council registration number
    private String phone;            // Contact telephone number
    private String email;            // Professional email address
    private String workplaceId;      // Associated workplace identifier
    private String workplaceType;    // Type of workplace (Hospital, Clinic, etc.)
    private String employmentStatus; // Current employment status
    private String startDate;        // Employment start date
//Made By Misbah Al Rehman. SRN: 24173647
    // Default constructor for object instantiation
    public Clinician() {}

    // Primary constructor with all clinician attributes
    public Clinician(String id, String title, String firstName, String lastName,
                     String speciality, String gmcNumber, String phone,
                     String email, String workplaceId, String workplaceType,
                     String employmentStatus, String startDate) {

        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
        this.gmcNumber = gmcNumber;
        this.phone = phone;
        this.email = email;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    // Accessor methods for retrieving clinician data
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpeciality() { return speciality; }
    public String getGmcNumber() { return gmcNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWorkplaceId() { return workplaceId; }
    public String getWorkplaceType() { return workplaceType; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }

    // Returns formatted full name with professional title
    public String getFullName() {
        return title + " " + firstName + " " + lastName;
    }
}