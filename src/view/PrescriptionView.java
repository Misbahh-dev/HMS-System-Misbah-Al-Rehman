package view;

import controller.PrescriptionController;
import model.Prescription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PrescriptionView extends JPanel {

    private PrescriptionController controller;

    // Table components for data display
    private JTable table;
    private DefaultTableModel model;

    // Form input fields for prescription data
    private JLabel lblId;
    private JLabel titleLabel;
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbClinicianId;
    private JComboBox<String> cbDrug;
    private JComboBox<String> cbPharmacy;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbAppointmentId;
    private JTextField txtPrescDate;
    private JTextField txtDosage;
    private JTextField txtFrequency;
    private JTextField txtDuration;
    private JTextField txtQuantity;
    private JTextField txtIssueDate;
    private JTextField txtCollectionDate;
    private JTextArea txtInstructions;
//Made By Misbah Al Rehman. SRN: 24173647
    // Action buttons for prescription management
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    private boolean readOnlyMode = false;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

    public PrescriptionView() {
        // Main panel layout with consistent spacing
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title panel displays current view context
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Prescription Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel with form and table
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Form panel for prescription data entry
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.5;

        // Initialize form components
        lblId = new JLabel("RX001");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));

        cbPatientId = new JComboBox<>();
        cbClinicianId = new JComboBox<>();
        cbDrug = new JComboBox<>();
        cbPharmacy = new JComboBox<>();
        cbAppointmentId = new JComboBox<>();

        // Status options for prescription lifecycle
        cbStatus = new JComboBox<>(new String[]{
                "PENDING",
                "ISSUED",
                "COLLECTED",
                "CANCELLED",
                "REJECTED"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPrescDate = new JTextField();
        txtDosage = new JTextField();
        txtFrequency = new JTextField();
        txtDuration = new JTextField();
        txtQuantity = new JTextField();
        txtIssueDate = new JTextField();
        txtCollectionDate = new JTextField();

        txtInstructions = new JTextArea(4, 25);
        txtInstructions.setLineWrap(true);
        txtInstructions.setWrapStyleWord(true);

        int row = 0;
        addPair(form, gc, row++, "Prescription ID:", lblId, "Patient ID:", cbPatientId);
        addPair(form, gc, row++, "Clinician ID:", cbClinicianId, "Appointment ID:", cbAppointmentId);
        addPair(form, gc, row++, "Prescription Date (yyyy-MM-dd):", txtPrescDate, "Drug:", cbDrug);
        addPair(form, gc, row++, "Dosage:", txtDosage, "Frequency:", txtFrequency);
        addPair(form, gc, row++, "Duration (days):", txtDuration, "Quantity:", txtQuantity);
        addPair(form, gc, row++, "Pharmacy:", cbPharmacy, "Status:", cbStatus);
        addPair(form, gc, row++, "Issue Date (yyyy-MM-dd):", txtIssueDate,
                "Collection Date (yyyy-MM-dd):", txtCollectionDate);

        // Instructions field with scroll pane
        gc.gridy = row;
        gc.gridx = 0;
        gc.gridwidth = 1;
        form.add(new JLabel("Instructions:"), gc);

        gc.gridx = 1;
        gc.gridwidth = 3;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 0.5;
        form.add(new JScrollPane(txtInstructions), gc);

        JScrollPane formScrollPane = new JScrollPane(form);
        mainContentPanel.add(formScrollPane, BorderLayout.CENTER);

        // Table setup for prescription data display
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Clinician", "Appt",
                        "Presc Date", "Drug", "Dosage", "Freq",
                        "Duration", "Qty", "Instructions",
                        "Pharmacy", "Status", "Issue", "Collected"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct table editing
            }
        };
        table = new JTable(model);
        table.setRowHeight(22);
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 150));
        mainContentPanel.add(tableScrollPane, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);

        // Action buttons panel with vertical arrangement
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Delete Selected");

        // Standardize button dimensions for consistent UI
        Dimension buttonSize = new Dimension(120, 30);
        btnAdd.setPreferredSize(buttonSize);
        btnAdd.setMaximumSize(buttonSize);
        btnUpdate.setPreferredSize(buttonSize);
        btnUpdate.setMaximumSize(buttonSize);
        btnDelete.setPreferredSize(buttonSize);
        btnDelete.setMaximumSize(buttonSize);

        // Connect buttons to action handlers
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());

        // Arrange buttons vertically with spacing
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnUpdate);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnDelete);
        buttonPanel.add(Box.createVerticalStrut(10));

        add(buttonPanel, BorderLayout.EAST);

        // Load selected row data into form when table selection changes
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowIntoForm();
        });

        // Initial UI state configuration
        updateUIState();
    }

    // Configures form for read-only or editable mode
    public void setReadOnlyMode(boolean readOnly) {
        this.readOnlyMode = readOnly;
        updateUIState();
    }
    
    // Shows all action buttons (admin view)
    public void showAddUpdateButtons() {
        if (btnAdd != null) btnAdd.setVisible(true);
        if (btnUpdate != null) btnUpdate.setVisible(true);
        if (btnDelete != null) btnDelete.setVisible(true);
    }
    
    // Hides add and update buttons (read-only view)
    public void hideAddUpdateButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
    
    // Updates view title based on user role
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    // Updates UI state based on read-only mode
    private void updateUIState() {
        boolean editable = !readOnlyMode;
        
        cbPatientId.setEnabled(editable);
        cbClinicianId.setEnabled(editable);
        cbDrug.setEnabled(editable);
        cbPharmacy.setEnabled(editable);
        cbStatus.setEnabled(editable);
        cbAppointmentId.setEnabled(editable);
        
        txtPrescDate.setEditable(editable);
        txtDosage.setEditable(editable);
        txtFrequency.setEditable(editable);
        txtDuration.setEditable(editable);
        txtQuantity.setEditable(editable);
        txtIssueDate.setEditable(editable);
        txtCollectionDate.setEditable(editable);
        txtInstructions.setEditable(editable);
        
        if (readOnlyMode) {
            hideAddUpdateButtons();
        } else {
            showAddUpdateButtons();
        }
    }
    
    // Helper method for form layout arrangement
    private void addPair(JPanel panel, GridBagConstraints gc, int row,
                         String label1, JComponent field1,
                         String label2, JComponent field2) {

        gc.gridy = row;
        gc.gridx = 0;
        panel.add(new JLabel(label1), gc);
        gc.gridx = 1;
        panel.add(field1, gc);
        gc.gridx = 2;
        panel.add(new JLabel(label2), gc);
        gc.gridx = 3;
        panel.add(field2, gc);
    }

    // Establishes connection to controller for business logic
    public void setController(PrescriptionController controller) {
        this.controller = controller;
    }

    // Populates dropdowns with available data options
    public void populateDropdowns(List<String> patientIds,
                                  List<String> clinicianIds,
                                  List<String> drugs,
                                  List<String> pharmacies,
                                  List<String> appointmentIds) {

        cbPatientId.removeAllItems();
        for (String id : patientIds) cbPatientId.addItem(id);

        cbClinicianId.removeAllItems();
        for (String id : clinicianIds) cbClinicianId.addItem(id);

        cbDrug.removeAllItems();
        for (String d : drugs) cbDrug.addItem(d);

        cbPharmacy.removeAllItems();
        for (String ph : pharmacies) cbPharmacy.addItem(ph);

        cbAppointmentId.removeAllItems();
        for (String ap : appointmentIds) cbAppointmentId.addItem(ap);
    }

    // Sets next available prescription ID for new entries
    public void setNextId(String id) {
        lblId.setText(id);
    }

    // Populates table with prescription list data
    public void showPrescriptions(List<Prescription> list) {
        model.setRowCount(0);
        for (Prescription p : list) {
            model.addRow(new Object[]{
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
        }
    }

    // Handles addition of new prescription record
    private void onAdd() {
        if (controller == null) return;

        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Prescription p = buildFromForm(lblId.getText());
        controller.addPrescription(p);
        clearFormButKeepIds();
    }

    // Handles updating of existing prescription record
    private void onUpdate() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = lblId.getText();
        Prescription p = buildFromForm(id);
        controller.updatePrescription(p);
    }

    // Handles deletion of selected prescription record
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        controller.deleteById(id);
    }

    // Constructs Prescription object from form data
    private Prescription buildFromForm(String id) {
        return new Prescription(
                id,
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbAppointmentId.getSelectedItem(),
                txtPrescDate.getText().trim(),
                (String) cbDrug.getSelectedItem(),
                txtDosage.getText().trim(),
                txtFrequency.getText().trim(),
                txtDuration.getText().trim(),
                txtQuantity.getText().trim(),
                txtInstructions.getText().trim(),
                (String) cbPharmacy.getSelectedItem(),
                (String) cbStatus.getSelectedItem(),
                txtIssueDate.getText().trim(),
                txtCollectionDate.getText().trim()
        );
    }

    // Loads selected table row data into form fields
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        lblId.setText(model.getValueAt(row, 0).toString());
        cbPatientId.setSelectedItem(model.getValueAt(row, 1));
        cbClinicianId.setSelectedItem(model.getValueAt(row, 2));
        cbAppointmentId.setSelectedItem(model.getValueAt(row, 3));
        txtPrescDate.setText(value(row, 4));
        cbDrug.setSelectedItem(model.getValueAt(row, 5));
        txtDosage.setText(value(row, 6));
        txtFrequency.setText(value(row, 7));
        txtDuration.setText(value(row, 8));
        txtQuantity.setText(value(row, 9));
        txtInstructions.setText(value(row, 10));
        cbPharmacy.setSelectedItem(model.getValueAt(row, 11));
        cbStatus.setSelectedItem(model.getValueAt(row, 12));
        txtIssueDate.setText(value(row, 13));
        txtCollectionDate.setText(value(row, 14));
    }

    // Safely retrieves table cell values
    private String value(int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    // Validates form input for required fields and data formats
    private String validateForm() {
        StringBuilder sb = new StringBuilder();

        if (cbPatientId.getSelectedItem() == null)
            sb.append("- Patient ID is required.\n");

        if (cbClinicianId.getSelectedItem() == null)
            sb.append("- Clinician ID is required.\n");

        if (cbDrug.getSelectedItem() == null)
            sb.append("- Drug must be selected.\n");

        if (txtDosage.getText().trim().isEmpty())
            sb.append("- Dosage is required.\n");

        if (!txtDuration.getText().trim().isEmpty()) {
            try { Integer.parseInt(txtDuration.getText().trim()); }
            catch (NumberFormatException e) {
                sb.append("- Duration must be a number.\n");
            }
        }

        if (!txtQuantity.getText().trim().isEmpty()) {
            try { Integer.parseInt(txtQuantity.getText().trim()); }
            catch (NumberFormatException e) {
                sb.append("- Quantity must be a number.\n");
            }
        }

        checkDate(txtPrescDate.getText().trim(), "Prescription Date", sb);
        if (!txtIssueDate.getText().trim().isEmpty())
            checkDate(txtIssueDate.getText().trim(), "Issue Date", sb);
        if (!txtCollectionDate.getText().trim().isEmpty())
            checkDate(txtCollectionDate.getText().trim(), "Collection Date", sb);

        return sb.toString();
    }

    // Validates date format for date fields
    private void checkDate(String value, String label, StringBuilder sb) {
        if (value.isEmpty()) return;
        sdf.setLenient(false);
        try { sdf.parse(value); }
        catch (ParseException e) {
            sb.append("- ").append(label)
              .append(" must be in format ").append(DATE_PATTERN).append(".\n");
        }
    }

    // Resets form fields while preserving ID values
    private void clearFormButKeepIds() {
        txtPrescDate.setText("");
        txtDosage.setText("");
        txtFrequency.setText("");
        txtDuration.setText("");
        txtQuantity.setText("");
        cbStatus.setSelectedIndex(0);
        txtIssueDate.setText("");
        txtCollectionDate.setText("");
        txtInstructions.setText("");
    }
}