package view;

    import controller.PatientController;
    import model.Patient;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.util.List;
    

    public class PatientView extends JPanel {

        private PatientController controller;

        private JTable table;
        private DefaultTableModel tableModel;

        // Form components
        private JLabel lblAutoId;
        private JTextField txtFirstName, txtLastName, txtDob, txtNhs, txtGender;
        private JTextField txtPhone, txtEmail;
        private JTextField txtAddress, txtPostcode;
        private JTextField txtEmergencyName, txtEmergencyPhone;
        private JTextField txtRegistrationDate, txtGpSurgery;

        // Button references
        private JButton btnAdd;
        private JButton btnUpdate;
        private JButton btnDelete;
        private JPanel buttonsPanel;

        // Title label
        private JLabel titleLabel;

        public PatientView() {

            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // ============================================================
            // TITLE PANEL
            // ============================================================
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            titleLabel = new JLabel("Patient Management");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titlePanel.add(titleLabel);
            add(titlePanel, BorderLayout.NORTH);

            // ============================================================
            // TABLE (BOTTOM)
            // ============================================================
            tableModel = new DefaultTableModel(
                    new Object[]{
                            "ID", "First Name", "Last Name", "DOB", "NHS",
                            "Gender", "Phone", "Email", "Address", "Postcode",
                            "Emergency Name", "Emergency Phone",
                            "Registration Date", "GP Surgery ID"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            table = new JTable(tableModel);
            table.setRowHeight(22);
            JScrollPane tableScrollPane = new JScrollPane(table);
            tableScrollPane.setPreferredSize(new Dimension(800, 200));
            add(tableScrollPane, BorderLayout.SOUTH);

            // ============================================================
            // FORM (CENTER) — 4 columns using GridBagLayout
            // ============================================================
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Patient Information"));
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(10, 15, 10, 15);
            gc.fill = GridBagConstraints.HORIZONTAL;

            // Create fields
            lblAutoId = new JLabel("P001");
            lblAutoId.setFont(new Font("SansSerif", Font.BOLD, 12));

            txtFirstName = new JTextField();
            txtLastName = new JTextField();
            txtDob = new JTextField();
            txtNhs = new JTextField();
            txtGender = new JTextField();
            txtPhone = new JTextField();
            txtEmail = new JTextField();
            txtAddress = new JTextField();
            txtPostcode = new JTextField();
            txtEmergencyName = new JTextField();
            txtEmergencyPhone = new JTextField();
            txtRegistrationDate = new JTextField();
            txtGpSurgery = new JTextField();

            // Row counter
            int row = 0;

            // ============================================================
            // ADD FORM ROWS (4 columns each)
            // ============================================================
            add4(form, gc, row++, "Patient ID:", lblAutoId, "First Name:", txtFirstName);
            add4(form, gc, row++, "Last Name:", txtLastName, "DOB (YYYY-MM-DD):", txtDob);
            add4(form, gc, row++, "NHS Number:", txtNhs, "Gender (M/F):", txtGender);
            add4(form, gc, row++, "Phone Number:", txtPhone, "Email:", txtEmail);
            add4(form, gc, row++, "Address:", txtAddress, "Postcode:", txtPostcode);
            add4(form, gc, row++, "Emergency Name:", txtEmergencyName,
                    "Emergency Phone:", txtEmergencyPhone);
            add4(form, gc, row++, "Registration Date:", txtRegistrationDate,
                    "GP Surgery ID:", txtGpSurgery);

            JScrollPane formScrollPane = new JScrollPane(form);
            formScrollPane.setPreferredSize(new Dimension(800, 300));
            add(formScrollPane, BorderLayout.CENTER);

            // ============================================================
            // BUTTONS (EAST - Vertical layout)
            // ============================================================
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
            buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

            btnAdd = new JButton("Add Patient");
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
            // TABLE SELECTION LISTENER
            // ============================================================
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedRowIntoForm();
                }
            });
        }

        // =================================================================
        // Helper — Adds one ROW with 4 columns
        // =================================================================
        private void add4(JPanel panel, GridBagConstraints gc, int row,
                          String label1, JComponent field1,
                          String label2, JComponent field2) {

            gc.gridy = row;

            // Left label
            gc.gridx = 0; gc.weightx = 0.15;
            panel.add(new JLabel(label1), gc);

            // Left field
            gc.gridx = 1; gc.weightx = 0.35;
            panel.add(field1, gc);

            // Right label
            gc.gridx = 2; gc.weightx = 0.15;
            panel.add(new JLabel(label2), gc);

            // Right field
            gc.gridx = 3; gc.weightx = 0.35;
            panel.add(field2, gc);
        }

        // ============================================================
        // CONTROLLER LINK
        // ============================================================
        public void setController(PatientController controller) {
            this.controller = controller;
        }

        // ============================================================
        // NEW METHODS NEEDED BY CONTROLLER
        // ============================================================

        public void setReadOnlyMode(boolean readOnly) {
            // Enable/disable all input fields
            txtFirstName.setEditable(!readOnly);
            txtLastName.setEditable(!readOnly);
            txtDob.setEditable(!readOnly);
            txtNhs.setEditable(!readOnly);
            txtGender.setEditable(!readOnly);
            txtPhone.setEditable(!readOnly);
            txtEmail.setEditable(!readOnly);
            txtAddress.setEditable(!readOnly);
            txtPostcode.setEditable(!readOnly);
            txtEmergencyName.setEditable(!readOnly);
            txtEmergencyPhone.setEditable(!readOnly);
            txtRegistrationDate.setEditable(!readOnly);
            txtGpSurgery.setEditable(!readOnly);
        }
        public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }

        public void hideAddDeleteButtons() {
            btnAdd.setVisible(false);
            btnDelete.setVisible(false);
            btnUpdate.setVisible(true); // Keep update button visible
        }

        public void showUpdateButton() {
            btnUpdate.setVisible(true);
        }

        public void showAllButtons() {
            btnAdd.setVisible(true);
            btnUpdate.setVisible(true);
            btnDelete.setVisible(true);
        }

        public void setTitle(String title) {
            if (titleLabel != null) {
                titleLabel.setText(title);
            }
        }

        public void setNextId(String id) {
            if (lblAutoId != null) {
                lblAutoId.setText(id);
            }
        }

        // ============================================================
        // SHOW PATIENTS
        // ============================================================
        public void showPatients(List<Patient> list) {
            tableModel.setRowCount(0);

            // Don't auto-update ID when showing list (let controller handle it)
            for (Patient p : list) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getFirstName(), p.getLastName(),
                        p.getDateOfBirth(), p.getNhsNumber(), p.getGender(),
                        p.getPhoneNumber(), p.getEmail(), p.getAddress(),
                        p.getPostcode(), p.getEmergencyContactName(),
                        p.getEmergencyContactPhone(), p.getRegistrationDate(),
                        p.getGpSurgeryId()
                });
            }
        }

        // ============================================================
        // ADD PATIENT
        // ============================================================
        private void onAdd() {
            if (controller == null) return;

            // Validate required fields
            if (!validateForm()) {
                return;
            }

            Patient p = new Patient(
                    lblAutoId.getText(),
                    txtFirstName.getText(),
                    txtLastName.getText(),
                    txtDob.getText(),
                    txtNhs.getText(),
                    txtGender.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtAddress.getText(),
                    txtPostcode.getText(),
                    txtEmergencyName.getText(),
                    txtEmergencyPhone.getText(),
                    txtRegistrationDate.getText(),
                    txtGpSurgery.getText()
            );

            controller.addPatient(p);
            clearForm();
        }

        // ============================================================
        // UPDATE PATIENT
        // ============================================================
        private void onUpdate() {
            if (controller == null) return;

            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a patient to update.",
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate form
            if (!validateForm()) {
                return;
            }

            Patient p = new Patient(
                    lblAutoId.getText(),
                    txtFirstName.getText(),
                    txtLastName.getText(),
                    txtDob.getText(),
                    txtNhs.getText(),
                    txtGender.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtAddress.getText(),
                    txtPostcode.getText(),
                    txtEmergencyName.getText(),
                    txtEmergencyPhone.getText(),
                    txtRegistrationDate.getText(),
                    txtGpSurgery.getText()
            );

            controller.updatePatient(p);
        }

        // ============================================================
        // DELETE PATIENT
        // ============================================================
        private void onDelete() {
            if (controller == null) return;

            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a patient first!");
                return;
            }

            String id = tableModel.getValueAt(row, 0).toString();

            // Ask for confirmation
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteById(id);
                clearForm();
            }
        }

        // ============================================================
        // LOAD SELECTED ROW INTO FORM
        // ============================================================
        private void loadSelectedRowIntoForm() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            lblAutoId.setText(tableModel.getValueAt(row, 0).toString());
            txtFirstName.setText(getValue(row, 1));
            txtLastName.setText(getValue(row, 2));
            txtDob.setText(getValue(row, 3));
            txtNhs.setText(getValue(row, 4));
            txtGender.setText(getValue(row, 5));
            txtPhone.setText(getValue(row, 6));
            txtEmail.setText(getValue(row, 7));
            txtAddress.setText(getValue(row, 8));
            txtPostcode.setText(getValue(row, 9));
            txtEmergencyName.setText(getValue(row, 10));
            txtEmergencyPhone.setText(getValue(row, 11));
            txtRegistrationDate.setText(getValue(row, 12));
            txtGpSurgery.setText(getValue(row, 13));
        }

        private String getValue(int row, int col) {
            Object value = tableModel.getValueAt(row, col);
            return value == null ? "" : value.toString();
        }

        // ============================================================
        // VALIDATION
        // ============================================================
        private boolean validateForm() {
            StringBuilder errors = new StringBuilder();

            if (txtFirstName.getText().trim().isEmpty()) {
                errors.append("- First Name is required\n");
            }
            if (txtLastName.getText().trim().isEmpty()) {
                errors.append("- Last Name is required\n");
            }
            if (txtDob.getText().trim().isEmpty()) {
                errors.append("- Date of Birth is required\n");
            }
            if (txtNhs.getText().trim().isEmpty()) {
                errors.append("- NHS Number is required\n");
            }
            if (txtGender.getText().trim().isEmpty()) {
                errors.append("- Gender is required\n");
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
        // CLEAR FORM
        // ============================================================
        private void clearForm() {
            txtFirstName.setText("");
            txtLastName.setText("");
            txtDob.setText("");
            txtNhs.setText("");
            txtGender.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtAddress.setText("");
            txtPostcode.setText("");
            txtEmergencyName.setText("");
            txtEmergencyPhone.setText("");
            txtRegistrationDate.setText("");
            txtGpSurgery.setText("");

            // Don't clear the ID - let controller set it
        }
    }