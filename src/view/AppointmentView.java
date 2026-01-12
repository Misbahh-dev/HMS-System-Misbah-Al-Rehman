package view;

import controller.AppointmentController;
import model.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentView extends JPanel {

    private AppointmentController controller;

    private JTable table;
    private DefaultTableModel model;

    // Form components
    private JTextField txtId, txtDate, txtTime, txtDuration, txtType;
    private JTextField txtReason, txtCreatedDate, txtLastModified;

    private JComboBox<String> cbStatus;
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbClinicianId;
    private JComboBox<String> cbFacilityId;

    private JTextArea txtNotes;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // ============================================================
    // ADDED: UI components for role-based access
    // ============================================================
    private JLabel titleLabel; // For setting window title
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel buttonsPanel;

    public AppointmentView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ============================================================
        // ADDED: TITLE PANEL
        // ============================================================
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ============================================================
        // TABLE
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Clinician", "Facility",
                        "Date", "Time", "Duration (min)", "Type",
                        "Status", "Reason", "Notes", "Created", "Last Modified"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        add(tableScrollPane, BorderLayout.SOUTH);

        // ============================================================
        // FORM
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); 
        txtId.setEditable(false);

        cbPatientId = new JComboBox<>();
        cbClinicianId = new JComboBox<>();
        cbFacilityId = new JComboBox<>();

        txtDate = new JTextField();
        txtTime = new JTextField();
        txtDuration = new JTextField();
        txtType = new JTextField();
        txtReason = new JTextField();

        cbStatus = new JComboBox<>(new String[]{
                "SCHEDULED",
                "RESCHEDULED",
                "CANCELLED",
                "COMPLETED",
                "NO-SHOW"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtNotes = new JTextArea(3, 15);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        txtCreatedDate = new JTextField();
        txtLastModified = new JTextField();

        int row = 0;
        addFieldPair(form, gc, row++, "Appointment ID:", txtId, "Patient ID:", cbPatientId);
        addFieldPair(form, gc, row++, "Clinician ID:", cbClinicianId, "Facility ID:", cbFacilityId);
        addFieldPair(form, gc, row++, "Appointment Date:", txtDate, "Time (HH:mm):", txtTime);
        addFieldPair(form, gc, row++, "Duration (min):", txtDuration, "Appointment Type:", txtType);
        addFieldPair(form, gc, row++, "Status:", cbStatus, "Reason for Visit:", txtReason);
        addFieldPair(form, gc, row++, "Created Date:", txtCreatedDate, "Last Modified:", txtLastModified);

        // Notes row
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        form.add(new JLabel("Notes:"), gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 3;
        form.add(new JScrollPane(txtNotes), gc);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setPreferredSize(new Dimension(800, 300));
        add(formScrollPane, BorderLayout.CENTER);

        // ============================================================
        // BUTTONS (MODIFIED)
        // ============================================================
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnAdd = new JButton("Add Appointment");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Delete Selected");
        
        // Set preferred sizes for vertical alignment
        Dimension buttonSize = new Dimension(150, 35);
        btnAdd.setPreferredSize(buttonSize);
        btnAdd.setMaximumSize(buttonSize);
        btnUpdate.setPreferredSize(buttonSize);
        btnUpdate.setMaximumSize(buttonSize);
        btnDelete.setPreferredSize(buttonSize);
        btnDelete.setMaximumSize(buttonSize);
        
        // Add action listeners
        btnAdd.addActionListener(e -> addAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnDelete.addActionListener(e -> deleteAppointment());
        
        // Add vertical spacing
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(Box.createVerticalStrut(10));
        
        add(buttonsPanel, BorderLayout.EAST);
        
        // ============================================================
        // ADDED: TABLE SELECTION LISTENER
        // ============================================================
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });
    }

    // Helper method for form layout
    private void addFieldPair(JPanel panel, GridBagConstraints gc, int row,
                              String l1, JComponent f1,
                              String l2, JComponent f2) {

        gc.gridwidth = 1;

        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel(l1), gc);

        gc.gridx = 1;
        panel.add(f1, gc);

        gc.gridx = 2;
        panel.add(new JLabel(l2), gc);

        gc.gridx = 3;
        panel.add(f2, gc);
    }

    // ============================================================
    // Dropdown loading
    // ============================================================
    public void loadDropdowns(List<String> patients, List<String> clinicians, List<String> facilities) {
        cbPatientId.removeAllItems();
        cbClinicianId.removeAllItems();
        cbFacilityId.removeAllItems();

        for (String s : patients) cbPatientId.addItem(s);
        for (String s : clinicians) cbClinicianId.addItem(s);
        for (String s : facilities) cbFacilityId.addItem(s);

        txtId.setText(controller.generateId());
        txtCreatedDate.setText(LocalDate.now().format(fmt));
        txtLastModified.setText(LocalDate.now().format(fmt));
    }

    // ============================================================
    // TABLE VIEW UPDATE
    // ============================================================
    public void showAppointments(List<Appointment> list) {
        model.setRowCount(0);

        for (Appointment a : list) {
            model.addRow(new Object[]{
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
    }

    // ============================================================
    // ADD APPOINTMENT
    // ============================================================
    private void addAppointment() {
        if (controller == null) return;
        
        // Validate form
        if (!validateForm()) {
            return;
        }

        Appointment a = new Appointment(
                txtId.getText(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbFacilityId.getSelectedItem(),
                txtDate.getText(),
                txtTime.getText(),
                txtDuration.getText(),
                txtType.getText(),
                (String) cbStatus.getSelectedItem(),
                txtReason.getText(),
                txtNotes.getText(),
                txtCreatedDate.getText(),
                txtLastModified.getText()
        );

        controller.addAppointment(a);
        clearForm();
    }
    
    // ============================================================
    // ADDED: UPDATE APPOINTMENT
    // ============================================================
    private void updateAppointment() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to update.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        Appointment a = new Appointment(
                txtId.getText(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbFacilityId.getSelectedItem(),
                txtDate.getText(),
                txtTime.getText(),
                txtDuration.getText(),
                txtType.getText(),
                (String) cbStatus.getSelectedItem(),
                txtReason.getText(),
                txtNotes.getText(),
                txtCreatedDate.getText(),
                txtLastModified.getText()
        );
        
        // Call update method in controller
        controller.updateAppointment(a);
    }

    // ============================================================
    // DELETE APPOINTMENT
    // ============================================================
    private void deleteAppointment() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        String patient = model.getValueAt(row, 1).toString();
        String date = model.getValueAt(row, 4).toString();
        
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete appointment:\n" +
            "ID: " + id + "\n" +
            "Patient: " + patient + "\n" +
            "Date: " + date + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteById(id);
            clearForm();
        }
    }
    
    // ============================================================
    // ADDED: LOAD SELECTED ROW INTO FORM
    // ============================================================
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        
        txtId.setText(getValue(row, 0));
        cbPatientId.setSelectedItem(getValue(row, 1));
        cbClinicianId.setSelectedItem(getValue(row, 2));
        cbFacilityId.setSelectedItem(getValue(row, 3));
        txtDate.setText(getValue(row, 4));
        txtTime.setText(getValue(row, 5));
        txtDuration.setText(getValue(row, 6));
        txtType.setText(getValue(row, 7));
        cbStatus.setSelectedItem(getValue(row, 8));
        txtReason.setText(getValue(row, 9));
        txtNotes.setText(getValue(row, 10));
        txtCreatedDate.setText(getValue(row, 11));
        txtLastModified.setText(getValue(row, 12));
    }
    
    private String getValue(int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }
    
    // ============================================================
    // ADDED: VALIDATION
    // ============================================================
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (cbPatientId.getSelectedItem() == null) {
            errors.append("- Patient ID is required\n");
        }
        if (cbClinicianId.getSelectedItem() == null) {
            errors.append("- Clinician ID is required\n");
        }
        if (cbFacilityId.getSelectedItem() == null) {
            errors.append("- Facility ID is required\n");
        }
        if (txtDate.getText().trim().isEmpty()) {
            errors.append("- Appointment date is required (dd/MM/yyyy)\n");
        }
        if (txtTime.getText().trim().isEmpty()) {
            errors.append("- Appointment time is required (HH:mm)\n");
        }
        if (txtDuration.getText().trim().isEmpty()) {
            errors.append("- Duration is required (minutes)\n");
        } else {
            try {
                Integer.parseInt(txtDuration.getText().trim());
            } catch (NumberFormatException e) {
                errors.append("- Duration must be a number\n");
            }
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "Please fix the following errors:\n" + errors.toString(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    // ============================================================
    // ADDED: CLEAR FORM
    // ============================================================
    private void clearForm() {
        txtDate.setText("");
        txtTime.setText("");
        txtDuration.setText("");
        txtType.setText("");
        txtReason.setText("");
        txtNotes.setText("");
        txtLastModified.setText(LocalDate.now().format(fmt));
        
        // Don't clear ID, patient, clinician, facility - let dropdowns stay
        // Regenerate ID for next appointment
        if (controller != null) {
            txtId.setText(controller.generateId());
        }
    }

    public void setController(AppointmentController controller) {
        this.controller = controller;
    }
    
    // ============================================================
    // ADDED: METHODS NEEDED BY CONTROLLER
    // ============================================================
    
    public void setReadOnlyMode(boolean readOnly) {
        // Enable/disable all input components
        cbPatientId.setEnabled(!readOnly);
        cbClinicianId.setEnabled(!readOnly);
        cbFacilityId.setEnabled(!readOnly);
        cbStatus.setEnabled(!readOnly);
        
        txtDate.setEditable(!readOnly);
        txtTime.setEditable(!readOnly);
        txtDuration.setEditable(!readOnly);
        txtType.setEditable(!readOnly);
        txtReason.setEditable(!readOnly);
        txtNotes.setEditable(!readOnly);
        txtLastModified.setEditable(!readOnly);
    }
    
    public void hideAddDeleteButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(true); // Keep update button visible
    }
    
    public void showUpdateButton() {
        if (btnUpdate != null) btnUpdate.setVisible(true);
    }
    
    public void showAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(true);
        if (btnUpdate != null) btnUpdate.setVisible(true);
        if (btnDelete != null) btnDelete.setVisible(true);
    }
    
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    // ============================================================
    // ADDED: HIDE ALL BUTTONS (for initial state)
    // ============================================================
    public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
}