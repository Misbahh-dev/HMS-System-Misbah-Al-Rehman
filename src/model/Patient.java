package model;

public class Patient {

    // Core patient identification and personal information
    private String id;                      // Unique patient identifier
    private String firstName;               // Patient's given name
    private String lastName;                // Patient's family name
    private String dateOfBirth;             // Patient's birth date
    private String nhsNumber;               // National Health Service number
    private String gender;                  // Patient's gender identity
    private String phoneNumber;             // Primary contact telephone
    private String email;                   // Primary contact email
    private String address;                 // Residential street address
    private String postcode;                // Residential postal code
    private String emergencyContactName;    // Emergency contact person
    private String emergencyContactPhone;   // Emergency contact telephone
    private String registrationDate;        // System registration date
    private String gpSurgeryId;             // Registered general practitioner

    // Default constructor 
    public Patient() {}
// Primary constructor with all patient attributes
    public Patient(String id, String firstName, String lastName,
                   String dateOfBirth, String nhsNumber, String gender,
                   String phoneNumber, String email, String address, String postcode,
                   String emergencyContactName, String emergencyContactPhone,
                   String registrationDate, String gpSurgeryId) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhsNumber = nhsNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.registrationDate = registrationDate;
        this.gpSurgeryId = gpSurgeryId;
    }

    // Backward compatibility for older code
    public String getName() {
        return getFullName();
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // getters for retrieving patient data
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public String getNhsNumber() { return nhsNumber; }
    public String getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPostcode() { return postcode; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getRegistrationDate() { return registrationDate; }
    public String getGpSurgeryId() { return gpSurgeryId; }

    // setters for updating patient data or setters
    public void setId(String id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setNhsNumber(String nhsNumber) { this.nhsNumber = nhsNumber; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
    public void setGpSurgeryId(String gpSurgeryId) { this.gpSurgeryId = gpSurgeryId; }
}
