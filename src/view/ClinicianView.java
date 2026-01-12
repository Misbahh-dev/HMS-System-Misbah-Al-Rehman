package view;

import controller.ClinicianController;
import model.Clinician;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClinicianView extends JPanel {

    private ClinicianController controller;

    private JTable table;
    private DefaultTableModel model;

    // Form components
    private JLabel lblId;
    private JLabel titleLabel; // Added for setting title
    private JTextField txtFirstName, txtLastName, txtSpeciality, txtGmc, txtPhone, txtEmail, txtWorkplaceId;
    private JComboBox<String> cmbTitle, cmbWorkplaceType, cmbEmployment;
    private JSpinner dateSpinner;
    
    // Button references
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel buttonsPanel;
    
    // Date formatter
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ClinicianView() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ============================================================
        // TITLE PANEL (ADDED)
        // ============================================================
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Clinician Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ============================================================
        // TABLE
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Title", "First", "Last", "Speciality", "GMC",
                        "Phone", "Email", "Workplace ID", "Workplace Type",
                        "Employment", "Start Date"
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
        // FORM PANEL (4 COLUMNS)
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Clinician Information"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 15, 10, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        lblId = new JLabel("C001");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtSpeciality = new JTextField();
        txtGmc = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtWorkplaceId = new JTextField();

        cmbTitle = new JComboBox<>(new String[]{"GP", "Consultant", "Nurse", "Specialist"});
        cmbWorkplaceType = new JComboBox<>(new String[]{"GP Surgery", "Hospital", "Clinic"});
        cmbEmployment = new JComboBox<>(new String[]{"Full-time", "Part-time", "Locum"});

        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        int row = 0;

        // ============================  4 COLUMNS  ============================
        add4(form, gc, row++, "Clinician ID:", lblId, "Title:", cmbTitle);
        add4(form, gc, row++, "First Name:", txtFirstName, "Last Name:", txtLastName);
        add4(form, gc, row++, "Speciality:", txtSpeciality, "GMC Number:", txtGmc);
        add4(form, gc, row++, "Phone:", txtPhone, "Email:", txtEmail);
        add4(form, gc, row++, "Workplace ID:", txtWorkplaceId, "Workplace Type:", cmbWorkplaceType);
        add4(form, gc, row++, "Employment:", cmbEmployment, "Start Date:", dateSpinner);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setPreferredSize(new Dimension(800, 300));
        add(formScrollPane, BorderLayout.CENTER);

        // ============================================================
        // BUTTON PANEL (MODIFIED)
        // ============================================================
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnAdd = new JButton("Add Clinician");
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
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        
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
        // TABLE SELECTION LISTENER (ADDED)
        // ============================================================
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });
    }

    // Helper for 4-column rows
    private void add4(JPanel panel, GridBagConstraints gc, int row,
                      String label1, JComponent field1,
                      String label2, JComponent field2) {

        gc.gridy = row;

        // Left label
        gc.gridx = 0;
        gc.weightx = 0.15;
        panel.add(new JLabel(label1), gc);

        // Left field
        gc.gridx = 1;
        gc.weightx = 0.35;
        panel.add(field1, gc);

        // Right label
        gc.gridx = 2;
        gc.weightx = 0.15;
        panel.add(new JLabel(label2), gc);

        // Right field
        gc.gridx = 3;
        gc.weightx = 0.35;
        panel.add(field2, gc);
    }

    // ============================================================
    // CONTROLLER LINK
    // ============================================================
    public void setController(ClinicianController controller) {
        this.controller = controller;
    }

    // ============================================================
    // DISPLAY DATA
    // ============================================================
    public void showClinicians(List<Clinician> list) {
        model.setRowCount(0);

        for (Clinician c : list) {
            model.addRow(new Object[]{
                    c.getId(), c.getTitle(), c.getFirstName(), c.getLastName(),
                    c.getSpeciality(), c.getGmcNumber(), c.getPhone(), c.getEmail(),
                    c.getWorkplaceId(), c.getWorkplaceType(),
                    c.getEmploymentStatus(), c.getStartDate()
            });
        }
    }

    // ============================================================
    // ADD NEW CLINICIAN
    // ============================================================
    private void onAdd() {
        if (controller == null) return;
        
        // Validate form
        if (!validateForm()) {
            return;
        }

        Clinician c = new Clinician(
                lblId.getText(),
                (String) cmbTitle.getSelectedItem(),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtSpeciality.getText(),
                txtGmc.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtWorkplaceId.getText(),
                (String) cmbWorkplaceType.getSelectedItem(),
                (String) cmbEmployment.getSelectedItem(),
                dateFormat.format(dateSpinner.getValue())
        );

        controller.addClinician(c);
        clearForm();
    }
    
    // ============================================================
    // UPDATE CLINICIAN (ADDED)
    // ============================================================
    private void onUpdate() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a clinician to update.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        Clinician c = new Clinician(
                lblId.getText(),
                (String) cmbTitle.getSelectedItem(),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtSpeciality.getText(),
                txtGmc.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtWorkplaceId.getText(),
                (String) cmbWorkplaceType.getSelectedItem(),
                (String) cmbEmployment.getSelectedItem(),
                dateFormat.format(dateSpinner.getValue())
        );
        
        // Call update method in controller (make sure this method exists in ClinicianController)
        if (controller != null) {
            // You might need to add an updateClinician method to your controller
            // For now, we'll call a method that might exist
            try {
                // Try to call updateClinician if it exists
                controller.getClass().getMethod("updateClinician", Clinician.class).invoke(controller, c);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Update functionality not available yet.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // DELETE CLINICIAN
    // ============================================================
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a clinician to delete!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) model.getValueAt(row, 0);
        String name = getValue(row, 2) + " " + getValue(row, 3);
        
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete clinician:\n" + 
            "ID: " + id + "\n" +
            "Name: " + name + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteById(id);
            clearForm();
        }
    }
    
    // ============================================================
    // LOAD SELECTED ROW INTO FORM (ADDED)
    // ============================================================
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        
        lblId.setText(getValue(row, 0));
        cmbTitle.setSelectedItem(getValue(row, 1));
        txtFirstName.setText(getValue(row, 2));
        txtLastName.setText(getValue(row, 3));
        txtSpeciality.setText(getValue(row, 4));
        txtGmc.setText(getValue(row, 5));
        txtPhone.setText(getValue(row, 6));
        txtEmail.setText(getValue(row, 7));
        txtWorkplaceId.setText(getValue(row, 8));
        cmbWorkplaceType.setSelectedItem(getValue(row, 9));
        cmbEmployment.setSelectedItem(getValue(row, 10));
        
        // Handle date parsing
        String dateStr = getValue(row, 11);
        if (!dateStr.isEmpty()) {
            try {
                Date date = dateFormat.parse(dateStr);
                dateSpinner.setValue(date);
            } catch (ParseException e) {
                // If date parsing fails, keep current date
                System.err.println("Failed to parse date: " + dateStr);
            }
        }
    }
    
    private String getValue(int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }
    
    // ============================================================
    // VALIDATION (ADDED)
    // ============================================================
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (txtFirstName.getText().trim().isEmpty()) {
            errors.append("- First Name is required\n");
        }
        if (txtLastName.getText().trim().isEmpty()) {
            errors.append("- Last Name is required\n");
        }
        if (txtSpeciality.getText().trim().isEmpty()) {
            errors.append("- Speciality is required\n");
        }
        if (txtGmc.getText().trim().isEmpty()) {
            errors.append("- GMC Number is required\n");
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
    // CLEAR FORM (ADDED)
    // ============================================================
    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtSpeciality.setText("");
        txtGmc.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtWorkplaceId.setText("");
        cmbTitle.setSelectedIndex(0);
        cmbWorkplaceType.setSelectedIndex(0);
        cmbEmployment.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        
        // Don't clear the ID - let controller set it
    }
    
    // ============================================================
    // METHODS NEEDED BY CONTROLLER (ADDED)
    // ============================================================
    
    public void setReadOnlyMode(boolean readOnly) {
        // Enable/disable all input components
        txtFirstName.setEditable(!readOnly);
        txtLastName.setEditable(!readOnly);
        txtSpeciality.setEditable(!readOnly);
        txtGmc.setEditable(!readOnly);
        txtPhone.setEditable(!readOnly);
        txtEmail.setEditable(!readOnly);
        txtWorkplaceId.setEditable(!readOnly);
        
        cmbTitle.setEnabled(!readOnly);
        cmbWorkplaceType.setEnabled(!readOnly);
        cmbEmployment.setEnabled(!readOnly);
        dateSpinner.setEnabled(!readOnly);
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
    
    public void setNextId(String id) {
        if (lblId != null) {
            lblId.setText(id);
        }
    }
    
    // ============================================================
    // ADDITIONAL HELPER METHOD (Optional)
    // ============================================================
    public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
}