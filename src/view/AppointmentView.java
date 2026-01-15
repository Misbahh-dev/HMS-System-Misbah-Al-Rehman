package view;

import controller.AppointmentController;
import model.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentView extends JPanel {

    private AppointmentController controller;

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtId, txtTime, txtDuration, txtType;
    private JTextField txtReason, txtLastModified;

    private JComboBox<String> cbStatus;
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbClinicianId;
    private JComboBox<String> cbFacilityId;

    private JTextArea txtNotes;
    
    private JSpinner dateSpinner;
    private JSpinner createdDateSpinner;
    
    private JLabel titleLabel;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JPanel buttonsPanel;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AppointmentView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Clinician", "Facility",
                        "Date", "Time", "Duration (min)", "Type",
                        "Status", "Reason", "Notes", "Created", "Last Modified"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
//Made By Misbah Al Rehman. SRN: 24173647
        table = new JTable(model);
        table.setRowHeight(23);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(850, 220));
        add(tableScrollPane, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Appointment Details"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 12));

        cbPatientId = new JComboBox<>();
        cbClinicianId = new JComboBox<>();
        cbFacilityId = new JComboBox<>();
        cbPatientId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbClinicianId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbFacilityId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbPatientId.setPreferredSize(new Dimension(180, 28));
        cbClinicianId.setPreferredSize(new Dimension(180, 28));
        cbFacilityId.setPreferredSize(new Dimension(180, 28));

        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateSpinner.setPreferredSize(new Dimension(140, 28));
        
        createdDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        createdDateSpinner.setEditor(new JSpinner.DateEditor(createdDateSpinner, "yyyy-MM-dd"));
        createdDateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        createdDateSpinner.setPreferredSize(new Dimension(140, 28));
        createdDateSpinner.setEnabled(false);

        txtTime = new JTextField(12);
        txtDuration = new JTextField(12);
        txtType = new JTextField(12);
        txtReason = new JTextField(12);
        
        txtTime.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDuration.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtType.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtReason.setFont(new Font("SansSerif", Font.PLAIN, 12));

        cbStatus = new JComboBox<>(new String[]{
                "SCHEDULED",
                "RESCHEDULED",
                "CANCELLED",
                "COMPLETED",
                "NO-SHOW"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbStatus.setPreferredSize(new Dimension(180, 28));

        txtNotes = new JTextArea(4, 20);
        txtNotes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        txtLastModified = new JTextField(12);
        txtLastModified.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtLastModified.setEditable(false);

        int row = 0;
        addFieldPair(form, gc, row++, "Appointment ID:", txtId, "Patient ID:", cbPatientId);
        addFieldPair(form, gc, row++, "Clinician ID:", cbClinicianId, "Facility ID:", cbFacilityId);
        addFieldPair(form, gc, row++, "Appointment Date:", dateSpinner, "Time (HH:mm):", txtTime);
        addFieldPair(form, gc, row++, "Duration (min):", txtDuration, "Appointment Type:", txtType);
        addFieldPair(form, gc, row++, "Status:", cbStatus, "Reason for Visit:", txtReason);
        addFieldPair(form, gc, row++, "Created Date:", createdDateSpinner, "Last Modified:", txtLastModified);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        form.add(notesLabel, gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 3;
        gc.weightx = 1.0;
        gc.weighty = 0.8;
        gc.fill = GridBagConstraints.BOTH;
        JScrollPane notesScrollPane = new JScrollPane(txtNotes);
        notesScrollPane.setPreferredSize(new Dimension(550, 80));
        form.add(notesScrollPane, gc);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setPreferredSize(new Dimension(850, 350));
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(formScrollPane, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Actions"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        btnAdd = new JButton("Add Appointment");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Delete Selected");
        
        Font buttonFont = new Font("SansSerif", Font.BOLD, 13);
        btnAdd.setFont(buttonFont);
        btnUpdate.setFont(buttonFont);
        btnDelete.setFont(buttonFont);
        
        Dimension buttonSize = new Dimension(170, 38);
        btnAdd.setPreferredSize(buttonSize);
        btnAdd.setMaximumSize(buttonSize);
        btnAdd.setMinimumSize(buttonSize);
        btnUpdate.setPreferredSize(buttonSize);
        btnUpdate.setMaximumSize(buttonSize);
        btnUpdate.setMinimumSize(buttonSize);
        btnDelete.setPreferredSize(buttonSize);
        btnDelete.setMaximumSize(buttonSize);
        btnDelete.setMinimumSize(buttonSize);
        
        btnAdd.addActionListener(e -> addAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnDelete.addActionListener(e -> deleteAppointment());
        
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(Box.createVerticalStrut(15));
        
        add(buttonsPanel, BorderLayout.EAST);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });
    }

    private void addFieldPair(JPanel panel, GridBagConstraints gc, int row,
                              String l1, JComponent f1,
                              String l2, JComponent f2) {

        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.5;

        gc.gridx = 0; gc.gridy = row;
        JLabel label1 = new JLabel(l1);
        label1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(label1, gc);

        gc.gridx = 1;
        panel.add(f1, gc);

        gc.gridx = 2;
        JLabel label2 = new JLabel(l2);
        label2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(label2, gc);

        gc.gridx = 3;
        panel.add(f2, gc);
    }

    public void loadDropdowns(List<String> patients, List<String> clinicians, List<String> facilities) {
        cbPatientId.removeAllItems();
        cbClinicianId.removeAllItems();
        cbFacilityId.removeAllItems();

        for (String s : patients) cbPatientId.addItem(s);
        for (String s : clinicians) cbClinicianId.addItem(s);
        for (String s : facilities) cbFacilityId.addItem(s);

        txtId.setText(controller.generateId());
        
        createdDateSpinner.setValue(new Date());
        txtLastModified.setText(dateFormat.format(new Date()));
    }

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

    private void addAppointment() {
        if (controller == null) return;
        
        if (!validateForm()) {
            return;
        }

        Appointment a = new Appointment(
                txtId.getText(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbFacilityId.getSelectedItem(),
                dateFormat.format(dateSpinner.getValue()),
                txtTime.getText(),
                txtDuration.getText(),
                txtType.getText(),
                (String) cbStatus.getSelectedItem(),
                txtReason.getText(),
                txtNotes.getText(),
                dateFormat.format(createdDateSpinner.getValue()),
                txtLastModified.getText()
        );

        controller.addAppointment(a);
        clearForm();
    }
    
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
        
        if (!validateForm()) {
            return;
        }
        
        Appointment a = new Appointment(
                txtId.getText(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbFacilityId.getSelectedItem(),
                dateFormat.format(dateSpinner.getValue()),
                txtTime.getText(),
                txtDuration.getText(),
                txtType.getText(),
                (String) cbStatus.getSelectedItem(),
                txtReason.getText(),
                txtNotes.getText(),
                dateFormat.format(createdDateSpinner.getValue()),
                dateFormat.format(new Date())
        );
        
        controller.updateAppointment(a);
    }

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
    
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        
        txtId.setText(getValue(row, 0));
        cbPatientId.setSelectedItem(getValue(row, 1));
        cbClinicianId.setSelectedItem(getValue(row, 2));
        cbFacilityId.setSelectedItem(getValue(row, 3));
        
        String dateStr = getValue(row, 4);
        if (!dateStr.isEmpty()) {
            try {
                Date apptDate = dateFormat.parse(dateStr);
                dateSpinner.setValue(apptDate);
            } catch (ParseException e) {
                System.err.println("Failed to parse appointment date: " + dateStr);
            }
        }
        
        txtTime.setText(getValue(row, 5));
        txtDuration.setText(getValue(row, 6));
        txtType.setText(getValue(row, 7));
        cbStatus.setSelectedItem(getValue(row, 8));
        txtReason.setText(getValue(row, 9));
        txtNotes.setText(getValue(row, 10));
        
        String createdDateStr = getValue(row, 11);
        if (!createdDateStr.isEmpty()) {
            try {
                Date createdDate = dateFormat.parse(createdDateStr);
                createdDateSpinner.setValue(createdDate);
            } catch (ParseException e) {
                System.err.println("Failed to parse created date: " + createdDateStr);
            }
        }
        
        txtLastModified.setText(getValue(row, 12));
    }
    
    private String getValue(int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }
    
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
    
    private void clearForm() {
        dateSpinner.setValue(new Date());
        
        txtTime.setText("");
        txtDuration.setText("");
        txtType.setText("");
        txtReason.setText("");
        txtNotes.setText("");
        txtLastModified.setText(dateFormat.format(new Date()));
        
        if (controller != null) {
            txtId.setText(controller.generateId());
        }
    }

    public void setController(AppointmentController controller) {
        this.controller = controller;
    }
    
    public void setReadOnlyMode(boolean readOnly) {
        cbPatientId.setEnabled(!readOnly);
        cbClinicianId.setEnabled(!readOnly);
        cbFacilityId.setEnabled(!readOnly);
        cbStatus.setEnabled(!readOnly);
        
        dateSpinner.setEnabled(!readOnly);
        createdDateSpinner.setEnabled(!readOnly);
        
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
        if (btnUpdate != null) btnUpdate.setVisible(true);
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
    
    public void hideAllButtons() {
        if (btnAdd != null) btnAdd.setVisible(false);
        if (btnUpdate != null) btnUpdate.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
    }
    
    public void hideUpdateButton() {
     if (btnUpdate != null) btnUpdate.setVisible(false);
     if (btnAdd != null) btnAdd.setVisible(true);
     if (btnDelete != null) btnDelete.setVisible(true);
     
    }
}