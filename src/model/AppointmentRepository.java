package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    // In-memory storage for appointment data
    private final List<Appointment> appointments = new ArrayList<>();
    // File system path for persistent storage
    private final String csvPath;

    // Constructor - loads data from CSV on initialization
    public AppointmentRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Loads appointment data from CSV file into memory
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                // CSV column mapping: index to appointment field
                Appointment a = new Appointment(
                        row[0],  // id - appointment identifier
                        row[1],  // patient_id - associated patient
                        row[2],  // clinician_id - assigned clinician
                        row[3],  // facility_id - location facility
                        row[4],  // appointment_date - scheduled date
                        row[5],  // appointment_time - scheduled time
                        row[6],  // duration_minutes - appointment length
                        row[7],  // appointment_type - service category
                        row[8],  // status - current appointment state
                        row[9],  // reason_for_visit - primary purpose
                        row[10], // notes - additional information
                        row[11], // created_date - initial creation date
                        row[12]  // last_modified - most recent update
                );

                appointments.add(a);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }

    // Returns all appointments in the repository
    public List<Appointment> getAll() {
        return appointments;
    }

    // Generates next sequential appointment identifier
    public String generateNewId() {
        int max = 0;
        for (Appointment a : appointments) {
            try {
                // Extract numeric portion from ID (e.g., "A001" → 1)
                int n = Integer.parseInt(a.getId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignore) {}
        }
        return String.format("A%03d", max + 1);
    }

    // Adds appointment to in-memory list only
    public void add(Appointment a) {
        appointments.add(a);
    }

    // Adds appointment and appends to CSV file
    public void addAndAppend(Appointment a) {
        appointments.add(a);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes(),
                    a.getCreatedDate(),
                    a.getLastModified()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append appointment: " + ex.getMessage());
        }
    }
    
    // Updates existing appointment in memory and CSV
    public void update(Appointment updatedAppointment) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (appointment.getId().equals(updatedAppointment.getId())) {
                // Replace appointment in memory
                appointments.set(i, updatedAppointment);
                // Persist changes to CSV file
                saveAllToCsv();
                return;
            }
        }
        System.err.println("Appointment not found for update: " + updatedAppointment.getId());
    }

    // Removes appointment from memory and updates CSV
    public void remove(Appointment a) {
        appointments.remove(a);
        saveAllToCsv();
    }
    
    // Removes appointment by identifier
    public void removeById(String id) {
        Appointment appointmentToRemove = null;
        for (Appointment a : appointments) {
            if (a.getId().equals(id)) {
                appointmentToRemove = a;
                break;
            }
        }
        
        if (appointmentToRemove != null) {
            remove(appointmentToRemove);
        }
    }

    // Retrieves appointment by unique identifier
    public Appointment findById(String id) {
        for (Appointment a : appointments)
            if (a.getId().equals(id)) return a;
        return null;
    }
    
    // Returns all appointments for specific patient
    public List<Appointment> findByPatientId(String patientId) {
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(patientId)) {
                patientAppointments.add(a);
            }
        }
        return patientAppointments;
    }
    
    // Returns all appointments for specific clinician
    public List<Appointment> findByClinicianId(String clinicianId) {
        List<Appointment> clinicianAppointments = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getClinicianId().equals(clinicianId)) {
                clinicianAppointments.add(a);
            }
        }
        return clinicianAppointments;
    }
    
    // Writes all appointments to CSV file (full persistence)
    private void saveAllToCsv() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add CSV header row with column names
            allData.add(new String[]{
                "appointment_id", "patient_id", "clinician_id", "facility_id",
                "appointment_date", "appointment_time", "duration_minutes",
                "appointment_type", "status", "reason_for_visit", "notes",
                "created_date", "last_modified"
            });
            
            // Convert all appointments to CSV rows
            for (Appointment a : appointments) {
                allData.add(new String[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes(),
                    a.getCreatedDate(),
                    a.getLastModified()
                });
            }
            
            // Write complete dataset to CSV file
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to save appointments to CSV: " + ex.getMessage());
        }
    }
}