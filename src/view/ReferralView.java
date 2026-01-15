package view;

import controller.ReferralController;
import model.Referral;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReferralView extends JPanel {

    private ReferralController controller;
    private JTable table;
    private DefaultTableModel model;
//Made By Misbah Al Rehman. SRN: 24173647
    // Form input fields for referral data
    private JTextField txtId, txtReason, txtRequestedService,
            txtCreatedDate, txtLastUpdated;
    private JTextArea txtClinicalSummary, txtNotes;
    private JFormattedTextField txtReferralDate;
    private JLabel titleLabel;

    // Dropdown selectors for related entities
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbRefClin, cbToClin;
    private JComboBox<String> cbRefFacility, cbToFacility;
    private JComboBox<String> cbUrgency;
    private JComboBox<String> cbAppointmentId;
    private JComboBox<String> cbStatus;

    // Action buttons for referral management
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    // Date formatting utilities
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateTimeFormatter localDateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReferralView() {
        // Main panel layout with consistent spacing
        setLayout(new BorderLayout(10,10));
        
        // Title panel displays current view context
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Referral Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel with form on top, table on bottom
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Form panel for referral data entry
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setLayout(new GridLayout(0, 4, 20, 10));

        // Initialize form components
        txtId = createField();
        txtReason = createField();
        txtRequestedService = createField();
        txtCreatedDate = createField(); txtCreatedDate.setEditable(false);
        txtLastUpdated = createField(); txtLastUpdated.setEditable(false);

        cbPatientId = createCombo();
        cbRefClin = createCombo();
        cbToClin = createCombo();
        cbRefFacility = createCombo();
        cbToFacility = createCombo();
        cbAppointmentId = createCombo();

        // Urgency classification options
        cbUrgency = new JComboBox<>(new String[]{
                "Routine",
                "Urgent",
                "Non-urgent",
                "2-week wait"
        });
        cbUrgency.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Referral status options
        cbStatus = new JComboBox<>(new String[]{
                "Pending",
                "Sent",
                "Received",
                "In Review",
                "Accepted",
                "Rejected",
                "Completed",
                "Cancelled"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtReferralDate = createDateField();
        txtClinicalSummary = createArea();
        txtNotes = createArea();

        // Add form components in labeled panels
        formPanel.add(labeled("Referral ID:", txtId));
        formPanel.add(labeled("Patient ID:", cbPatientId));
        formPanel.add(labeled("Referring Clinician ID:", cbRefClin));
        formPanel.add(labeled("Referred-To Clinician ID:", cbToClin));
        formPanel.add(labeled("Referring Facility ID:", cbRefFacility));
        formPanel.add(labeled("Referred-To Facility ID:", cbToFacility));
        formPanel.add(labeled("Referral Date (dd/MM/yyyy):", txtReferralDate));
        formPanel.add(labeled("Urgency Level:", cbUrgency));
        formPanel.add(labeled("Referral Reason:", txtReason));
        formPanel.add(labeled("Requested Service:", txtRequestedService));
        formPanel.add(labeled("Status:", cbStatus));
        formPanel.add(labeled("Appointment ID:", cbAppointmentId));
        formPanel.add(labeled("Clinical Summary:", new JScrollPane(txtClinicalSummary)));
        formPanel.add(labeled("Notes:", new JScrollPane(txtNotes)));
        formPanel.add(labeled("Created Date:", txtCreatedDate));
        formPanel.add(labeled("Last Updated:", txtLastUpdated));

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        mainContentPanel.add(formContainer, BorderLayout.NORTH);

        // Table panel for referral data display
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Referrals List"));
        
        // Table model with all referral fields
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Ref Clin", "To Clin",
                        "Ref Facility", "To Facility", "Date", "Urgency",
                        "Reason", "Clinical Summary", "Requested Service",
                        "Status", "Appointment", "Notes", "Created", "Updated"
                }, 0
        );

        table = new JTable(model);
        table.setRowHeight(18);
        
        // Load selected row data into form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromSelectedRow();
            }
        });
        
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainContentPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // Action buttons panel with vertical arrangement
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        btnAdd = new JButton("Create Referral");
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
    }
   
    // Helper methods for component creation
    private JTextField createField() {
        JTextField f = new JTextField(12);
        f.setPreferredSize(new Dimension(140, 22));
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return f;
    }

    private JTextArea createArea() {
        JTextArea a = new JTextArea(2, 10);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return a;
    }

    private JComboBox<String> createCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return cb;
    }

    private JFormattedTextField createDateField() {
        DateFormatter df = new DateFormatter(dateFormat);
        JFormattedTextField f = new JFormattedTextField(df);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setValue(java.util.Date.from(LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        return f;
    }

    private JPanel labeled(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout(3, 2));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
    
    // Updates view title based on user role
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    // Establishes connection to controller for business logic
    public void setController(ReferralController controller) {
        this.controller = controller;
        loadCombos();
        refreshAutoId();
        refreshDates();
    }

    // Populates dropdowns with available data options
    private void loadCombos() {
        cbPatientId.removeAllItems();
        cbRefClin.removeAllItems();
        cbToClin.removeAllItems();
        cbRefFacility.removeAllItems();
        cbToFacility.removeAllItems();
        cbAppointmentId.removeAllItems();

        // Load patient IDs
        for (String id : controller.getPatientIds()) {
            cbPatientId.addItem(id);
        }

        // Load clinician IDs
        for (String id : controller.getClinicianIds()) {
            cbRefClin.addItem(id);
            cbToClin.addItem(id);
        }

        // Load facility IDs
        for (String id : controller.getFacilityIds()) {
            cbRefFacility.addItem(id);
            cbToFacility.addItem(id);
        }

        // Load appointment IDs
        for (String id : controller.getAppointmentIds()) {
            cbAppointmentId.addItem(id);
        }
    }

    // Sets next available referral ID
    private void refreshAutoId() {
        txtId.setText(controller.getNextReferralId());
        txtId.setEditable(false);
    }

    // Sets current date values for timestamps
    private void refreshDates() {
        String today = LocalDate.now().format(localDateFormatter);
        txtCreatedDate.setText(today);
        txtLastUpdated.setText(today);
    }

    // Populates table with referral list data
    public void showReferrals(List<Referral> list) {
        model.setRowCount(0);
        for (Referral r : list) {
            model.addRow(new Object[]{
                    r.getId(), r.getPatientId(), r.getReferringClinicianId(),
                    r.getReferredToClinicianId(), r.getReferringFacilityId(),
                    r.getReferredToFacilityId(), r.getReferralDate(),
                    r.getUrgencyLevel(), r.getReferralReason(),
                    r.getClinicalSummary(), r.getRequestedService(),
                    r.getStatus(), r.getAppointmentId(), r.getNotes(),
                    r.getCreatedDate(), r.getLastUpdated()
            });
        }
    }

    // Loads selected table row data into form fields
    private void fillFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(getTableValue(selectedRow, 0));
            setComboBoxSelection(cbPatientId, getTableValue(selectedRow, 1));
            setComboBoxSelection(cbRefClin, getTableValue(selectedRow, 2));
            setComboBoxSelection(cbToClin, getTableValue(selectedRow, 3));
            setComboBoxSelection(cbRefFacility, getTableValue(selectedRow, 4));
            setComboBoxSelection(cbToFacility, getTableValue(selectedRow, 5));
            txtReferralDate.setText(getTableValue(selectedRow, 6));
            setComboBoxSelection(cbUrgency, getTableValue(selectedRow, 7));
            txtReason.setText(getTableValue(selectedRow, 8));
            txtClinicalSummary.setText(getTableValue(selectedRow, 9));
            txtRequestedService.setText(getTableValue(selectedRow, 10));
            setComboBoxSelection(cbStatus, getTableValue(selectedRow, 11));
            setComboBoxSelection(cbAppointmentId, getTableValue(selectedRow, 12));
            txtNotes.setText(getTableValue(selectedRow, 13));
            txtCreatedDate.setText(getTableValue(selectedRow, 14));
            txtLastUpdated.setText(getTableValue(selectedRow, 15));
        }
    }
    
    // Safely retrieves table cell values
    private String getTableValue(int row, int column) {
        Object value = table.getValueAt(row, column);
        return value != null ? value.toString() : "";
    }
    
    // Sets combobox selection with validation
    private void setComboBoxSelection(JComboBox<String> comboBox, String value) {
        if (value == null || value.isEmpty()) {
            comboBox.setSelectedIndex(-1);
        } else {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                if (comboBox.getItemAt(i).equals(value)) {
                    comboBox.setSelectedIndex(i);
                    return;
                }
            }
            // Add value if not found in list
            comboBox.addItem(value);
            comboBox.setSelectedItem(value);
        }
    }

    // Handles addition of new referral record
    private void onAdd() {
        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Referral r = new Referral(
                txtId.getText().trim(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbRefClin.getSelectedItem(),
                (String) cbToClin.getSelectedItem(),
                (String) cbRefFacility.getSelectedItem(),
                (String) cbToFacility.getSelectedItem(),
                txtReferralDate.getText().trim(),
                (String) cbUrgency.getSelectedItem(),
                txtReason.getText().trim(),
                txtClinicalSummary.getText().trim(),
                txtRequestedService.getText().trim(),
                (String) cbStatus.getSelectedItem(),
                (String) cbAppointmentId.getSelectedItem(),
                txtNotes.getText().trim(),
                txtCreatedDate.getText().trim(),
                LocalDate.now().format(localDateFormatter)
        );

        controller.addReferral(r);
        JOptionPane.showMessageDialog(this,
                "Referral " + r.getId() + " created successfully.");
        refreshAutoId();
        refreshDates();
        clearFormButKeepIds();
    }

    // Constructs Referral object from form data
    private Referral buildReferralFromForm() {
        return new Referral(
                txtId.getText().trim(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbRefClin.getSelectedItem(),
                (String) cbToClin.getSelectedItem(),
                (String) cbRefFacility.getSelectedItem(),
                (String) cbToFacility.getSelectedItem(),
                txtReferralDate.getText().trim(),
                (String) cbUrgency.getSelectedItem(),
                txtReason.getText().trim(),
                txtClinicalSummary.getText().trim(),
                txtRequestedService.getText().trim(),
                (String) cbStatus.getSelectedItem(),
                (String) cbAppointmentId.getSelectedItem(),
                txtNotes.getText().trim(),
                txtCreatedDate.getText().trim(),
                java.time.LocalDate.now().format(localDateFormatter)
        );
    }

    // Handles updating of existing referral record
    private void onUpdate() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a referral to update.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Referral r = buildReferralFromForm();
        controller.updateReferral(r);
        
        JOptionPane.showMessageDialog(this,
                "Referral " + r.getId() + " updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        
        refreshAutoId();
    }

    // Handles deletion of selected referral record
    private void onDelete() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a referral to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = table.getValueAt(row, 0).toString();
        String reason = table.getValueAt(row, 8).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete referral:\n" +
            "ID: " + id + "\n" +
            "Reason: " + reason + "\n\n" +
            "This will add a deletion note to the text file.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteReferral(id);
            clearFormButKeepIds();
            JOptionPane.showMessageDialog(this, 
                "Referral deleted successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Validates form input for required fields
    private String validateForm() {
        StringBuilder sb = new StringBuilder();

        if (cbPatientId.getSelectedItem() == null)
            sb.append("- Patient ID required\n");
        if (cbRefClin.getSelectedItem() == null)
            sb.append("- Referring clinician required\n");
        if (cbToClin.getSelectedItem() == null)
            sb.append("- Referred-to clinician required\n");
        if (cbRefFacility.getSelectedItem() == null)
            sb.append("- Referring facility required\n");
        if (cbToFacility.getSelectedItem() == null)
            sb.append("- Referred-to facility required\n");
        if (txtReferralDate.getText().trim().isEmpty())
            sb.append("- Referral date required\n");
        if (txtReason.getText().trim().isEmpty())
            sb.append("- Referral reason required\n");
        if (txtClinicalSummary.getText().trim().isEmpty())
            sb.append("- Clinical summary required\n");

        return sb.toString();
    }

    // Resets form fields while preserving ID values
    private void clearFormButKeepIds() {
        txtReason.setText("");
        txtClinicalSummary.setText("");
        txtRequestedService.setText("");
        txtNotes.setText("");
    }

    // Configures form for read-only or editable mode
    public void setReadOnlyMode(boolean readOnly) {
        txtReason.setEditable(!readOnly);
        txtClinicalSummary.setEditable(!readOnly);
        txtNotes.setEditable(!readOnly);
        txtRequestedService.setEditable(!readOnly);
        
        cbPatientId.setEnabled(!readOnly);
        cbRefClin.setEnabled(!readOnly);
        cbToClin.setEnabled(!readOnly);
        cbRefFacility.setEnabled(!readOnly);
        cbToFacility.setEnabled(!readOnly);
        cbUrgency.setEnabled(!readOnly);
        cbStatus.setEnabled(!readOnly);
        cbAppointmentId.setEnabled(!readOnly);
        txtReferralDate.setEditable(!readOnly);
        btnAdd.setVisible(!readOnly);
        if (btnUpdate != null) btnUpdate.setVisible(!readOnly);
        if (btnDelete != null) btnDelete.setVisible(!readOnly);
    }
    
    // Hides add and update buttons (read-only view)
    public void hideAddUpdateButtons() {
        btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
    
    // Shows all action buttons (admin view)
    public void showAddUpdateButtons() {
        btnAdd.setVisible(true);
        if (btnUpdate != null) btnUpdate.setVisible(true);
        if (btnDelete != null) btnDelete.setVisible(true);
    }

    // Shows update and delete buttons only
    public void showUpdateDeleteButtons() {
        if (btnUpdate != null) btnUpdate.setVisible(true);
        if (btnDelete != null) btnDelete.setVisible(true);
    }

    // Hides update and delete buttons
    public void hideUpdateDeleteButtons() {
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
}