package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientRepository {

    // In-memory storage for patient records
    private final List<Patient> patients = new ArrayList<>();
    // File system path for CSV persistence
    private final String csvPath;

    // Constructor - loads data from CSV file on initialization
    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    // Returns all patient identifiers for reference purposes
    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        for (Patient p : patients) ids.add(p.getId());
        return ids;
    }

    // Loads patient data from CSV file into memory
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                Patient p = new Patient(
                        row[0],   // patient_id - unique identifier
                        row[1],   // first_name - given name
                        row[2],   // last_name - family name
                        row[3],   // date_of_birth - birth date
                        row[4],   // nhs_number - national health identifier
                        row[5],   // gender - gender identity
                        row[6],   // phone_number - contact telephone
                        row[7],   // email - contact email
                        row[8],   // address - residential address
                        row[9],   // postcode - postal code
                        row[10],  // emergency_contact_name - emergency person
                        row[11],  // emergency_contact_phone - emergency contact
                        row[12],  // registration_date - system enrollment
                        row[13]   // gp_surgery_id - primary care provider
                );

                patients.add(p);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load patients: " + ex.getMessage());
        }
    }
        //Made By Misbah Al Rehman. SRN: 24173647
    // Generates next sequential patient identifier
    public String generateNewId() {
        int max = 0;
        for (Patient p : patients) {
            try {
                // Extract numeric portion from ID (e.g., "P001" → 1)
                int num = Integer.parseInt(p.getId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignore) {}
        }
        return String.format("P%03d", max + 1);
    }

    // Adds patient to memory and appends to CSV file
    public void addAndAppend(Patient p) {
        patients.add(p);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    p.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getDateOfBirth(),
                    p.getNhsNumber(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getEmail(),
                    p.getAddress(),
                    p.getPostcode(),
                    p.getEmergencyContactName(),
                    p.getEmergencyContactPhone(),
                    p.getRegistrationDate(),
                    p.getGpSurgeryId()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append patient: " + ex.getMessage());
        }
    }

    // Updates existing patient in memory and persists to CSV
    public void update(Patient updatedPatient) {
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.getId().equals(updatedPatient.getId())) {
                // Replace patient record in memory
                patients.set(i, updatedPatient);
                
                // Persist all changes to CSV file
                try {
                    List<String[]> allData = new ArrayList<>();
                    
                    // Add CSV header row with column definitions
                    allData.add(new String[]{
                        "patient_id", "first_name", "last_name", "date_of_birth", 
                        "nhs_number", "gender", "phone_number", "email", 
                        "address", "postcode", "emergency_contact_name", 
                        "emergency_contact_phone", "registration_date", "gp_surgery_id"
                    });
                    
                    // Convert all patients to CSV row format
                    for (Patient p : patients) {
                        allData.add(new String[]{
                            p.getId(),
                            p.getFirstName(),
                            p.getLastName(),
                            p.getDateOfBirth(),
                            p.getNhsNumber(),
                            p.getGender(),
                            p.getPhoneNumber(),
                            p.getEmail(),
                            p.getAddress(),
                            p.getPostcode(),
                            p.getEmergencyContactName(),
                            p.getEmergencyContactPhone(),
                            p.getRegistrationDate(),
                            p.getGpSurgeryId()
                        });
                    }
                    
                    // Write complete dataset to CSV file
                    CsvUtils.writeCsv(csvPath, allData);
                    
                } catch (IOException ex) {
                    System.err.println("Failed to update patient in CSV: " + ex.getMessage());
                }
                return;
            }
        }
        System.err.println("Patient not found for update: " + updatedPatient.getId());
    }

    // Removes patient from memory and updates CSV file
    public void remove(Patient p) {
        patients.remove(p);
        updateCsvFile();
    }
    
    // Removes patient by identifier lookup
    public void removeById(String id) {
        Patient patientToRemove = null;
        for (Patient p : patients) {
            if (p.getId().equals(id)) {
                patientToRemove = p;
                break;
            }
        }
        
        if (patientToRemove != null) {
            remove(patientToRemove);
        }
    }

    // Returns all patient records in the repository
    public List<Patient> getAll() {
        return patients;
    }

    // Retrieves patient by unique identifier
    public Patient findById(String id) {
        for (Patient p : patients) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    // Returns patients associated with specific clinician via appointments
    public List<Patient> findByClinicianId(String clinicianId, AppointmentRepository appointmentRepo) {
        List<Patient> clinicianPatients = new ArrayList<>();
        
        // Retrieve all appointments for specified clinician
        List<Appointment> clinicianAppointments = appointmentRepo.findByClinicianId(clinicianId);
        
        // Extract unique patient identifiers from appointments
        Set<String> patientIds = new HashSet<>();
        for (Appointment a : clinicianAppointments) {
            patientIds.add(a.getPatientId());
        }
        
        // Find corresponding patient records
        for (String patientId : patientIds) {
            Patient patient = findById(patientId);
            if (patient != null) {
                clinicianPatients.add(patient);
            }
        }
        
        return clinicianPatients;
    }
    
    // Writes all patient records to CSV file (full persistence)
    private void updateCsvFile() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add CSV header row with column definitions
            allData.add(new String[]{
                "patient_id", "first_name", "last_name", "date_of_birth", 
                "nhs_number", "gender", "phone_number", "email", 
                "address", "postcode", "emergency_contact_name", 
                "emergency_contact_phone", "registration_date", "gp_surgery_id"
            });
            
            // Convert all patients to CSV row format
            for (Patient p : patients) {
                allData.add(new String[]{
                    p.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getDateOfBirth(),
                    p.getNhsNumber(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getEmail(),
                    p.getAddress(),
                    p.getPostcode(),
                    p.getEmergencyContactName(),
                    p.getEmergencyContactPhone(),
                    p.getRegistrationDate(),
                    p.getGpSurgeryId()
                });
            }
            
            // Write complete dataset to CSV file
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to update CSV file: " + ex.getMessage());
        }
    }
}