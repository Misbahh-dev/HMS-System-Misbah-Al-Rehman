package model;

public class Appointment {

    // Core appointment identification and tracking
    private String id;                  // Unique appointment identifier
    private String patientId;           // Associated patient identifier
    private String clinicianId;         // Assigned clinician identifier
    private String facilityId;          // Scheduled facility location
    private String appointmentDate;     // Scheduled appointment date
    private String appointmentTime;     // Scheduled appointment time
    private String durationMinutes;     // Expected duration in minutes
    private String appointmentType;     // Type of appointment/service
    private String status;              // Current appointment status
    private String reasonForVisit;      // Primary reason for appointment
    private String notes;               // Additional notes/comments
    private String createdDate;         // Original creation timestamp
    private String lastModified;        // Last update timestamp

    // Default constructor for object creation
    public Appointment() { }

    // Primary constructor with all appointment attributes
    public Appointment(String id,
                       String patientId,
                       String clinicianId,
                       String facilityId,
                       String appointmentDate,
                       String appointmentTime,
                       String durationMinutes,
                       String appointmentType,
                       String status,
                       String reasonForVisit,
                       String notes,
                       String createdDate,
                       String lastModified) {

        this.id = id;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.appointmentType = appointmentType;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Accessor methods for retrieving appointment data
    public String getId()                { return id; }
    public String getPatientId()         { return patientId; }
    public String getClinicianId()       { return clinicianId; }
    public String getFacilityId()        { return facilityId; }
    public String getAppointmentDate()   { return appointmentDate; }
    public String getAppointmentTime()   { return appointmentTime; }
    public String getDurationMinutes()   { return durationMinutes; }
    public String getAppointmentType()   { return appointmentType; }
    public String getStatus()            { return status; }
    public String getReasonForVisit()    { return reasonForVisit; }
    public String getNotes()             { return notes; }
    public String getCreatedDate()       { return createdDate; }
    public String getLastModified()      { return lastModified; }

    // Mutator methods for updating appointment data
    public void setId(String id)                          { this.id = id; }
    public void setPatientId(String patientId)            { this.patientId = patientId; }
    public void setClinicianId(String clinicianId)        { this.clinicianId = clinicianId; }
    public void setFacilityId(String facilityId)          { this.facilityId = facilityId; }
    public void setAppointmentDate(String appointmentDate){ this.appointmentDate = appointmentDate; }
    public void setAppointmentTime(String appointmentTime){ this.appointmentTime = appointmentTime; }
    public void setDurationMinutes(String durationMinutes){ this.durationMinutes = durationMinutes; }
    public void setAppointmentType(String appointmentType){ this.appointmentType = appointmentType; }
    public void setStatus(String status)                  { this.status = status; }
    public void setReasonForVisit(String reasonForVisit)  { this.reasonForVisit = reasonForVisit; }
    public void setNotes(String notes)                    { this.notes = notes; }
    public void setCreatedDate(String createdDate)        { this.createdDate = createdDate; }
    public void setLastModified(String lastModified)      { this.lastModified = lastModified; }
}