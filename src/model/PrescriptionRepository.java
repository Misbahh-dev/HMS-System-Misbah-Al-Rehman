package model;

import java.io.IOException;
import java.util.*;

public class PrescriptionRepository {

    // In-memory storage for prescription records
    private final List<Prescription> prescriptions = new ArrayList<>();
    // File system path for CSV persistence
    private final String csvPath;

    // CSV structure definition - exactly 15 columns expected
    private static final int COLUMN_COUNT = 15;

    // Constructor - loads data from CSV file on initialization
    public PrescriptionRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
//Made By Misbah Al Rehman. SRN: 24173647
    // Loads prescription data from CSV with safety validation
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                // Skip header row and empty entries
                if (row.length == 0 || row[0].equalsIgnoreCase("prescription_id"))
                    continue;

                // Ensure consistent column count to prevent indexing errors
                String[] safe = new String[COLUMN_COUNT];
                for (int i = 0; i < COLUMN_COUNT; i++) {
                    safe[i] = (i < row.length) ? row[i] : "";
                }

                // Create Prescription object from CSV data
                Prescription p = new Prescription(
                        safe[0], // prescription_id - unique identifier
                        safe[1], // patient_id - prescribed patient
                        safe[2], // clinician_id - prescribing clinician
                        safe[3], // appointment_id - related appointment
                        safe[4], // prescription_date - creation date
                        safe[5], // medication_name - drug name
                        safe[6], // dosage - strength and unit
                        safe[7], // frequency - administration schedule
                        safe[8], // duration_days - treatment length
                        safe[9], // quantity - total amount dispensed
                        safe[10],// instructions - usage directions
                        safe[11],// pharmacy_name - dispensing location
                        safe[12],// status - prescription state
                        safe[13],// issue_date - original issue date
                        safe[14] // collection_date - patient pickup date
                );

                prescriptions.add(p);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load prescriptions: " + ex.getMessage());
        }
    }

    // Returns all prescription records in the repository
    public List<Prescription> getAll() {
        return prescriptions;
    }

    // Generates next sequential prescription identifier
    public String generateNewId() {
        int max = 0;
        for (Prescription p : prescriptions) {
            try {
                String id = p.getId();
                if (id != null && id.startsWith("RX")) {
                    // Extract numeric portion from ID (e.g., "RX001" → 1)
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) max = num;
                }
            } catch (Exception ignore) {}
        }
        return String.format("RX%03d", max + 1);
    }

    // Returns unique medication names for dropdown population
    public List<String> getMedicationOptions() {
        Set<String> meds = new TreeSet<>();
        for (Prescription p : prescriptions) {
            if (p.getMedication() != null && !p.getMedication().isBlank())
                meds.add(p.getMedication());
        }
        return new ArrayList<>(meds);
    }

    // Returns unique pharmacy names for dropdown population
    public List<String> getPharmacyOptions() {
        Set<String> pharms = new TreeSet<>();
        for (Prescription p : prescriptions) {
            if (p.getPharmacyName() != null && !p.getPharmacyName().isBlank())
                pharms.add(p.getPharmacyName());
        }
        return new ArrayList<>(pharms);
    }

    // Adds prescription to memory and appends to CSV file
    public void addAndAppend(Prescription p) {
        prescriptions.add(p);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    p.getId(),
                    p.getPatientId(),
                    p.getClinicianId(),
                    p.getAppointmentId(),
                    p.getPrescriptionDate(),
                    p.getMedication(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getDurationDays(),
                    p.getQuantity(),
                    p.getInstructions(),
                    p.getPharmacyName(),
                    p.getStatus(),
                    p.getIssueDate(),
                    p.getCollectionDate()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append prescription: " + ex.getMessage());
        }
    }

    // Updates existing prescription in memory 
    public void update(Prescription p) {
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getId().equals(p.getId())) {
                prescriptions.set(i, p);
                return;
            }
        }
    }

    // Removes prescription by identifier from memory
    public void removeById(String id) {
        prescriptions.removeIf(p -> p.getId().equals(id));
        // Note: CSV file not rewritten 
    }
}