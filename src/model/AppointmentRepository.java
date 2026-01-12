package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();
    private final String csvPath;

    public AppointmentRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                // CSV columns (14):
                // 0: appointment_id
                // 1: patient_id
                // 2: clinician_id
                // 3: facility_id
                // 4: appointment_date
                // 5: appointment_time
                // 6: duration_minutes
                // 7: appointment_type
                // 8: status
                // 9: reason_for_visit
                //10: notes
                //11: created_date
                //12: last_modified

                Appointment a = new Appointment(
                        row[0],  // id
                        row[1],  // patient_id
                        row[2],  // clinician_id
                        row[3],  // facility_id
                        row[4],  // appointment_date
                        row[5],  // appointment_time
                        row[6],  // duration_minutes
                        row[7],  // appointment_type
                        row[8],  // status
                        row[9],  // reason_for_visit
                        row[10], // notes
                        row[11], // created_date
                        row[12]  // last_modified
                );

                appointments.add(a);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }

    public List<Appointment> getAll() {
        return appointments;
    }

    // Optional but handy
    public String generateNewId() {
        int max = 0;
        for (Appointment a : appointments) {
            try {
                int n = Integer.parseInt(a.getId().substring(1)); // "A001" → 1
                if (n > max) max = n;
            } catch (Exception ignore) {}
        }
        return String.format("A%03d", max + 1);
    }

    public void add(Appointment a) {
        appointments.add(a);
    }

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
    
    // ============================================================
    // NEW METHOD: UPDATE APPOINTMENT + UPDATE CSV
    // ============================================================
    public void update(Appointment updatedAppointment) {
        // Find the appointment by ID and update their information
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (appointment.getId().equals(updatedAppointment.getId())) {
                // Update the appointment in the list
                appointments.set(i, updatedAppointment);
                
                // Update the CSV file - rewrite entire file
                saveAllToCsv();
                return;
            }
        }
        
        System.err.println("Appointment not found for update: " + updatedAppointment.getId());
    }

    public void remove(Appointment a) {
        appointments.remove(a);
        // Update CSV after removal
        saveAllToCsv();
    }
    
    // ============================================================
    // NEW METHOD: REMOVE BY ID
    // ============================================================
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

    public Appointment findById(String id) {
        for (Appointment a : appointments)
            if (a.getId().equals(id)) return a;
        return null;
    }
    
    // ============================================================
    // NEW METHOD: FIND APPOINTMENTS BY PATIENT ID
    // ============================================================
    public List<Appointment> findByPatientId(String patientId) {
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(patientId)) {
                patientAppointments.add(a);
            }
        }
        return patientAppointments;
    }
    
    // ============================================================
    // NEW METHOD: FIND APPOINTMENTS BY CLINICIAN ID
    // ============================================================
    public List<Appointment> findByClinicianId(String clinicianId) {
        List<Appointment> clinicianAppointments = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getClinicianId().equals(clinicianId)) {
                clinicianAppointments.add(a);
            }
        }
        return clinicianAppointments;
    }
    
    // ============================================================
    // NEW METHOD: SAVE ALL APPOINTMENTS TO CSV
    // ============================================================
    private void saveAllToCsv() {
        try {
            List<String[]> allData = new ArrayList<>();
            
            // Add header row first (based on your CSV structure)
            allData.add(new String[]{
                "appointment_id", "patient_id", "clinician_id", "facility_id",
                "appointment_date", "appointment_time", "duration_minutes",
                "appointment_type", "status", "reason_for_visit", "notes",
                "created_date", "last_modified"
            });
            
            // Add all appointments
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
            
            // Write to CSV using the writeCsv method
            CsvUtils.writeCsv(csvPath, allData);
            
        } catch (IOException ex) {
            System.err.println("Failed to save appointments to CSV: " + ex.getMessage());
        }
    }
}