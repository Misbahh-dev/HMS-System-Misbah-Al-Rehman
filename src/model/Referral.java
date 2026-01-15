package model;

public class Referral {

    // Core referral identification and attribution
    private String id;                    // Unique referral identifier
    private String patientId;             // Referred patient identifier
    private String referringClinicianId;  // Originating clinician identifier
    private String referredToClinicianId; // Destination clinician identifier
    private String referringFacilityId;   // Originating facility identifier
    private String referredToFacilityId;  // Destination facility identifier
    private String referralDate;          // Referral creation date
    
    // Clinical information and classification
    private String urgencyLevel;          // Clinical urgency classification
    private String referralReason;        // Primary reason for referral
    private String clinicalSummary;       // Comprehensive clinical summary
    private String requestedService;      // Requested investigation/service
    
    // Referral tracking and administration
    private String status;                // Current referral status
    private String appointmentId;         // Associated appointment identifier
    private String notes;                 // Additional administrative notes
    private String createdDate;           // System creation timestamp
    private String lastUpdated;           // Most recent update timestamp

    // Default constructor for object instantiation
    public Referral() {}

    // Primary constructor with all referral attributes
    public Referral(String id, String patientId, String referringClinicianId,
                    String referredToClinicianId, String referringFacilityId,
                    String referredToFacilityId, String referralDate,
                    String urgencyLevel, String referralReason,
                    String clinicalSummary, String requestedService,
                    String status, String appointmentId, String notes,
                    String createdDate, String lastUpdated) {

        this.id = id;
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedService = requestedService;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters for referral data
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
//Made By Misbah Al Rehman. SRN: 24173647
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReferringClinicianId() { return referringClinicianId; }
    public void setReferringClinicianId(String referringClinicianId) { this.referringClinicianId = referringClinicianId; }

    public String getReferredToClinicianId() { return referredToClinicianId; }
    public void setReferredToClinicianId(String referredToClinicianId) { this.referredToClinicianId = referredToClinicianId; }

    public String getReferringFacilityId() { return referringFacilityId; }
    public void setReferringFacilityId(String referringFacilityId) { this.referringFacilityId = referringFacilityId; }

    public String getReferredToFacilityId() { return referredToFacilityId; }
    public void setReferredToFacilityId(String referredToFacilityId) { this.referredToFacilityId = referredToFacilityId; }

    public String getReferralDate() { return referralDate; }
    public void setReferralDate(String referralDate) { this.referralDate = referralDate; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getReferralReason() { return referralReason; }
    public void setReferralReason(String referralReason) { this.referralReason = referralReason; }

    public String getClinicalSummary() { return clinicalSummary; }
    public void setClinicalSummary(String clinicalSummary) { this.clinicalSummary = clinicalSummary; }

    public String getRequestedService() { return requestedService; }
    public void setRequestedService(String requestedService) { this.requestedService = requestedService; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}