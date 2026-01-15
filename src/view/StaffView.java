package view;

import controller.StaffController;
import model.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffView extends JPanel {

    private StaffController controller;

    // Table components for data display
    private JTable table;
    private DefaultTableModel tableModel;
//Made By Misbah Al Rehman. SRN: 24173647
    // Form input fields for staff data
    private JLabel lblAutoId;
    private JTextField txtFirstName, txtLastName, txtPosition;
    private JTextField txtDepartment, txtFacilityId, txtPhone, txtEmail;
    private JTextField txtEmploymentStatus, txtStartDate, txtLineManager, txtAccessLevel;
    
    // Action buttons for staff management
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel buttonsPanel;
    
    // View title display
    private JLabel titleLabel;

    public StaffView() {
        // Main panel layout with consistent spacing
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title panel displays current view context
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Staff Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table setup for staff data display
        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID", "First Name", "Last Name", "Position", "Department",
                        "Facility ID", "Phone", "Email", "Employment Status",
                        "Start Date", "Line Manager", "Access Level"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct table editing
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        add(tableScrollPane, BorderLayout.SOUTH);

        // Form panel for data entry with four-column layout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Staff Information"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 15, 10, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize form components
        lblAutoId = new JLabel("ST001");
        lblAutoId.setFont(new Font("SansSerif", Font.BOLD, 12));

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtPosition = new JTextField();
        txtDepartment = new JTextField();
        txtFacilityId = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtEmploymentStatus = new JTextField();
        txtStartDate = new JTextField();
        txtLineManager = new JTextField();
        txtAccessLevel = new JTextField();

        int row = 0;

        // Arrange form fields in four-column layout
        add4(form, gc, row++, "Staff ID:", lblAutoId, "First Name:", txtFirstName);
        add4(form, gc, row++, "Last Name:", txtLastName, "Position:", txtPosition);
        add4(form, gc, row++, "Department:", txtDepartment, "Facility ID:", txtFacilityId);
        add4(form, gc, row++, "Phone:", txtPhone, "Email:", txtEmail);
        add4(form, gc, row++, "Employment Status:", txtEmploymentStatus, "Start Date:", txtStartDate);
        add4(form, gc, row++, "Line Manager:", txtLineManager, "Access Level:", txtAccessLevel);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setPreferredSize(new Dimension(800, 300));
        add(formScrollPane, BorderLayout.CENTER);

        // Action buttons panel with vertical arrangement
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        btnAdd = new JButton("Add Staff");
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
        gc.gridx = 0; gc.weightx = 0.15;
        panel.add(new JLabel(label1), gc);
        gc.gridx = 1; gc.weightx = 0.35;
        panel.add(field1, gc);
        gc.gridx = 2; gc.weightx = 0.15;
        panel.add(new JLabel(label2), gc);
        gc.gridx = 3; gc.weightx = 0.35;
        panel.add(field2, gc);
    }

    // Establishes connection to controller for business logic
    public void setController(StaffController controller) {
        this.controller = controller;
    }
    
    // Configures form for read-only or editable mode
    public void setReadOnlyMode(boolean readOnly) {
        txtFirstName.setEditable(!readOnly);
        txtLastName.setEditable(!readOnly);
        txtPosition.setEditable(!readOnly);
        txtDepartment.setEditable(!readOnly);
        txtFacilityId.setEditable(!readOnly);
        txtPhone.setEditable(!readOnly);
        txtEmail.setEditable(!readOnly);
        txtEmploymentStatus.setEditable(!readOnly);
        txtStartDate.setEditable(!readOnly);
        txtLineManager.setEditable(!readOnly);
        txtAccessLevel.setEditable(!readOnly);
    }
    
    // Hides all action buttons (initial state)
    public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
    
    // Hides add and delete buttons (staff view)
    public void hideAddDeleteButtons() {
        btnAdd.setVisible(false);
        btnDelete.setVisible(false);
        btnUpdate.setVisible(true);
    }
    
    // Shows update button explicitly
    public void showUpdateButton() {
        btnUpdate.setVisible(true);
    }
    
    // Shows all action buttons (admin view)
    public void showAllButtons() {
        btnAdd.setVisible(true);
        btnUpdate.setVisible(true);
        btnDelete.setVisible(true);
    }
    
    // Updates view title based on user role
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    // Sets next available staff ID for new entries
    public void setNextId(String id) {
        if (lblAutoId != null) {
            lblAutoId.setText(id);
        }
    }

    // Populates table with staff list data
    public void showStaff(List<Staff> list) {
        tableModel.setRowCount(0);
        for (Staff s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getFirstName(), s.getLastName(),
                    s.getPosition(), s.getDepartment(), s.getFacilityId(),
                    s.getPhone(), s.getEmail(), s.getEmploymentStatus(),
                    s.getStartDate(), s.getLineManager(), s.getAccessLevel()
            });
        }
    }

    // Handles addition of new staff record
    private void onAdd() {
        if (controller == null) return;
        
        if (!validateForm()) {
            return;
        }

        Staff staff = new Staff(
                lblAutoId.getText(),
                txtFirstName.getText() + " " + txtLastName.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtPosition.getText(),
                txtFacilityId.getText()
        );
        
        // Set additional staff attributes
        staff.setDepartment(txtDepartment.getText());
        staff.setEmploymentStatus(txtEmploymentStatus.getText());
        staff.setStartDate(txtStartDate.getText());
        staff.setLineManager(txtLineManager.getText());
        staff.setAccessLevel(txtAccessLevel.getText());

        controller.addStaff(staff);
        clearForm();
    }
    
    // Handles updating of existing staff record
    private void onUpdate() {
        if (controller == null) return;
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a staff member to update.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        Staff staff = new Staff(
                lblAutoId.getText(),
                txtFirstName.getText() + " " + txtLastName.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtPosition.getText(),
                txtFacilityId.getText()
        );
        
        // Set additional staff attributes
        staff.setDepartment(txtDepartment.getText());
        staff.setEmploymentStatus(txtEmploymentStatus.getText());
        staff.setStartDate(txtStartDate.getText());
        staff.setLineManager(txtLineManager.getText());
        staff.setAccessLevel(txtAccessLevel.getText());
        
        controller.updateStaff(staff);
    }

    // Handles deletion of selected staff record
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member first!");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete staff " + id + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteById(id);
            clearForm();
        }
    }
    
    // Loads selected table row data into form fields
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        
        lblAutoId.setText(tableModel.getValueAt(row, 0).toString());
        String fullName = getValue(row, 1) + " " + getValue(row, 2);
        String[] nameParts = fullName.split(" ", 2);
        txtFirstName.setText(nameParts.length > 0 ? nameParts[0] : "");
        txtLastName.setText(nameParts.length > 1 ? nameParts[1] : "");
        txtPosition.setText(getValue(row, 3));
        txtDepartment.setText(getValue(row, 4));
        txtFacilityId.setText(getValue(row, 5));
        txtPhone.setText(getValue(row, 6));
        txtEmail.setText(getValue(row, 7));
        txtEmploymentStatus.setText(getValue(row, 8));
        txtStartDate.setText(getValue(row, 9));
        txtLineManager.setText(getValue(row, 10));
        txtAccessLevel.setText(getValue(row, 11));
    }
    
    // Safely retrieves table cell values
    private String getValue(int row, int col) {
        Object value = tableModel.getValueAt(row, col);
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
        if (txtPosition.getText().trim().isEmpty()) {
            errors.append("- Position is required\n");
        }
        if (txtDepartment.getText().trim().isEmpty()) {
            errors.append("- Department is required\n");
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
        txtPosition.setText("");
        txtDepartment.setText("");
        txtFacilityId.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtEmploymentStatus.setText("");
        txtStartDate.setText("");
        txtLineManager.setText("");
        txtAccessLevel.setText("");
    }
}