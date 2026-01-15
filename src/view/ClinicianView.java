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

    // Controller for business logic operations
    private ClinicianController controller;

    // Table components for data display
    private JTable table;
    private DefaultTableModel model;
//Made By Misbah Al Rehman. SRN: 24173647
    // Form input fields for clinician data
    private JLabel lblId;
    private JLabel titleLabel;
    private JTextField txtFirstName, txtLastName, txtSpeciality, txtGmc, txtPhone, txtEmail, txtWorkplaceId;
    private JComboBox<String> cmbTitle, cmbWorkplaceType, cmbEmployment;
    private JSpinner dateSpinner;
    
    // Action buttons for clinician management
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel buttonsPanel;
    
    // Date formatting utility for consistent display
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ClinicianView() {

        // Main panel layout with spacing
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title panel displays current view context
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Clinician Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table setup for clinician data display
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Title", "First", "Last", "Speciality", "GMC",
                        "Phone", "Email", "Workplace ID", "Workplace Type",
                        "Employment", "Start Date"
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
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        add(tableScrollPane, BorderLayout.SOUTH);

        // Form panel for data entry with four-column layout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Clinician Information"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 15, 10, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize form components
        lblId = new JLabel("C001");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtSpeciality = new JTextField();
        txtGmc = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtWorkplaceId = new JTextField();

        // Dropdown options for categorical fields
        cmbTitle = new JComboBox<>(new String[]{"GP", "Consultant", "Nurse", "Specialist", "Senior Nurse", "Practice Nurse", "Staff Nurse"});
        cmbWorkplaceType = new JComboBox<>(new String[]{"GP Surgery", "Hospital", "Clinic"});
        cmbEmployment = new JComboBox<>(new String[]{"Full-time", "Part-time", "Locum"});

        // Date selector with standard format
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        int row = 0;

        // Arrange form fields in four-column layout
        add4(form, gc, row++, "Clinician ID:", lblId, "Title:", cmbTitle);
        add4(form, gc, row++, "First Name:", txtFirstName, "Last Name:", txtLastName);
        add4(form, gc, row++, "Speciality:", txtSpeciality, "GMC Number:", txtGmc);
        add4(form, gc, row++, "Phone:", txtPhone, "Email:", txtEmail);
        add4(form, gc, row++, "Workplace ID:", txtWorkplaceId, "Workplace Type:", cmbWorkplaceType);
        add4(form, gc, row++, "Employment:", cmbEmployment, "Start Date:", dateSpinner);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setPreferredSize(new Dimension(800, 300));
        add(formScrollPane, BorderLayout.CENTER);

        // Action buttons panel with vertical arrangement
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnAdd = new JButton("Add Clinician");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Delete Selected");
        
        // Standardize button dimensions for consistent UI
        Dimension buttonSize = new Dimension(150, 35);
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
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(Box.createVerticalStrut(10));
        
        add(buttonsPanel, BorderLayout.EAST);
        
        // Load selected row data into form when table selection changes
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });
    }

    // Helper method for four-column form layout arrangement
    private void add4(JPanel panel, GridBagConstraints gc, int row,
                      String label1, JComponent field1,
                      String label2, JComponent field2) {

        gc.gridy = row;

        // Left column label positioning
        gc.gridx = 0;
        gc.weightx = 0.15;
        panel.add(new JLabel(label1), gc);

        // Left column input field positioning
        gc.gridx = 1;
        gc.weightx = 0.35;
        panel.add(field1, gc);

        // Right column label positioning
        gc.gridx = 2;
        gc.weightx = 0.15;
        panel.add(new JLabel(label2), gc);

        // Right column input field positioning
        gc.gridx = 3;
        gc.weightx = 0.35;
        panel.add(field2, gc);
    }

    // Establishes connection to controller for business logic
    public void setController(ClinicianController controller) {
        this.controller = controller;
    }

    // Populates table with clinician list data
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

    // Handles addition of new clinician record
    private void onAdd() {
        if (controller == null) return;
        
        // Validate input before proceeding
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
    
    // Handles updating of existing clinician record
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
        
        // Validate input before proceeding
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
        
        controller.updateClinician(c);
    }

    // Handles deletion of selected clinician record
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
        
        // Confirmation dialog for deletion
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
    
    // Loads selected table row data into form fields
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
        
        // Parse and set date value
        String dateStr = getValue(row, 11);
        if (!dateStr.isEmpty()) {
            try {
                Date date = dateFormat.parse(dateStr);
                dateSpinner.setValue(date);
            } catch (ParseException e) {
                System.err.println("Failed to parse date: " + dateStr);
            }
        }
    }
    
    // Safely retrieves table cell values
    private String getValue(int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }
    
    // Validates form input for required fields
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
    
    // Resets form fields to default empty state
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
    }
    
    // Configures form for read-only or editable mode
    public void setReadOnlyMode(boolean readOnly) {
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
    
    // Hides add and delete buttons (clinician view)
    public void hideAddDeleteButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(true);
    }
    
    // Shows update button explicitly
    public void showUpdateButton() {
        if (btnUpdate != null) btnUpdate.setVisible(true);
    }
    
    // Shows all action buttons (admin view)
    public void showAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(true);
        if (btnUpdate != null) btnUpdate.setVisible(true);
        if (btnDelete != null) btnDelete.setVisible(true);
    }
    
    // Updates view title based on user role
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    // Sets next available clinician ID for new entries
    public void setNextId(String id) {
        if (lblId != null) {
            lblId.setText(id);
        }
    }
    
    // Hides all action buttons (initial state)
    public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
}