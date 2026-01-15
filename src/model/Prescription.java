package model;

public class Prescription {

    // Core prescription identification and attribution
    private String id;               // Unique prescription identifier (RX001)
    private String patientId;        // Prescribed patient identifier (P001)
    private String clinicianId;      // Prescribing clinician identifier (C001)
    private String appointmentId;    // Associated appointment identifier (A001)
    private String prescriptionDate; // Prescription creation date (yyyy-MM-dd)

    // Medication details and dosage information
    private String medication;       // Medication brand/generic name
    private String dosage;           // Strength and unit (e.g. 20mg)
    private String frequency;        // Administration schedule (e.g. Once daily)
    private String durationDays;     // Treatment duration in days
    private String quantity;         // Total quantity dispensed

    // Prescription administration and tracking
    private String instructions;     // Patient usage instructions
    private String pharmacyName;     // Dispensing pharmacy name
    private String status;           // Prescription lifecycle status
    private String issueDate;        // Original issuance date
    private String collectionDate;   // Patient collection date

    // Default constructor for object instantiation
    public Prescription() { }
//Made By Misbah Al Rehman. SRN: 24173647
    // Primary constructor with all prescription attributes
    public Prescription(String id,
                        String patientId,
                        String clinicianId,
                        String appointmentId,
                        String prescriptionDate,
                        String medication,
                        String dosage,
                        String frequency,
                        String durationDays,
                        String quantity,
                        String instructions,
                        String pharmacyName,
                        String status,
                        String issueDate,
                        String collectionDate) {

        this.id = id;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.medication = medication;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.quantity = quantity;
        this.instructions = instructions;
        this.pharmacyName = pharmacyName;
        this.status = status;
        this.issueDate = issueDate;
        this.collectionDate = collectionDate;
    }

    // Getters for retrieving prescription data
    public String getId()               { return id; }
    public String getPatientId()        { return patientId; }
    public String getClinicianId()      { return clinicianId; }
    public String getAppointmentId()    { return appointmentId; }
    public String getPrescriptionDate() { return prescriptionDate; }
    public String getMedication()       { return medication; }
    public String getDosage()           { return dosage; }
    public String getFrequency()        { return frequency; }
    public String getDurationDays()     { return durationDays; }
    public String getQuantity()         { return quantity; }
    public String getInstructions()     { return instructions; }
    public String getPharmacyName()     { return pharmacyName; }
    public String getStatus()           { return status; }
    public String getIssueDate()        { return issueDate; }
    public String getCollectionDate()   { return collectionDate; }

    // Setters for updating prescription data
    public void setId(String id)                             { this.id = id; }
    public void setPatientId(String patientId)               { this.patientId = patientId; }
    public void setClinicianId(String clinicianId)           { this.clinicianId = clinicianId; }
    public void setAppointmentId(String appointmentId)       { this.appointmentId = appointmentId; }
    public void setPrescriptionDate(String prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public void setMedication(String medication)             { this.medication = medication; }
    public void setDosage(String dosage)                     { this.dosage = dosage; }
    public void setFrequency(String frequency)               { this.frequency = frequency; }
    public void setDurationDays(String durationDays)         { this.durationDays = durationDays; }
    public void setQuantity(String quantity)                 { this.quantity = quantity; }
    public void setInstructions(String instructions)         { this.instructions = instructions; }
    public void setPharmacyName(String pharmacyName)         { this.pharmacyName = pharmacyName; }
    public void setStatus(String status)                     { this.status = status; }
    public void setIssueDate(String issueDate)               { this.issueDate = issueDate; }
    public void setCollectionDate(String collectionDate)     { this.collectionDate = collectionDate; }
}